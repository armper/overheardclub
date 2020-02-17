import { Moment } from 'moment';
import { IComment } from 'app/shared/model/comment.model';
import { IRank } from 'app/shared/model/rank.model';
import { IUser } from 'app/core/user/user.model';
import { ITopic } from 'app/shared/model/topic.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  date?: Moment;
  comments?: IComment[];
  rank?: IRank;
  user?: IUser;
  topic?: ITopic;
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public title?: string,
    public content?: string,
    public date?: Moment,
    public comments?: IComment[],
    public rank?: IRank,
    public user?: IUser,
    public topic?: ITopic
  ) {}
}
