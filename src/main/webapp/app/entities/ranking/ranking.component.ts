import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRanking } from 'app/shared/model/ranking.model';
import { RankingService } from './ranking.service';
import { RankingDeleteDialogComponent } from './ranking-delete-dialog.component';

@Component({
  selector: 'jhi-ranking',
  templateUrl: './ranking.component.html'
})
export class RankingComponent implements OnInit, OnDestroy {
  rankings?: IRanking[];
  eventSubscriber?: Subscription;

  constructor(protected rankingService: RankingService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.rankingService.query().subscribe((res: HttpResponse<IRanking[]>) => (this.rankings = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInRankings();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IRanking): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInRankings(): void {
    this.eventSubscriber = this.eventManager.subscribe('rankingListModification', () => this.loadAll());
  }

  delete(ranking: IRanking): void {
    const modalRef = this.modalService.open(RankingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ranking = ranking;
  }
}
