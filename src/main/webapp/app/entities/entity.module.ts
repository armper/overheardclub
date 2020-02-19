import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'overheard-comment',
        loadChildren: () => import('./overheard-comment/overheard-comment.module').then(m => m.OverheardclubOverheardCommentModule)
      },
      {
        path: 'post',
        loadChildren: () => import('./post/post.module').then(m => m.OverheardclubPostModule)
      },
      {
        path: 'topic',
        loadChildren: () => import('./topic/topic.module').then(m => m.OverheardclubTopicModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class OverheardclubEntityModule {}
