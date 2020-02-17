import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRank } from 'app/shared/model/rank.model';
import { RankService } from './rank.service';
import { RankDeleteDialogComponent } from './rank-delete-dialog.component';

@Component({
  selector: 'jhi-rank',
  templateUrl: './rank.component.html'
})
export class RankComponent implements OnInit, OnDestroy {
  ranks?: IRank[];
  eventSubscriber?: Subscription;

  constructor(protected rankService: RankService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.rankService.query().subscribe((res: HttpResponse<IRank[]>) => (this.ranks = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInRanks();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IRank): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInRanks(): void {
    this.eventSubscriber = this.eventManager.subscribe('rankListModification', () => this.loadAll());
  }

  delete(rank: IRank): void {
    const modalRef = this.modalService.open(RankDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.rank = rank;
  }
}
