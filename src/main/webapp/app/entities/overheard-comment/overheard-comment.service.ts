import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IOverheardComment } from 'app/shared/model/overheard-comment.model';

type EntityResponseType = HttpResponse<IOverheardComment>;
type EntityArrayResponseType = HttpResponse<IOverheardComment[]>;

@Injectable({ providedIn: 'root' })
export class OverheardCommentService {
  public resourceUrl = SERVER_API_URL + 'api/overheard-comments';

  constructor(protected http: HttpClient) {}

  create(overheardComment: IOverheardComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(overheardComment);
    return this.http
      .post<IOverheardComment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(overheardComment: IOverheardComment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(overheardComment);
    return this.http
      .put<IOverheardComment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOverheardComment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOverheardComment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(overheardComment: IOverheardComment): IOverheardComment {
    const copy: IOverheardComment = Object.assign({}, overheardComment, {
      date: overheardComment.date && overheardComment.date.isValid() ? overheardComment.date.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? moment(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((overheardComment: IOverheardComment) => {
        overheardComment.date = overheardComment.date ? moment(overheardComment.date) : undefined;
      });
    }
    return res;
  }
}
