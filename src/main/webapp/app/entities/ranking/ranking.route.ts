import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IRanking, Ranking } from 'app/shared/model/ranking.model';
import { RankingService } from './ranking.service';
import { RankingComponent } from './ranking.component';
import { RankingDetailComponent } from './ranking-detail.component';
import { RankingUpdateComponent } from './ranking-update.component';

@Injectable({ providedIn: 'root' })
export class RankingResolve implements Resolve<IRanking> {
  constructor(private service: RankingService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRanking> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((ranking: HttpResponse<Ranking>) => {
          if (ranking.body) {
            return of(ranking.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ranking());
  }
}

export const rankingRoute: Routes = [
  {
    path: '',
    component: RankingComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Rankings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: RankingDetailComponent,
    resolve: {
      ranking: RankingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Rankings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: RankingUpdateComponent,
    resolve: {
      ranking: RankingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Rankings'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: RankingUpdateComponent,
    resolve: {
      ranking: RankingResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Rankings'
    },
    canActivate: [UserRouteAccessService]
  }
];
