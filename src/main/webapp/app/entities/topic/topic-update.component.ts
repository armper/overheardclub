import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ITopic, Topic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-topic-update',
  templateUrl: './topic-update.component.html'
})
export class TopicUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(80)]],
    user: [null, Validators.required]
  });

  constructor(
    protected topicService: TopicService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.updateForm(topic);

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(topic: ITopic): void {
    this.editForm.patchValue({
      id: topic.id,
      title: topic.title,
      user: topic.user
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const topic = this.createFromForm();
    if (topic.id !== undefined) {
      this.subscribeToSaveResponse(this.topicService.update(topic));
    } else {
      this.subscribeToSaveResponse(this.topicService.create(topic));
    }
  }

  private createFromForm(): ITopic {
    return {
      ...new Topic(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      user: this.editForm.get(['user'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITopic>>): void {
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
