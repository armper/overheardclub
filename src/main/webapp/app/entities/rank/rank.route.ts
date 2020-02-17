import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IRank, Rank } from 'app/shared/model/rank.model';
import { RankService } from './rank.service';
import { RankComponent } from './rank.component';
import { RankDetailComponent } from './rank-detail.component';
import { RankUpdateComponent } from './rank-update.component';

@Injectable({ providedIn: 'root' })
export class RankResolve implements Resolve<IRank> {
  constructor(private service: RankService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRank> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((rank: HttpResponse<Rank>) => {
          if (rank.body) {
            return of(rank.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Rank());
  }
}

export const rankRoute: Routes = [
  {
    path: '',
    component: RankComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Ranks'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: RankDetailComponent,
    resolve: {
      rank: RankResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Ranks'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: RankUpdateComponent,
    resolve: {
      rank: RankResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Ranks'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: RankUpdateComponent,
    resolve: {
      rank: RankResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Ranks'
    },
    canActivate: [UserRouteAccessService]
  }
];
