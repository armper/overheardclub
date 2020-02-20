import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPost } from 'app/shared/model/post.model';
import { AccountService } from 'app/core/auth/account.service';
import { Subscription } from 'rxjs';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html'
})
export class PostDetailComponent implements OnInit {
  post: IPost | null = null;
  authSubscription!: Subscription;
  account: Account | null = null;

  constructor(protected activatedRoute: ActivatedRoute, private accountService: AccountService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => (this.post = post));
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  previousState(): void {
    window.history.back();
  }

  private isAdmin(): boolean | undefined {
    // return this.account?.authorities.has('ROLE_ADMIN');
    return true;
  }
}
