import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOverheardComment } from 'app/shared/model/overheard-comment.model';

@Component({
  selector: 'jhi-overheard-comment-detail',
  templateUrl: './overheard-comment-detail.component.html'
})
export class OverheardCommentDetailComponent implements OnInit {
  overheardComment: IOverheardComment | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ overheardComment }) => (this.overheardComment = overheardComment));
  }

  previousState(): void {
    window.history.back();
  }
}
