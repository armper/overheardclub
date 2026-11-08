import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { JhiParseLinks } from 'ng-jhipster';
import { Subscription } from 'rxjs';

import { Account } from 'app/core/user/account.model';
import { ITopic } from 'app/shared/model/topic.model';
import { AccountService } from 'app/core/auth/account.service';
import { PostService } from '../post/post.service';
import { IPost } from 'app/shared/model/post.model';

@Component({
  selector: 'jhi-topic-detail',
  templateUrl: './topic-detail.component.html'
})
export class TopicDetailComponent implements OnInit, OnDestroy {
  readonly pageSize = 10;

  topic: ITopic | null = null;
  account: Account | null = null;
  posts: IPost[] = [];
  sort: 'new' | 'top' = 'new';
  page = 0;
  hasMore = false;
  loading = false;
  authSubscription?: Subscription;

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    private postService: PostService,
    private parseLinks: JhiParseLinks
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.topic = topic;
      this.reset();
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  setSort(sort: 'new' | 'top'): void {
    this.sort = sort;
    this.reset();
  }

  reset(): void {
    this.page = 0;
    this.posts = [];
    this.load();
  }

  loadMore(): void {
    this.page++;
    this.load();
  }

  load(): void {
    if (!this.topic || !this.topic.id) {
      return;
    }
    this.loading = true;
    const sort = this.sort === 'top' ? ['rankThree,desc', 'rankOne,desc', 'date,desc'] : ['date,desc', 'id,desc'];
    this.postService
      .query({
        page: this.page,
        size: this.pageSize,
        sort,
        'topicId.equals': this.topic.id
      })
      .subscribe(
        (res: HttpResponse<IPost[]>) => {
          this.append(res.body, res.headers);
          this.loading = false;
        },
        () => (this.loading = false)
      );
  }

  trackId(index: number, item: IPost): number | undefined {
    return item.id;
  }

  previousState(): void {
    window.history.back();
  }

  public isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }

  canModify(): boolean {
    return !!this.account && (this.isAdmin() || (!!this.topic && !!this.topic.user && this.topic.user.login === this.account.login));
  }

  private append(data: IPost[] | null, headers: HttpHeaders): void {
    const link = headers.get('link');
    const links = link ? this.parseLinks.parse(link) : { last: 0 };
    this.hasMore = links.last !== undefined && this.page < links.last;
    (data || []).forEach(post => this.posts.push(post));
  }
}
