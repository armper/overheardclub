import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPost, Post } from 'app/shared/model/post.model';
import { PostService } from './post.service';
import { IRank } from 'app/shared/model/rank.model';
import { RankService } from 'app/entities/rank/rank.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITopic } from 'app/shared/model/topic.model';
import { TopicService } from 'app/entities/topic/topic.service';

type SelectableEntity = IRank | IUser | ITopic;

@Component({
  selector: 'jhi-post-update',
  templateUrl: './post-update.component.html'
})
export class PostUpdateComponent implements OnInit {
  isSaving = false;
  ranks: IRank[] = [];
  users: IUser[] = [];
  topics: ITopic[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    content: [],
    date: [],
    rank: [],
    user: [],
    topic: []
  });

  constructor(
    protected postService: PostService,
    protected rankService: RankService,
    protected userService: UserService,
    protected topicService: TopicService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => {
      if (!post.id) {
        const today = moment().startOf('day');
        post.date = today;
      }

      this.updateForm(post);

      this.rankService.query().subscribe((res: HttpResponse<IRank[]>) => (this.ranks = res.body || []));

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

      this.topicService.query().subscribe((res: HttpResponse<ITopic[]>) => (this.topics = res.body || []));
    });
  }

  updateForm(post: IPost): void {
    this.editForm.patchValue({
      id: post.id,
      title: post.title,
      content: post.content,
      date: post.date ? post.date.format(DATE_TIME_FORMAT) : null,
      rank: post.rank,
      user: post.user,
      topic: post.topic
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const post = this.createFromForm();
    if (post.id !== undefined) {
      this.subscribeToSaveResponse(this.postService.update(post));
    } else {
      this.subscribeToSaveResponse(this.postService.create(post));
    }
  }

  private createFromForm(): IPost {
    return {
      ...new Post(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      content: this.editForm.get(['content'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      rank: this.editForm.get(['rank'])!.value,
      user: this.editForm.get(['user'])!.value,
      topic: this.editForm.get(['topic'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPost>>): void {
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
