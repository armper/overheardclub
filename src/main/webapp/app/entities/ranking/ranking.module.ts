import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OverheardclubSharedModule } from 'app/shared/shared.module';
import { RankingComponent } from './ranking.component';
import { RankingDetailComponent } from './ranking-detail.component';
import { RankingUpdateComponent } from './ranking-update.component';
import { RankingDeleteDialogComponent } from './ranking-delete-dialog.component';
import { rankingRoute } from './ranking.route';

@NgModule({
  imports: [OverheardclubSharedModule, RouterModule.forChild(rankingRoute)],
  declarations: [RankingComponent, RankingDetailComponent, RankingUpdateComponent, RankingDeleteDialogComponent],
  entryComponents: [RankingDeleteDialogComponent]
})
export class OverheardclubRankingModule {}
