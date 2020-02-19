import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model/post.model';

export interface IOverheardComment {
  id?: number;
  content?: string;
  date?: Moment;
  ranking?: number;
  user?: IUser;
  post?: IPost;
}

export class OverheardComment implements IOverheardComment {
  constructor(
    public id?: number,
    public content?: string,
    public date?: Moment,
    public ranking?: number,
    public user?: IUser,
    public post?: IPost
  ) {}
}
