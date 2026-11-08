import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITopic } from 'app/shared/model/topic.model';
import { TopicService } from './topic.service';
import { TopicDeleteDialogComponent } from './topic-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

@Component({
  selector: 'jhi-topic',
  templateUrl: './topic.component.html'
})
export class TopicComponent implements OnInit, OnDestroy {
  topics?: ITopic[];
  eventSubscriber?: Subscription;
  authSubscription?: Subscription;
  account: Account | null = null;
  filter = '';

  constructor(
    protected topicService: TopicService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected accountService: AccountService
  ) {}

  loadAll(): void {
    this.topicService
      .query()
      .subscribe(
        (res: HttpResponse<ITopic[]>) => (this.topics = (res.body || []).sort((a, b) => (a.title || '').localeCompare(b.title || '')))
      );
  }

  get filteredTopics(): ITopic[] {
    const filter = this.filter.trim().toLowerCase();
    return (this.topics || []).filter(topic => !filter || (topic.title || '').toLowerCase().includes(filter));
  }

  canModify(topic: ITopic): boolean {
    return !!this.account && (this.account.authorities.includes('ROLE_ADMIN') || (!!topic.user && topic.user.login === this.account.login));
  }

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.loadAll();
    this.registerChangeInTopics();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
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
