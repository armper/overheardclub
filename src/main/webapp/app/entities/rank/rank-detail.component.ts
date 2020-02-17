import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRank } from 'app/shared/model/rank.model';

@Component({
  selector: 'jhi-rank-detail',
  templateUrl: './rank-detail.component.html'
})
export class RankDetailComponent implements OnInit {
  rank: IRank | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rank }) => (this.rank = rank));
  }

  previousState(): void {
    window.history.back();
  }
}
