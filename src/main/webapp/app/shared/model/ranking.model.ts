import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model/post.model';
import { RankType } from 'app/shared/model/enumerations/rank-type.model';

export interface IRanking {
  id?: number;
  rankType?: RankType;
  date?: Moment;
  user?: IUser;
  post?: IPost;
}

export class Ranking implements IRanking {
  constructor(public id?: number, public rankType?: RankType, public date?: Moment, public user?: IUser, public post?: IPost) {}
}
