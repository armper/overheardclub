import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiParseLinks } from 'ng-jhipster';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IPost } from 'app/shared/model/post.model';
import { ITopic } from 'app/shared/model/topic.model';
import { PostService } from 'app/entities/post/post.service';
import { TopicService } from 'app/entities/topic/topic.service';

export type FeedMode = 'top' | 'new' | 'mine' | 'search';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  readonly pageSize = 10;

  account: Account | null = null;
  authSubscription?: Subscription;

  mode: FeedMode = 'top';
  posts: IPost[] = [];
  topics: ITopic[] = [];
  page = 0;
  hasMore = false;
  loading = false;
  searchQuery = '';
  activeSearch = '';

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private postService: PostService,
    private topicService: TopicService,
    private parseLinks: JhiParseLinks
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      if (!account && this.mode === 'mine') {
        this.setMode('top');
      }
    });
    this.topicService.query().subscribe(res => {
      this.topics = (res.body || []).sort((a, b) => (a.title || '').localeCompare(b.title || ''));
    });
    this.load();
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  setMode(mode: FeedMode): void {
    if (mode !== 'search') {
      this.activeSearch = '';
      this.searchQuery = '';
    }
    this.mode = mode;
    this.page = 0;
    this.posts = [];
    this.load();
  }

  search(): void {
    const query = (this.searchQuery || '').trim();
    if (!query) {
      this.setMode('top');
      return;
    }
    this.activeSearch = query;
    this.setMode('search');
  }

  clearSearch(): void {
    this.setMode('top');
  }

  loadMore(): void {
    this.page++;
    this.load();
  }

  trackId(index: number, item: IPost): number | undefined {
    return item.id;
  }

  private load(): void {
    this.loading = true;
    const paging = { page: this.page, size: this.pageSize };
    let request;
    switch (this.mode) {
      case 'top':
        request = this.postService.top({ ...paging, days: 7 });
        break;
      case 'search':
        request = this.postService.search({ ...paging, q: this.activeSearch });
        break;
      case 'mine':
        request = this.postService.query({ ...paging, sort: ['date,desc', 'id,desc'], 'userId.equals': this.accountId() });
        break;
      default:
        request = this.postService.query({ ...paging, sort: ['date,desc', 'id,desc'] });
    }
    const mode = this.mode;
    request.subscribe(
      (res: HttpResponse<IPost[]>) => {
        if (mode !== this.mode) {
          return;
        }
        this.append(res.body, res.headers);
        this.loading = false;
      },
      () => (this.loading = false)
    );
  }

  private accountId(): number | undefined {
    return this.account ? (this.account as any).id : undefined;
  }

  private append(data: IPost[] | null, headers: HttpHeaders): void {
    const link = headers.get('link');
    const links = link ? this.parseLinks.parse(link) : { last: 0 };
    this.hasMore = links.last !== undefined && this.page < links.last;
    (data || []).forEach(post => {
      if (!this.posts.some(existing => existing.id === post.id)) {
        this.posts.push(post);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
}
