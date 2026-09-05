import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';

import { IPost } from 'app/shared/model/post.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { ReactionService, REACTIONS, Reaction, ReactionKey } from 'app/shared/reaction/reaction.service';

@Component({
  selector: 'jhi-post-card',
  templateUrl: './post-card.component.html'
})
export class PostCardComponent implements OnInit {
  @Input() post!: IPost;
  /** Render the full content instead of a short excerpt. */
  @Input() full = false;
  /** Number of comments, when the parent knows it. */
  @Input() commentCount?: number;
  /** Hide the topic chip (e.g. when already inside that topic). */
  @Input() hideTopic = false;
  @Output() reacted = new EventEmitter<IPost>();

  reactions: Reaction[] = REACTIONS;
  busy = false;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private reactionService: ReactionService
  ) {}

  ngOnInit(): void {
    this.reactionService.loadMine().subscribe();
  }

  authorName(): string {
    const user = this.post.user;
    if (!user) {
      return 'Someone';
    }
    return user.firstName || user.login || 'Someone';
  }

  initial(): string {
    return this.authorName().charAt(0);
  }

  excerpt(): string {
    const text = this.post.content || '';
    if (this.full || text.length <= 220) {
      return text;
    }
    return text.slice(0, 220).replace(/\s+\S*$/, '') + '…';
  }

  hasReacted(): boolean {
    return this.reactionService.hasReacted(this.post.id);
  }

  count(key: ReactionKey): number {
    return this.post[key] || 0;
  }

  total(): number {
    return this.reactionService.total(this.post);
  }

  react(key: ReactionKey): void {
    if (!this.accountService.isAuthenticated()) {
      this.loginModalService.open();
      return;
    }
    if (this.busy || this.hasReacted()) {
      return;
    }
    this.busy = true;
    this.reactionService.react(this.post, key).subscribe(
      updated => {
        this.reactions.forEach(r => (this.post[r.key] = updated[r.key]));
        this.busy = false;
        this.reacted.emit(this.post);
      },
      () => (this.busy = false)
    );
  }
}
