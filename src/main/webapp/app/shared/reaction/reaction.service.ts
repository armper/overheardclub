import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';
import { AccountService } from 'app/core/auth/account.service';

export type ReactionKey = 'rankOne' | 'rankTwo' | 'rankThree' | 'rankFour' | 'rankFive';

export interface Reaction {
  key: ReactionKey;
  emoji: string;
  label: string;
}

export const REACTIONS: Reaction[] = [
  { key: 'rankOne', emoji: '👍', label: 'Nice' },
  { key: 'rankTwo', emoji: '❤️', label: 'Love it' },
  { key: 'rankThree', emoji: '😂', label: 'Funny' },
  { key: 'rankFour', emoji: '🤯', label: 'Mind blown' },
  { key: 'rankFive', emoji: '🙄', label: 'Eye roll' }
];

/**
 * One reaction per user per post. The set of posts the current user already
 * reacted to is loaded once and kept in memory so cards can render their state
 * without a round trip.
 */
@Injectable({ providedIn: 'root' })
export class ReactionService {
  private reacted = new Set<number>();
  private loadedForUser: number | null = null;

  constructor(private postService: PostService, private accountService: AccountService) {
    this.accountService.getAuthenticationState().subscribe(account => {
      if (!account || account.id !== this.loadedForUser) {
        this.reacted = new Set<number>();
        this.loadedForUser = null;
      }
    });
  }

  /** Ensure the reacted-post ids for the current user are loaded. */
  loadMine(): Observable<Set<number>> {
    const userId = this.currentUserId();
    if (userId === null) {
      return of(this.reacted);
    }
    if (this.loadedForUser === userId) {
      return of(this.reacted);
    }
    return this.postService.query({ 'userUprankId.equals': userId, size: 1000, sort: ['id,desc'] }).pipe(
      map(res => res.body || []),
      tap(posts => {
        this.reacted = new Set<number>(posts.map(p => p.id!));
        this.loadedForUser = userId;
      }),
      map(() => this.reacted)
    );
  }

  hasReacted(postId?: number): boolean {
    return postId !== undefined && this.reacted.has(postId);
  }

  total(post: IPost): number {
    return REACTIONS.reduce((sum, r) => sum + (post[r.key] || 0), 0);
  }

  /**
   * Record a reaction. Re-reads the post first so the counters and the
   * uprank list are current, then saves once. Emits the updated post, or the
   * unchanged one if the user had already reacted.
   */
  react(post: IPost, key: ReactionKey): Observable<IPost> {
    const userId = this.currentUserId();
    if (userId === null || !post.id) {
      return of(post);
    }
    return this.postService.find(post.id).pipe(
      switchMap(res => {
        const fresh = res.body!;
        const upranks = fresh.userUpranks || [];
        if (upranks.some(u => u.id === userId)) {
          this.reacted.add(fresh.id!);
          return of(fresh);
        }
        fresh[key] = (fresh[key] || 0) + 1;
        fresh.userUpranks = [...upranks, { id: userId }];
        return this.postService.update(fresh).pipe(
          map(updated => updated.body || fresh),
          tap(() => this.reacted.add(fresh.id!))
        );
      })
    );
  }

  private currentUserId(): number | null {
    const account = this.accountService.getAccount();
    return account && account.id ? account.id : null;
  }
}
