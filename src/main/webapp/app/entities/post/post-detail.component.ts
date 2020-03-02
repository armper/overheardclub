import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPost } from 'app/shared/model/post.model';
import { Subscription } from 'rxjs';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { OverheardCommentService } from '../overheard-comment/overheard-comment.service';
import { IOverheardComment } from 'app/shared/model/overheard-comment.model';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html'
})
export class PostDetailComponent implements OnInit {
  post!: IPost;
  authSubscription!: Subscription;
  account!: Account;
  comments!: IOverheardComment[];

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected overheardCommentService: OverheardCommentService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      this.post = post;
      this.overheardCommentService.query({ 'postId.equals': this.post.id }).subscribe(comments => (this.comments = comments.body!));
    });

    this.accountService.getAuthenticationState().subscribe(account => (this.account = account!));
  }

  public isAdmin(): boolean | undefined {
    return this.account.authorities.includes('ROLE_ADMIN');
  }

  previousState(): void {
    window.history.back();
  }
}
