import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import * as moment from 'moment';

import { IPost } from 'app/shared/model/post.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { OverheardCommentService } from '../overheard-comment/overheard-comment.service';
import { IOverheardComment, OverheardComment } from 'app/shared/model/overheard-comment.model';
import { authorName } from 'app/shared/model/reaction.model';
import { PostDeleteDialogComponent } from './post-delete-dialog.component';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html'
})
export class PostDetailComponent implements OnInit, OnDestroy {
  post!: IPost;
  account: Account | null = null;
  comments: IOverheardComment[] = [];
  isSavingComment = false;
  commentError: string | null = null;
  linkCopied = false;
  authSubscription?: Subscription;

  commentForm = this.fb.group({
    content: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(4096)]]
  });

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected overheardCommentService: OverheardCommentService,
    protected loginModalService: LoginModalService,
    protected modalService: NgbModal,
    protected router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      this.post = post;
      this.loadComments();
    });

    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  loadComments(): void {
    if (!this.post || !this.post.id) {
      return;
    }
    this.overheardCommentService.query({ 'postId.equals': this.post.id }).subscribe(res => {
      this.comments = (res.body || []).sort((a, b) => (a.date && b.date ? a.date.valueOf() - b.date.valueOf() : (a.id || 0) - (b.id || 0)));
    });
  }

  isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }

  canModify(owner?: { login?: string }): boolean {
    return !!this.account && (this.isAdmin() || (!!owner && owner.login === this.account.login));
  }

  authorOf(comment: IOverheardComment): string {
    return authorName(comment);
  }

  login(): void {
    this.loginModalService.open();
  }

  addComment(): void {
    if (this.commentForm.invalid || !this.account) {
      return;
    }
    this.isSavingComment = true;
    this.commentError = null;
    const comment: IOverheardComment = {
      ...new OverheardComment(),
      content: this.commentForm.get('content')!.value.trim(),
      date: moment(),
      ranking: 0,
      user: this.account as any,
      post: { id: this.post.id }
    };
    this.overheardCommentService.create(comment).subscribe(
      res => {
        this.isSavingComment = false;
        this.commentForm.reset();
        if (res.body) {
          this.comments.push(res.body);
        }
      },
      () => {
        this.isSavingComment = false;
        this.commentError = 'Your comment could not be saved, please try again.';
      }
    );
  }

  deleteComment(comment: IOverheardComment): void {
    if (!comment.id || !confirm('Delete this comment?')) {
      return;
    }
    this.overheardCommentService.delete(comment.id).subscribe(() => {
      this.comments = this.comments.filter(c => c.id !== comment.id);
    });
  }

  deletePost(): void {
    const modalRef = this.modalService.open(PostDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.post = this.post;
    modalRef.result.then(
      () => this.router.navigate(['/']),
      () => {}
    );
  }

  copyLink(): void {
    const url = window.location.href;
    const done = (): void => {
      this.linkCopied = true;
      setTimeout(() => (this.linkCopied = false), 2000);
    };
    if (navigator && (navigator as any).clipboard) {
      (navigator as any).clipboard.writeText(url).then(done, () => window.prompt('Copy this link:', url));
    } else {
      window.prompt('Copy this link:', url);
    }
  }

  previousState(): void {
    window.history.back();
  }
}
