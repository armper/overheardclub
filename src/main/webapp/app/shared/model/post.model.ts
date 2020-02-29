import { Moment } from 'moment';
import { IOverheardComment } from 'app/shared/model/overheard-comment.model';
import { IUser } from 'app/core/user/user.model';
import { ITopic } from 'app/shared/model/topic.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  date?: Moment;
  rankOne?: number;
  rankTwo?: number;
  rankThree?: number;
  rankFour?: number;
  rankFive?: number;
  overheardComments?: IOverheardComment[];
  user?: IUser;
  topic?: ITopic;
  userUpranks?: IUser[];
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public title?: string,
    public content?: string,
    public date?: Moment,
    public rankOne?: number,
    public rankTwo?: number,
    public rankThree?: number,
    public rankFour?: number,
    public rankFive?: number,
    public overheardComments?: IOverheardComment[],
    public user?: IUser,
    public topic?: ITopic,
    public userUpranks?: IUser[]
  ) {}
}
