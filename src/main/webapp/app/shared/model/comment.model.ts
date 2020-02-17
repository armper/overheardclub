import { Moment } from 'moment';
import { IRanking } from 'app/shared/model/ranking.model';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model/post.model';

export interface IComment {
  id?: number;
  content?: string;
  date?: Moment;
  rank?: IRanking;
  user?: IUser;
  post?: IPost;
}

export class Comment implements IComment {
  constructor(
    public id?: number,
    public content?: string,
    public date?: Moment,
    public rank?: IRanking,
    public user?: IUser,
    public post?: IPost
  ) {}
}
