import { Moment } from 'moment';
import { RankType } from 'app/shared/model/enumerations/rank-type.model';

export interface IRank {
  id?: number;
  rank?: number;
  rankType?: RankType;
  date?: Moment;
}

export class Rank implements IRank {
  constructor(public id?: number, public rank?: number, public rankType?: RankType, public date?: Moment) {}
}
