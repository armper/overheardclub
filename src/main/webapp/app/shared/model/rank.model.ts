import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { RankType } from 'app/shared/model/enumerations/rank-type.model';

export interface IRank {
  id?: number;
  rankType?: RankType;
  date?: Moment;
  user?: IUser;
}

export class Rank implements IRank {
  constructor(public id?: number, public rankType?: RankType, public date?: Moment, public user?: IUser) {}
}
