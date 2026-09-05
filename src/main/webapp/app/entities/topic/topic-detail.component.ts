import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiParseLinks } from 'ng-jhipster';

import { Account } from 'app/core/user/account.model';
import { ITopic } from 'app/shared/model/topic.model';
import { AccountService } from 'app/core/auth/account.service';
import { PostService } from '../post/post.service';
import { IPost } from 'app/shared/model/post.model';
import { OverheardCommentService } from 'app/entities/overheard-comment/overheard-comment.service';
import { IOverheardComment } from 'app/shared/model/overheard-comment.model';

const PAGE_SIZE = 10;

@Component({
  selector: 'jhi-topic-detail',
  templateUrl: './topic-detail.component.html'
})
export class TopicDetailComponent implements OnInit, OnDestroy {
  topic: ITopic | null = null;
  account: Account | null = null;
  posts: IPost[] = [];
  commentCounts: { [postId: number]: number } = {};
  page = 0;
  hasMore = false;
  loading = false;
  loadingMore = false;
  authSubscription?: Subscription;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    private postService: PostService,
    private commentService: OverheardCommentService,
    private parseLinks: JhiParseLinks
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.topic = topic;
      this.page = 0;
      this.posts = [];
      this.loading = true;
      this.fetch();
    });
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  loadMore(): void {
    this.page++;
    this.loadingMore = true;
    this.fetch();
  }

  trackId(index: number, item: IPost): number {
    return item.id!;
  }

  isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }

  canEdit(): boolean {
    if (!this.topic || !this.account) {
      return false;
    }
    return this.isAdmin() || (!!this.topic.user && this.topic.user.id === this.account.id);
  }

  previousState(): void {
    window.history.back();
  }

  private fetch(): void {
    if (!this.topic || !this.topic.id) {
      this.loading = false;
      return;
    }
    this.postService
      .query({
        page: this.page,
        size: PAGE_SIZE,
        sort: ['date,desc', 'id,desc'],
        'topicId.equals': this.topic.id
      })
      .subscribe(
        (res: HttpResponse<IPost[]>) => {
          const batch = res.body || [];
          this.posts = this.posts.concat(batch);
          const links = this.parseLinks.parse(res.headers.get('link') || '');
          this.hasMore = links.next !== undefined && batch.length === PAGE_SIZE;
          this.loading = false;
          this.loadingMore = false;
          this.loadCommentCounts(batch);
        },
        () => {
          this.loading = false;
          this.loadingMore = false;
        }
      );
  }

  private loadCommentCounts(posts: IPost[]): void {
    const ids = posts.map(p => p.id!).filter(id => id !== undefined);
    if (ids.length === 0) {
      return;
    }
    ids.forEach(id => (this.commentCounts[id] = this.commentCounts[id] || 0));
    this.commentService.query({ 'postId.in': ids }).subscribe((res: HttpResponse<IOverheardComment[]>) => {
      (res.body || []).forEach(c => {
        const postId = c.post && c.post.id;
        if (postId !== undefined) {
          this.commentCounts[postId] = (this.commentCounts[postId] || 0) + 1;
        }
      });
    });
  }
}
