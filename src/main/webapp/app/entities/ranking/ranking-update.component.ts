import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IRanking, Ranking } from 'app/shared/model/ranking.model';
import { RankingService } from './ranking.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post/post.service';

type SelectableEntity = IUser | IPost;

@Component({
  selector: 'jhi-ranking-update',
  templateUrl: './ranking-update.component.html'
})
export class RankingUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  posts: IPost[] = [];

  editForm = this.fb.group({
    id: [],
    rankType: [],
    date: [],
    user: [],
    post: []
  });

  constructor(
    protected rankingService: RankingService,
    protected userService: UserService,
    protected postService: PostService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ranking }) => {
      if (!ranking.id) {
        const today = moment().startOf('day');
        ranking.date = today;
      }

      this.updateForm(ranking);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.postService.query().subscribe((res: HttpResponse<IPost[]>) => (this.posts = res.body || []));
    });
  }

  updateForm(ranking: IRanking): void {
    this.editForm.patchValue({
      id: ranking.id,
      rankType: ranking.rankType,
      date: ranking.date ? ranking.date.format(DATE_TIME_FORMAT) : null,
      user: ranking.user,
      post: ranking.post
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ranking = this.createFromForm();
    if (ranking.id !== undefined) {
      this.subscribeToSaveResponse(this.rankingService.update(ranking));
    } else {
      this.subscribeToSaveResponse(this.rankingService.create(ranking));
    }
  }

  private createFromForm(): IRanking {
    return {
      ...new Ranking(),
      id: this.editForm.get(['id'])!.value,
      rankType: this.editForm.get(['rankType'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value,
      post: this.editForm.get(['post'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRanking>>): void {
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
