import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';
import { UserService } from 'app/core/user/user.service';
import { IUser } from 'app/core/user/user.model';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription!: Subscription;
  topFunnyPosts!: IPost[];
  postSubscription!: Subscription;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private postService: PostService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    const date = new Date();
    date.setDate(date.getDate() - 7);

    this.postSubscription = this.postService
      .query({
        page: '0',
        size: '5',
        sort: ['rankOne,desc'],
        'date.greaterThan': date.toISOString()
      })
      .subscribe(posts => (this.topFunnyPosts = posts.body!));
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  upRank(post: IPost, rank: number): void {
    // Only logged in users can upvote
    if (!this.account) {
      return;
    }

    this.userService.find(this.account.login).subscribe(user => {
      this.postService.query({ 'userUprankId.in': user.id }).subscribe(uprankedPosts => {
        if (uprankedPosts.body!.length === 0) {
          this.postService.find(post.id!).subscribe(pp => {
            post.userUpranks = pp.body!.userUpranks;

            switch (rank) {
              case 1:
                post.rankOne!++;
                break;
              case 2:
                post.rankTwo!++;
                break;
              case 3:
                post.rankThree!++;
                break;
              case 4:
                post.rankFour!++;
                break;
              case 5:
                post.rankFive!++;
                break;
            }

            post.userUpranks!.push(user);
            this.postService.update(post).subscribe(updatePost => {
              updatePost.body;
              // eslint-disable-next-line no-console
              console.log(updatePost.body);
            });
          });
        } else {
          const urps: IPost[] = uprankedPosts.body!.filter(urp => urp.id === post.id);

          if (urps.length === 0) {
            this.postService.find(post.id!).subscribe(pp => {
              post.userUpranks = pp.body!.userUpranks;

              switch (rank) {
                case 1:
                  post.rankOne!++;
                  break;
                case 2:
                  post.rankTwo!++;
                  break;
                case 3:
                  post.rankThree!++;
                  break;
                case 4:
                  post.rankFour!++;
                  break;
                case 5:
                  post.rankFive!++;
                  break;
              }

              post.userUpranks!.push(user);
              this.postService.update(post).subscribe(updatePost => {
                updatePost.body;
                // eslint-disable-next-line no-console
                console.log(updatePost.body);
              });
            });
          }
        }
      });
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }

    if (this.postSubscription) {
      this.postSubscription.unsubscribe();
    }
  }
}
