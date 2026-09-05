import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPost, Post } from 'app/shared/model/post.model';
import { PostService } from './post.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITopic } from 'app/shared/model/topic.model';
import { TopicService } from 'app/entities/topic/topic.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';
import { REACTIONS, Reaction } from 'app/shared/reaction/reaction.service';

type SelectableEntity = IUser | ITopic;

@Component({
  selector: 'jhi-post-update',
  templateUrl: './post-update.component.html'
})
export class PostUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  topics: ITopic[] = [];
  reactions: Reaction[] = REACTIONS;
  account: Account | null = null;
  /** The post as loaded, so an edit keeps reactions and metadata intact. */
  original: IPost = new Post();

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(80)]],
    content: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(4096)]],
    date: [],
    rankOne: [],
    rankTwo: [],
    rankThree: [],
    rankFour: [],
    rankFive: [],
    user: [],
    topic: [null, Validators.required],
    userUpranks: []
  });

  constructor(
    protected postService: PostService,
    protected userService: UserService,
    protected topicService: TopicService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    private fb: FormBuilder,
    protected accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      if (this.isAdmin() && this.users.length === 0) {
        this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
      }
    });

    this.activatedRoute.data.subscribe(({ post }) => {
      this.original = post;
      if (!post.id) {
        post.date = moment();
      }
      this.updateForm(post);

      this.topicService.query({ sort: ['title,asc'] }).subscribe((res: HttpResponse<ITopic[]>) => {
        this.topics = res.body || [];
        this.preselectTopic();
      });
    });
  }

  /** `/post/new?topic=<id>` pre-selects the topic the user came from. */
  private preselectTopic(): void {
    if (this.editForm.get('topic')!.value) {
      return;
    }
    const topicId = Number(this.activatedRoute.snapshot.queryParamMap.get('topic'));
    if (!topicId) {
      return;
    }
    const match = this.topics.find(t => t.id === topicId);
    if (match) {
      this.editForm.patchValue({ topic: match });
    }
  }

  updateForm(post: IPost): void {
    this.editForm.patchValue({
      id: post.id,
      title: post.title,
      content: post.content,
      date: post.date ? post.date.format(DATE_TIME_FORMAT) : null,
      rankOne: post.rankOne,
      rankTwo: post.rankTwo,
      rankThree: post.rankThree,
      rankFour: post.rankFour,
      rankFive: post.rankFive,
      user: post.user,
      topic: post.topic,
      userUpranks: post.userUpranks
    });
  }

  isNew(): boolean {
    return !this.editForm.get('id')!.value;
  }

  length(field: string): number {
    const value = this.editForm.get(field)!.value;
    return value ? value.length : 0;
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const post = this.createFromForm();
    if (post.id !== undefined && post.id !== null) {
      this.subscribeToSaveResponse(this.postService.update(post));
    } else {
      this.subscribeToSaveResponse(this.postService.create(post));
    }
  }

  private createFromForm(): IPost {
    const id = this.editForm.get(['id'])!.value;
    const base: IPost = {
      ...new Post(),
      ...this.original,
      id: id === null ? undefined : id,
      title: this.editForm.get(['title'])!.value,
      content: this.editForm.get(['content'])!.value,
      topic: this.editForm.get(['topic'])!.value
    };

    if (this.isAdmin()) {
      return {
        ...base,
        date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : base.date,
        rankOne: this.editForm.get(['rankOne'])!.value || 0,
        rankTwo: this.editForm.get(['rankTwo'])!.value || 0,
        rankThree: this.editForm.get(['rankThree'])!.value || 0,
        rankFour: this.editForm.get(['rankFour'])!.value || 0,
        rankFive: this.editForm.get(['rankFive'])!.value || 0,
        user: this.editForm.get(['user'])!.value || base.user || this.currentUserRef(),
        userUpranks: this.editForm.get(['userUpranks'])!.value || []
      };
    }

    if (this.isNew()) {
      return {
        ...base,
        date: moment(),
        rankOne: 0,
        rankTwo: 0,
        rankThree: 0,
        rankFour: 0,
        rankFive: 0,
        user: this.currentUserRef(),
        userUpranks: []
      };
    }

    // Members editing their own post: only the text and topic change.
    return {
      ...base,
      rankOne: base.rankOne || 0,
      rankTwo: base.rankTwo || 0,
      rankThree: base.rankThree || 0,
      rankFour: base.rankFour || 0,
      rankFive: base.rankFive || 0,
      userUpranks: base.userUpranks || []
    };
  }

  private currentUserRef(): IUser | undefined {
    return this.account && this.account.id ? { id: this.account.id } : undefined;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPost>>): void {
    result.subscribe(
      res => this.onSaveSuccess(res.body),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(saved: IPost | null): void {
    this.isSaving = false;
    if (saved && saved.id) {
      this.router.navigate(['/post', saved.id, 'view']);
    } else {
      this.previousState();
    }
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  getSelected(selectedVals: IUser[], option: IUser): IUser {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }

  public isAdmin(): boolean {
    return !!this.account && this.account.authorities.includes('ROLE_ADMIN');
  }
}
