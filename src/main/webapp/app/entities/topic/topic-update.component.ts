import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { ITopic, Topic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'jhi-topic-update',
  templateUrl: './topic-update.component.html'
})
export class TopicUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  account: Account | null = null;
  original: ITopic = new Topic();

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(80)]],
    user: []
  });

  constructor(
    protected topicService: TopicService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected accountService: AccountService,
    protected router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      if (this.isAdmin() && this.users.length === 0) {
        this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
      }
    });
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.original = topic;
      this.updateForm(topic);
    });
  }

  updateForm(topic: ITopic): void {
    this.editForm.patchValue({
      id: topic.id,
      title: topic.title,
      user: topic.user
    });
  }

  isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const topic = this.createFromForm();
    if (topic.id !== undefined && topic.id !== null) {
      this.subscribeToSaveResponse(this.topicService.update(topic));
    } else {
      this.subscribeToSaveResponse(this.topicService.create(topic));
    }
  }

  private createFromForm(): ITopic {
    const id = this.editForm.get(['id'])!.value;
    const chosenUser = this.isAdmin() ? this.editForm.get(['user'])!.value : null;
    const owner = chosenUser || this.original.user || (this.account && this.account.id ? { id: this.account.id } : undefined);
    return {
      ...new Topic(),
      id: id === null ? undefined : id,
      title: this.editForm.get(['title'])!.value,
      user: owner
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITopic>>): void {
    result.subscribe(
      res => this.onSaveSuccess(res.body),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(saved: ITopic | null): void {
    this.isSaving = false;
    if (saved && saved.id) {
      this.router.navigate(['/topic', saved.id, 'view']);
    } else {
      this.previousState();
    }
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
