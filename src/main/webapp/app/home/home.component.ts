import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiParseLinks } from 'ng-jhipster';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';
import { OverheardCommentService } from 'app/entities/overheard-comment/overheard-comment.service';
import { IOverheardComment } from 'app/shared/model/overheard-comment.model';

type FeedMode = 'top' | 'latest';

const PAGE_SIZE = 10;

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;

  mode: FeedMode = 'top';
  posts: IPost[] = [];
  commentCounts: { [postId: number]: number } = {};
  page = 0;
  hasMore = false;
  loading = false;
  loadingMore = false;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private postService: PostService,
    private commentService: OverheardCommentService,
    private parseLinks: JhiParseLinks
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
      const wasSignedIn = this.account !== null;
      this.account = account;
      if (account && !wasSignedIn) {
        this.reload();
      }
      if (!account) {
        this.posts = [];
      }
    });
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  setMode(mode: FeedMode): void {
    if (this.mode !== mode) {
      this.mode = mode;
      this.reload();
    }
  }

  reload(): void {
    this.page = 0;
    this.posts = [];
    this.loading = true;
    this.fetch();
  }

  loadMore(): void {
    this.page++;
    this.loadingMore = true;
    this.fetch();
  }

  trackId(index: number, item: IPost): number {
    return item.id!;
  }

  private fetch(): void {
    const req: any = { page: this.page, size: PAGE_SIZE };
    if (this.mode === 'top') {
      const since = new Date();
      since.setDate(since.getDate() - 7);
      req.sort = ['rankOne,desc', 'rankTwo,desc', 'rankThree,desc', 'id,desc'];
      req['date.greaterThan'] = since.toISOString();
    } else {
      req.sort = ['date,desc', 'id,desc'];
    }

    this.postService.query(req).subscribe(
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

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
