import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IRank, Rank } from 'app/shared/model/rank.model';
import { RankService } from './rank.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-rank-update',
  templateUrl: './rank-update.component.html'
})
export class RankUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    rankType: [],
    date: [],
    user: []
  });

  constructor(
    protected rankService: RankService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rank }) => {
      if (!rank.id) {
        const today = moment().startOf('day');
        rank.date = today;
      }

      this.updateForm(rank);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(rank: IRank): void {
    this.editForm.patchValue({
      id: rank.id,
      rankType: rank.rankType,
      date: rank.date ? rank.date.format(DATE_TIME_FORMAT) : null,
      user: rank.user
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rank = this.createFromForm();
    if (rank.id !== undefined) {
      this.subscribeToSaveResponse(this.rankService.update(rank));
    } else {
      this.subscribeToSaveResponse(this.rankService.create(rank));
    }
  }

  private createFromForm(): IRank {
    return {
      ...new Rank(),
      id: this.editForm.get(['id'])!.value,
      rankType: this.editForm.get(['rankType'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      user: this.editForm.get(['user'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRank>>): void {
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

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
