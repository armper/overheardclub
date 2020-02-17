import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IRank } from 'app/shared/model/rank.model';
import { RankService } from './rank.service';

@Component({
  templateUrl: './rank-delete-dialog.component.html'
})
export class RankDeleteDialogComponent {
  rank?: IRank;

  constructor(protected rankService: RankService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rankService.delete(id).subscribe(() => {
      this.eventManager.broadcast('rankListModification');
      this.activeModal.close();
    });
  }
}
