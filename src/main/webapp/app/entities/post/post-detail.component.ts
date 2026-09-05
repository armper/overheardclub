import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import * as moment from 'moment';

import { IPost } from 'app/shared/model/post.model';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { OverheardCommentService } from '../overheard-comment/overheard-comment.service';
import { IOverheardComment, OverheardComment } from 'app/shared/model/overheard-comment.model';
import { PostDeleteDialogComponent } from './post-delete-dialog.component';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html'
})
export class PostDetailComponent implements OnInit, OnDestroy {
  post!: IPost;
  account: Account | null = null;
  comments: IOverheardComment[] = [];
  savingComment = false;
  authSubscription?: Subscription;

  commentForm = this.fb.group({
    content: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(4096)]]
  });

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected overheardCommentService: OverheardCommentService,
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
    this.overheardCommentService
      .query({ 'postId.equals': this.post.id, sort: ['date,asc', 'id,asc'] })
      .subscribe(res => (this.comments = res.body || []));
  }

  isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }

  isOwner(): boolean {
    return !!this.account && !!this.post.user && this.post.user.id === this.account.id;
  }

  canEdit(): boolean {
    return this.isOwner() || this.isAdmin();
  }

  canRemove(comment: IOverheardComment): boolean {
    return this.isAdmin() || (!!this.account && !!comment.user && comment.user.id === this.account.id);
  }

  authorName(comment: IOverheardComment): string {
    const user = comment.user;
    return user ? user.firstName || user.login || 'Someone' : 'Someone';
  }

  initial(comment: IOverheardComment): string {
    return this.authorName(comment).charAt(0);
  }

  commentLength(): number {
    const value = this.commentForm.get('content')!.value;
    return value ? value.length : 0;
  }

  trackId(index: number, item: IOverheardComment): number {
    return item.id!;
  }

  addComment(): void {
    if (this.commentForm.invalid || !this.account || !this.post.id) {
      return;
    }
    this.savingComment = true;
    const comment: IOverheardComment = {
      ...new OverheardComment(),
      content: this.commentForm.get('content')!.value,
      date: moment(),
      ranking: 0,
      user: { id: this.account.id },
      post: { id: this.post.id }
    };
    this.overheardCommentService.create(comment).subscribe(
      () => {
        this.savingComment = false;
        this.commentForm.reset();
        this.loadComments();
      },
      () => (this.savingComment = false)
    );
  }

  removeComment(comment: IOverheardComment): void {
    if (!comment.id) {
      return;
    }
    this.overheardCommentService.delete(comment.id).subscribe(() => {
      this.comments = this.comments.filter(c => c.id !== comment.id);
    });
  }

  delete(): void {
    const modalRef = this.modalService.open(PostDeleteDialogComponent, { backdrop: 'static' });
    modalRef.componentInstance.post = this.post;
    modalRef.result.then(
      () => this.router.navigate(this.post.topic ? ['/topic', this.post.topic.id, 'view'] : ['/']),
      () => {}
    );
  }

  previousState(): void {
    window.history.back();
  }
}
