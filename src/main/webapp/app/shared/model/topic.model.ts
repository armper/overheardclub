import { IPost } from 'app/shared/model/post.model';
import { IUser } from 'app/core/user/user.model';

export interface ITopic {
  id?: number;
  title?: string;
  posts?: IPost[];
  user?: IUser;
}

export class Topic implements ITopic {
  constructor(public id?: number, public title?: string, public posts?: IPost[], public user?: IUser) {}
}
