import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OverheardclubSharedModule } from 'app/shared/shared.module';
import { RankComponent } from './rank.component';
import { RankDetailComponent } from './rank-detail.component';
import { RankUpdateComponent } from './rank-update.component';
import { RankDeleteDialogComponent } from './rank-delete-dialog.component';
import { rankRoute } from './rank.route';

@NgModule({
  imports: [OverheardclubSharedModule, RouterModule.forChild(rankRoute)],
  declarations: [RankComponent, RankDetailComponent, RankUpdateComponent, RankDeleteDialogComponent],
  entryComponents: [RankDeleteDialogComponent]
})
export class OverheardclubRankModule {}
