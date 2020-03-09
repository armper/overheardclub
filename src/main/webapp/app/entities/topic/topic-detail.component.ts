import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Account } from 'app/core/user/account.model';
import { Subscription } from 'rxjs';

import { ITopic } from 'app/shared/model/topic.model';
import { AccountService } from 'app/core/auth/account.service';
import { PostService } from '../post/post.service';
import { IPost } from 'app/shared/model/post.model';

@Component({
  selector: 'jhi-topic-detail',
  templateUrl: './topic-detail.component.html'
})
export class TopicDetailComponent implements OnInit {
  topic: ITopic | null = null;
  postSubscription!: Subscription;
  account!: Account;
  posts: IPost[] = [];

  constructor(protected activatedRoute: ActivatedRoute, protected accountService: AccountService, private postService: PostService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => (this.topic = topic));
    this.accountService.getAuthenticationState().subscribe(account => (this.account = account!));
    this.postSubscription = this.postService
      .query({
        page: '0',
        size: '10',
        sort: ['id,desc'],
        'topicId.equals': this.topic!.id
      })
      .subscribe(posts => (this.posts = posts.body!));
  }

  previousState(): void {
    window.history.back();
  }

  public isAdmin(): boolean | undefined {
    return this.account.authorities.includes('ROLE_ADMIN');
  }
}
