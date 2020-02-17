import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITopic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { TopicDeleteDialogComponent } from './topic-delete-dialog.component';

@Component({
  selector: 'jhi-topic',
  templateUrl: './topic.component.html'
})
export class TopicComponent implements OnInit, OnDestroy {
  topics?: ITopic[];
  eventSubscriber?: Subscription;

  constructor(protected topicService: TopicService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.topicService.query().subscribe((res: HttpResponse<ITopic[]>) => (this.topics = res.body || []));
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

  delete(topic: ITopic): void {
    const modalRef = this.modalService.open(TopicDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.topic = topic;
  }
}
