import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { Subscription, forkJoin, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITopic, Topic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { TopicDeleteDialogComponent } from './topic-delete-dialog.component';
import { PostService } from 'app/entities/post/post.service';
import { AccountService } from 'app/core/auth/account.service';

@Component({
  selector: 'jhi-topic',
  templateUrl: './topic.component.html'
})
export class TopicComponent implements OnInit, OnDestroy {
  topics?: ITopic[];
  counts: { [topicId: number]: number } = {};
  eventSubscriber?: Subscription;
  showCreate = false;
  saving = false;

  createForm = this.fb.group({
    title: [null, [Validators.required, Validators.minLength(2), Validators.maxLength(80)]]
  });

  constructor(
    protected topicService: TopicService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected postService: PostService,
    protected accountService: AccountService,
    private fb: FormBuilder
  ) {}

  loadAll(): void {
    this.topicService.query({ sort: ['title,asc'] }).subscribe((res: HttpResponse<ITopic[]>) => {
      this.topics = res.body || [];
      this.loadCounts(this.topics);
    });
  }

  private loadCounts(topics: ITopic[]): void {
    if (topics.length === 0) {
      return;
    }
    const requests = topics.map(t => (t.id ? this.postService.count({ 'topicId.equals': t.id }) : of(null)));
    forkJoin(requests).subscribe(results => {
      results.forEach((res, i) => {
        const id = topics[i].id;
        if (id !== undefined && res && res.body !== null) {
          this.counts[id] = res.body;
        }
      });
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInTopics();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ITopic): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInTopics(): void {
    this.eventSubscriber = this.eventManager.subscribe('topicListModification', () => this.loadAll());
  }

  toggleCreate(): void {
    this.showCreate = !this.showCreate;
    if (!this.showCreate) {
      this.createForm.reset();
    }
  }

  create(): void {
    const account = this.accountService.getAccount();
    if (this.createForm.invalid || !account || !account.id) {
      return;
    }
    this.saving = true;
    const topic: ITopic = { ...new Topic(), title: this.createForm.get('title')!.value.trim(), user: { id: account.id } };
    this.topicService.create(topic).subscribe(
      () => {
        this.saving = false;
        this.showCreate = false;
        this.createForm.reset();
        this.loadAll();
      },
      () => (this.saving = false)
    );
  }

  delete(topic: ITopic): void {
    const modalRef = this.modalService.open(TopicDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.topic = topic;
  }
}
