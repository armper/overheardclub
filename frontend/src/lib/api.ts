// Typed client for the Overheard Club JSON API.

export type ReactionType = 'NICE' | 'LOVE' | 'FUNNY' | 'MIND_BLOWN' | 'EYE_ROLL';

export const REACTIONS: { type: ReactionType; emoji: string; label: string }[] = [
  { type: 'NICE', emoji: '👍', label: 'Nice' },
  { type: 'LOVE', emoji: '❤️', label: 'Love it' },
  { type: 'FUNNY', emoji: '😂', label: 'Funny' },
  { type: 'MIND_BLOWN', emoji: '🤯', label: 'Mind blown' },
  { type: 'EYE_ROLL', emoji: '🙄', label: 'Eye roll' },
];

export interface User {
  id: number;
  username: string;
  displayName: string;
  email: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
}

export interface Author {
  id: number;
  displayName: string;
}

export interface Topic {
  id: number;
  title: string;
  postCount: number;
  createdBy: Author;
  createdAt: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  author: Author;
  topic: { id: number; title: string };
  createdAt: string;
  updatedAt?: string;
  reactions: Partial<Record<ReactionType, number>>;
  myReaction?: ReactionType;
  commentCount: number;
  canEdit: boolean;
}

export interface Page<T> {
  items: T[];
  page: number;
  size: number;
  total: number;
  hasMore: boolean;
}

export interface Comment {
  id: number;
  content: string;
  author: Author;
  createdAt: string;
  canDelete: boolean;
}

export interface AdminUser {
  id: number;
  username: string;
  displayName: string;
  email: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
  postCount: number;
}

export class ApiError extends Error {
  status: number;
  fields: Record<string, string>;
  constructor(status: number, message: string, fields: Record<string, string> = {}) {
    super(message);
    this.status = status;
    this.fields = fields;
  }
}

const TOKEN_KEY = 'oc.token';

export function getToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setToken(token: string | null) {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
  } catch {
    /* storage unavailable */
  }
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`/api${path}`, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) });
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  const data = text ? JSON.parse(text) : undefined;
  if (!res.ok) {
    if (res.status === 401 && token) setToken(null);
    const message = data?.message ?? (res.status === 401 ? 'Please sign in.' : `Request failed (${res.status}).`);
    throw new ApiError(res.status, message, data?.fields ?? {});
  }
  return data as T;
}

const q = (params: Record<string, string | number | undefined>) => {
  const s = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => v !== undefined && s.set(k, String(v)));
  const str = s.toString();
  return str ? `?${str}` : '';
};

export const api = {
  register: (data: { username: string; email: string; password: string; displayName: string }) =>
    request<{ token: string; user: User }>('POST', '/auth/register', data),
  login: (data: { username: string; password: string }) => request<{ token: string; user: User }>('POST', '/auth/login', data),
  me: () => request<User>('GET', '/me'),
  updateProfile: (data: { displayName: string; email: string }) => request<User>('PUT', '/me', data),
  changePassword: (data: { currentPassword: string; newPassword: string }) => request<void>('PUT', '/me/password', data),

  topics: () => request<Topic[]>('GET', '/topics'),
  topic: (id: number) => request<Topic>('GET', `/topics/${id}`),
  createTopic: (title: string) => request<Topic>('POST', '/topics', { title }),
  renameTopic: (id: number, title: string) => request<Topic>('PUT', `/topics/${id}`, { title }),
  deleteTopic: (id: number) => request<void>('DELETE', `/topics/${id}`),

  posts: (params: { sort?: 'top' | 'latest'; topicId?: number; page?: number; size?: number }) =>
    request<Page<Post>>('GET', `/posts${q(params)}`),
  post: (id: number) => request<Post>('GET', `/posts/${id}`),
  createPost: (data: { title: string; content: string; topicId: number }) => request<Post>('POST', '/posts', data),
  updatePost: (id: number, data: { title: string; content: string; topicId: number }) => request<Post>('PUT', `/posts/${id}`, data),
  deletePost: (id: number) => request<void>('DELETE', `/posts/${id}`),
  react: (id: number, type: ReactionType) => request<Post>('PUT', `/posts/${id}/reaction`, { type }),
  unreact: (id: number) => request<Post>('DELETE', `/posts/${id}/reaction`),

  comments: (postId: number) => request<Comment[]>('GET', `/posts/${postId}/comments`),
  addComment: (postId: number, content: string) => request<Comment>('POST', `/posts/${postId}/comments`, { content }),
  deleteComment: (id: number) => request<void>('DELETE', `/comments/${id}`),

  adminUsers: () => request<AdminUser[]>('GET', '/admin/users'),
  setRole: (id: number, role: 'USER' | 'ADMIN') => request<AdminUser>('PUT', `/admin/users/${id}/role`, { role }),
};
