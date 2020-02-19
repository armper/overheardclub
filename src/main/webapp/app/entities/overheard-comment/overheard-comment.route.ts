import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IOverheardComment, OverheardComment } from 'app/shared/model/overheard-comment.model';
import { OverheardCommentService } from './overheard-comment.service';
import { OverheardCommentComponent } from './overheard-comment.component';
import { OverheardCommentDetailComponent } from './overheard-comment-detail.component';
import { OverheardCommentUpdateComponent } from './overheard-comment-update.component';

@Injectable({ providedIn: 'root' })
export class OverheardCommentResolve implements Resolve<IOverheardComment> {
  constructor(private service: OverheardCommentService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOverheardComment> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((overheardComment: HttpResponse<OverheardComment>) => {
          if (overheardComment.body) {
            return of(overheardComment.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new OverheardComment());
  }
}

export const overheardCommentRoute: Routes = [
  {
    path: '',
    component: OverheardCommentComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OverheardComments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: OverheardCommentDetailComponent,
    resolve: {
      overheardComment: OverheardCommentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OverheardComments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: OverheardCommentUpdateComponent,
    resolve: {
      overheardComment: OverheardCommentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OverheardComments'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: OverheardCommentUpdateComponent,
    resolve: {
      overheardComment: OverheardCommentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OverheardComments'
    },
    canActivate: [UserRouteAccessService]
  }
];
