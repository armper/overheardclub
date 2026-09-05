import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api, type Comment, type Post } from '../lib/api';
import { useAuth } from '../lib/auth';
import { fullDate, plural, timeAgo } from '../lib/format';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { PostMeta, Reactions } from '../components/PostCard';
import { Alert, Avatar, BackLink, CharCount, Confirm, Empty } from '../components/ui';

export function PostPage() {
  const id = Number(useParams().id);
  const { user } = useAuth();
  const navigate = useNavigate();
  const [post, setPost] = useState<Post | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [draft, setDraft] = useState('');
  const [posting, setPosting] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);
  useDocumentTitle(post?.title);

  useEffect(() => {
    setPost(null);
    setError(null);
    Promise.all([api.post(id), api.comments(id)])
      .then(([p, c]) => {
        setPost(p);
        setComments(c);
      })
      .catch(e => setError((e as Error).message));
  }, [id, user?.id]);

  const addComment = async (e: FormEvent) => {
    e.preventDefault();
    if (!post) return;
    setPosting(true);
    setError(null);
    try {
      const c = await api.addComment(post.id, draft.trim());
      setComments(prev => [...prev, c]);
      setPost({ ...post, commentCount: post.commentCount + 1 });
      setDraft('');
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setPosting(false);
    }
  };

  const removeComment = async (c: Comment) => {
    if (!post) return;
    try {
      await api.deleteComment(c.id);
      setComments(prev => prev.filter(x => x.id !== c.id));
      setPost({ ...post, commentCount: Math.max(0, post.commentCount - 1) });
    } catch (err) {
      setError((err as Error).message);
    }
  };

  const remove = async () => {
    if (!post) return;
    setDeleting(true);
    try {
      await api.deletePost(post.id);
      navigate(`/topics/${post.topic.id}`);
    } catch (err) {
      setError((err as Error).message);
      setConfirmDelete(false);
    } finally {
      setDeleting(false);
    }
  };

  if (error && !post) return <Empty>{error}</Empty>;
  if (!post) {
    return (
      <div>
        <div className="skeleton w25" />
        <div className="skeleton w75" />
        <div className="skeleton w50" />
      </div>
    );
  }

  return (
    <>
      <BackLink to={`/topics/${post.topic.id}`}>{post.topic.title}</BackLink>

      <article>
        <PostMeta post={post} />
        <h1 className="post-title lg">{post.title}</h1>
        <blockquote className="post-quote">{post.content}</blockquote>
        <Reactions post={post} onChange={setPost} />
        {post.canEdit && (
          <div className="post-actions">
            <Link to={`/posts/${post.id}/edit`}>Edit</Link>
            <span>·</span>
            <button type="button" className="link-btn" onClick={() => setConfirmDelete(true)}>
              Delete
            </button>
            {post.updatedAt && (
              <>
                <span>·</span>
                <span title={fullDate(post.updatedAt)}>edited {timeAgo(post.updatedAt)}</span>
              </>
            )}
          </div>
        )}
      </article>

      <section className="comments">
        <h2 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>{plural(comments.length, 'comment')}</h2>

        {error && <Alert kind="error">{error}</Alert>}

        {user ? (
          <form className="comment-form" onSubmit={addComment}>
            <textarea className="input" value={draft} onChange={e => setDraft(e.target.value)} placeholder="Add a comment…" aria-label="Comment" maxLength={4096} />
            <div className="row between" style={{ marginTop: '0.5rem' }}>
              <CharCount value={draft} max={4096} />
              <button type="submit" className="btn sm" disabled={draft.trim().length === 0 || posting}>
                {posting ? 'Posting…' : 'Comment'}
              </button>
            </div>
          </form>
        ) : (
          <p className="muted small">
            <Link to="/login">Sign in</Link> to join the conversation.
          </p>
        )}

        <div className="mt">
          {comments.map(c => (
            <article className="comment" key={c.id}>
              <div className="meta">
                <span className="author">
                  <Avatar name={c.author.displayName} />
                  {c.author.displayName}
                </span>
                <span className="dot" />
                <span title={fullDate(c.createdAt)}>{timeAgo(c.createdAt)}</span>
                {c.canDelete && (
                  <>
                    <span className="dot" />
                    <button type="button" className="link-btn small" onClick={() => removeComment(c)}>
                      Delete
                    </button>
                  </>
                )}
              </div>
              <p className="comment-body">{c.content}</p>
            </article>
          ))}
        </div>
      </section>

      {confirmDelete && (
        <Confirm
          title="Delete this post?"
          body={`“${post.title}” and its comments will be removed. This can't be undone.`}
          busy={deleting}
          onConfirm={remove}
          onCancel={() => setConfirmDelete(false)}
        />
      )}
    </>
  );
}
