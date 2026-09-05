import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { api, ApiError, type Topic } from '../lib/api';
import { useAuth } from '../lib/auth';
import { plural } from '../lib/format';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { Alert, BackLink, Confirm, Empty, PageHeader } from '../components/ui';
import { PostFeed } from './Feed';

export function TopicsPage() {
  const { user } = useAuth();
  const [topics, setTopics] = useState<Topic[] | null>(null);
  const [creating, setCreating] = useState(false);
  const [title, setTitle] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  useDocumentTitle('Topics');

  useEffect(() => {
    api.topics().then(setTopics).catch(e => setError((e as Error).message));
  }, []);

  const create = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const t = await api.createTopic(title.trim());
      setTopics(prev => [...(prev ?? []), t].sort((a, b) => a.title.localeCompare(b.title)));
      setTitle('');
      setCreating(false);
    } catch (err) {
      setError(err instanceof ApiError ? err.fields.title ?? err.message : (err as Error).message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <>
      <PageHeader
        eyebrow="Browse"
        title="Topics"
        action={
          user && !creating ? (
            <button type="button" className="btn secondary sm" onClick={() => setCreating(true)}>
              + New topic
            </button>
          ) : undefined
        }
      />

      {error && <Alert kind="error">{error}</Alert>}

      {creating && (
        <form onSubmit={create} className="field">
          <label htmlFor="new-topic">Topic name</label>
          <div className="row">
            <input id="new-topic" className="input" value={title} onChange={e => setTitle(e.target.value)} placeholder="e.g. On the train" maxLength={80} autoFocus />
            <button type="submit" className="btn" disabled={title.trim().length < 2 || saving}>
              {saving ? 'Saving…' : 'Create'}
            </button>
            <button type="button" className="btn ghost" onClick={() => setCreating(false)}>
              Cancel
            </button>
          </div>
        </form>
      )}

      {topics === null ? (
        <div className="topic-grid">
          {[1, 2, 3].map(i => (
            <div className="topic-tile" key={i}>
              <div className="skeleton w75" />
              <div className="skeleton w25" />
            </div>
          ))}
        </div>
      ) : topics.length === 0 ? (
        <Empty>
          <p>No topics yet.</p>
          {user && (
            <button type="button" className="btn" onClick={() => setCreating(true)}>
              Create the first one
            </button>
          )}
        </Empty>
      ) : (
        <div className="topic-grid">
          {topics.map(t => (
            <Link key={t.id} to={`/topics/${t.id}`} className="topic-tile">
              <div className="name">{t.title}</div>
              <div className="count">{plural(t.postCount, 'post')}</div>
            </Link>
          ))}
        </div>
      )}
    </>
  );
}

export function TopicPage() {
  const id = Number(useParams().id);
  const { user } = useAuth();
  const navigate = useNavigate();
  const [topic, setTopic] = useState<Topic | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [renaming, setRenaming] = useState(false);
  const [title, setTitle] = useState('');
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [busy, setBusy] = useState(false);
  useDocumentTitle(topic?.title);

  useEffect(() => {
    setTopic(null);
    api.topic(id).then(setTopic).catch(e => setError((e as Error).message));
  }, [id]);

  const canManage = !!user && !!topic && (user.role === 'ADMIN' || topic.createdBy.id === user.id);

  const rename = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      setTopic(await api.renameTopic(id, title.trim()));
      setRenaming(false);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    setBusy(true);
    try {
      await api.deleteTopic(id);
      navigate('/topics');
    } catch (err) {
      setError((err as Error).message);
      setConfirmDelete(false);
    } finally {
      setBusy(false);
    }
  };

  if (error && !topic) return <Empty>{error}</Empty>;
  if (!topic) return <div className="skeleton w50" />;

  return (
    <>
      <BackLink to="/topics">All topics</BackLink>

      {renaming ? (
        <form onSubmit={rename} className="field">
          <label htmlFor="rename">Topic name</label>
          <div className="row">
            <input id="rename" className="input" value={title} onChange={e => setTitle(e.target.value)} maxLength={80} autoFocus />
            <button type="submit" className="btn" disabled={title.trim().length < 2 || busy}>
              Save
            </button>
            <button type="button" className="btn ghost" onClick={() => setRenaming(false)}>
              Cancel
            </button>
          </div>
        </form>
      ) : (
        <PageHeader
          eyebrow={plural(topic.postCount, 'post')}
          title={topic.title}
          action={
            <div className="row">
              {canManage && (
                <>
                  <button
                    type="button"
                    className="btn ghost sm"
                    onClick={() => {
                      setTitle(topic.title);
                      setRenaming(true);
                    }}
                  >
                    Rename
                  </button>
                  {topic.postCount === 0 && (
                    <button type="button" className="btn ghost sm" onClick={() => setConfirmDelete(true)}>
                      Delete
                    </button>
                  )}
                </>
              )}
              {user && (
                <Link to={`/posts/new?topic=${topic.id}`} className="btn sm">
                  + Post here
                </Link>
              )}
            </div>
          }
        />
      )}

      {error && <Alert kind="error">{error}</Alert>}

      <PostFeed sort="latest" topicId={id} hideTopic emptyMessage="Nothing has been posted in this topic yet." />

      {confirmDelete && (
        <Confirm title="Delete this topic?" body={`“${topic.title}” will be removed.`} busy={busy} onConfirm={remove} onCancel={() => setConfirmDelete(false)} />
      )}
    </>
  );
}
