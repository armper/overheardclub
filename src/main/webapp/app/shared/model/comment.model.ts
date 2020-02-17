import { Moment } from 'moment';
import { IRank } from 'app/shared/model/rank.model';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model/post.model';

export interface IComment {
  id?: number;
  content?: string;
  date?: Moment;
  rank?: IRank;
  user?: IUser;
  post?: IPost;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public content?: string,
    public date?: Moment,
    public rank?: IRank,
    public user?: IUser,
    public post?: IPost
  ) {}
}
