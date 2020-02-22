import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IOverheardComment, OverheardComment } from 'app/shared/model/overheard-comment.model';
import { OverheardCommentService } from './overheard-comment.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';

type SelectableEntity = IUser | IPost;

@Component({
  selector: 'jhi-overheard-comment-update',
  templateUrl: './overheard-comment-update.component.html'
})
export class OverheardCommentUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  posts: IPost[] = [];

  editForm = this.fb.group({
    id: [],
    content: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(4096)]],
    date: [null, [Validators.required]],
    ranking: [],
    user: [null, Validators.required],
    post: [null, Validators.required]
  });

  constructor(
    protected overheardCommentService: OverheardCommentService,
    protected userService: UserService,
    protected postService: PostService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ overheardComment }) => {
      if (!overheardComment.id) {
        const today = moment().startOf('day');
        overheardComment.date = today;
      }

      this.updateForm(overheardComment);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.postService.query().subscribe((res: HttpResponse<IPost[]>) => (this.posts = res.body || []));
    });
  }

  updateForm(overheardComment: IOverheardComment): void {
    this.editForm.patchValue({
      id: overheardComment.id,
      content: overheardComment.content,
      date: overheardComment.date ? overheardComment.date.format(DATE_TIME_FORMAT) : null,
      ranking: overheardComment.ranking,
      user: overheardComment.user,
      post: overheardComment.post
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const overheardComment = this.createFromForm();
    if (overheardComment.id !== undefined) {
      this.subscribeToSaveResponse(this.overheardCommentService.update(overheardComment));
    } else {
      this.subscribeToSaveResponse(this.overheardCommentService.create(overheardComment));
    }
  }

  private createFromForm(): IOverheardComment {
    return {
      ...new OverheardComment(),
      id: this.editForm.get(['id'])!.value,
      content: this.editForm.get(['content'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      ranking: this.editForm.get(['ranking'])!.value,
      user: this.editForm.get(['user'])!.value,
      post: this.editForm.get(['post'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOverheardComment>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
