import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'topic',
        loadChildren: () => import('./topic/topic.module').then(m => m.OverheardclubTopicModule)
      },
      {
        path: 'post',
        loadChildren: () => import('./post/post.module').then(m => m.OverheardclubPostModule)
      },
      {
        path: 'comment',
        loadChildren: () => import('./comment/comment.module').then(m => m.OverheardclubCommentModule)
      },
      {
        path: 'rank',
        loadChildren: () => import('./rank/rank.module').then(m => m.OverheardclubRankModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class OverheardclubEntityModule {}
