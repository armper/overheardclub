import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IOverheardComment } from 'app/shared/model/overheard-comment.model';
import { OverheardCommentService } from './overheard-comment.service';

@Component({
  templateUrl: './overheard-comment-delete-dialog.component.html'
})
export class OverheardCommentDeleteDialogComponent {
  overheardComment?: IOverheardComment;

  constructor(
    protected overheardCommentService: OverheardCommentService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.overheardCommentService.delete(id).subscribe(() => {
      this.eventManager.broadcast('overheardCommentListModification');
      this.activeModal.close();
    });
  }
}
