import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OverheardclubSharedModule } from 'app/shared/shared.module';
import { OverheardCommentComponent } from './overheard-comment.component';
import { OverheardCommentDetailComponent } from './overheard-comment-detail.component';
import { OverheardCommentUpdateComponent } from './overheard-comment-update.component';
import { OverheardCommentDeleteDialogComponent } from './overheard-comment-delete-dialog.component';
import { overheardCommentRoute } from './overheard-comment.route';

@NgModule({
  imports: [OverheardclubSharedModule, RouterModule.forChild(overheardCommentRoute)],
  declarations: [
    OverheardCommentComponent,
    OverheardCommentDetailComponent,
    OverheardCommentUpdateComponent,
    OverheardCommentDeleteDialogComponent
  ],
  entryComponents: [OverheardCommentDeleteDialogComponent]
})
export class OverheardclubOverheardCommentModule {}
