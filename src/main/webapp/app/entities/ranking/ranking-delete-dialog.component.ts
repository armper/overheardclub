import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IRanking } from 'app/shared/model/ranking.model';
import { RankingService } from './ranking.service';

@Component({
  templateUrl: './ranking-delete-dialog.component.html'
})
export class RankingDeleteDialogComponent {
  ranking?: IRanking;

  constructor(protected rankingService: RankingService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rankingService.delete(id).subscribe(() => {
      this.eventManager.broadcast('rankingListModification');
      this.activeModal.close();
    });
  }
}
