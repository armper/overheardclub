import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, REACTIONS, type Post, type ReactionType } from '../lib/api';
import { useAuth } from '../lib/auth';
import { fullDate, plural, timeAgo } from '../lib/format';
import { Avatar } from './ui';

function excerpt(text: string, max = 220): string {
  if (text.length <= max) return text;
  return text.slice(0, max).replace(/\s+\S*$/, '') + '…';
}

export function Reactions({ post, onChange }: { post: Post; onChange: (p: Post) => void }) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);

  const toggle = async (type: ReactionType) => {
    if (!user) {
      navigate('/login', { state: { from: location.pathname } });
      return;
    }
    if (busy) return;
    setBusy(true);
    try {
      onChange(post.myReaction === type ? await api.unreact(post.id) : await api.react(post.id, type));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="reactions" aria-label="Reactions">
      {REACTIONS.map(r => {
        const n = post.reactions[r.type] ?? 0;
        const mine = post.myReaction === r.type;
        return (
          <button
            key={r.type}
            type="button"
            className={`reaction${mine ? ' mine' : ''}`}
            title={mine ? 'Remove your reaction' : r.label}
            aria-pressed={mine}
            disabled={busy}
            onClick={() => toggle(r.type)}
          >
            <span className="emoji">{r.emoji}</span>
            {n > 0 && <span className="count">{n}</span>}
          </button>
        );
      })}
    </div>
  );
}

export function PostMeta({ post, hideTopic }: { post: Post; hideTopic?: boolean }) {
  return (
    <div className="meta">
      <span className="author">
        <Avatar name={post.author.displayName} />
        {post.author.displayName}
      </span>
      <span className="dot" />
      <span title={fullDate(post.createdAt)}>{timeAgo(post.createdAt)}</span>
      {!hideTopic && (
        <>
          <span className="dot" />
          <Link className="chip" to={`/topics/${post.topic.id}`}>
            {post.topic.title}
          </Link>
        </>
      )}
    </div>
  );
}

export function PostCard({ post, hideTopic, onChange }: { post: Post; hideTopic?: boolean; onChange: (p: Post) => void }) {
  return (
    <article className="post-card">
      <PostMeta post={post} hideTopic={hideTopic} />
      <h2 className="post-title">
        <Link to={`/posts/${post.id}`}>{post.title}</Link>
      </h2>
      <p className="post-excerpt">{excerpt(post.content)}</p>
      <Reactions post={post} onChange={onChange} />
      <div className="post-actions">
        <Link to={`/posts/${post.id}`}>{plural(post.commentCount, 'comment')}</Link>
      </div>
    </article>
  );
}
