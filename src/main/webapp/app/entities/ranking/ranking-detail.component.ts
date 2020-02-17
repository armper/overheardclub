import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRanking } from 'app/shared/model/ranking.model';

@Component({
  selector: 'jhi-ranking-detail',
  templateUrl: './ranking-detail.component.html'
})
export class RankingDetailComponent implements OnInit {
  ranking: IRanking | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ranking }) => (this.ranking = ranking));
  }

  previousState(): void {
    window.history.back();
  }
}
