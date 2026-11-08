import { IPost } from 'app/shared/model/post.model';

export interface IReaction {
  id: number;
  emoji: string;
  label: string;
  count: (post: IPost) => number;
}

export const REACTIONS: IReaction[] = [
  { id: 1, emoji: '👍', label: 'Like', count: post => post.rankOne || 0 },
  { id: 2, emoji: '❤️', label: 'Love', count: post => post.rankTwo || 0 },
  { id: 3, emoji: '😂', label: 'Funny', count: post => post.rankThree || 0 },
  { id: 4, emoji: '🤯', label: 'Mind blown', count: post => post.rankFour || 0 },
  { id: 5, emoji: '😠', label: 'Angry', count: post => post.rankFive || 0 }
];

export const totalReactions = (post: IPost): number => REACTIONS.reduce((sum, reaction) => sum + reaction.count(post), 0);

export const hasReacted = (post: IPost, login?: string | null): boolean =>
  !!login && !!post.userUpranks && post.userUpranks.some(user => user.login === login);

export const authorName = (post: { user?: { firstName?: string; login?: string } }): string =>
  (post.user && (post.user.firstName || post.user.login)) || 'Anonymous';
