import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { api, ApiError, type Topic } from '../lib/api';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { Alert, CharCount, Field, PageHeader } from '../components/ui';

export function PostEditorPage() {
  const params = useParams();
  const editingId = params.id ? Number(params.id) : null;
  const [search] = useSearchParams();
  const navigate = useNavigate();

  const [topics, setTopics] = useState<Topic[]>([]);
  const [topicId, setTopicId] = useState<string>(search.get('topic') ?? '');
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [fields, setFields] = useState<Record<string, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [loaded, setLoaded] = useState(editingId === null);
  const [saving, setSaving] = useState(false);
  useDocumentTitle(editingId ? 'Edit post' : 'New post');

  useEffect(() => {
    api.topics().then(setTopics).catch(e => setError((e as Error).message));
    if (editingId) {
      api
        .post(editingId)
        .then(p => {
          if (!p.canEdit) {
            navigate(`/posts/${p.id}`, { replace: true });
            return;
          }
          setTitle(p.title);
          setContent(p.content);
          setTopicId(String(p.topic.id));
          setLoaded(true);
        })
        .catch(e => setError((e as Error).message));
    }
  }, [editingId, navigate]);

  const valid = title.trim().length >= 2 && title.length <= 80 && content.trim().length >= 2 && content.length <= 4096 && topicId !== '';

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!valid) return;
    setSaving(true);
    setError(null);
    setFields({});
    try {
      const data = { title: title.trim(), content: content.trim(), topicId: Number(topicId) };
      const saved = editingId ? await api.updatePost(editingId, data) : await api.createPost(data);
      navigate(`/posts/${saved.id}`);
    } catch (err) {
      if (err instanceof ApiError) setFields(err.fields);
      setError((err as Error).message);
    } finally {
      setSaving(false);
    }
  };

  if (!loaded) return <div className="skeleton w50" />;

  return (
    <form onSubmit={submit} noValidate>
      <PageHeader eyebrow={editingId ? 'Edit post' : 'New post'} title={editingId ? 'Edit your post' : 'What did you overhear?'} />

      {error && <Alert kind="error">{error}</Alert>}

      <Field
        label="Topic"
        htmlFor="topic"
        error={fields.topicId}
        hint={
          topics.length === 0 ? (
            <>
              There are no topics yet. <Link to="/topics">Create one</Link> first.
            </>
          ) : undefined
        }
      >
        <select id="topic" className="input" value={topicId} onChange={e => setTopicId(e.target.value)}>
          <option value="">Choose a topic…</option>
          {topics.map(t => (
            <option key={t.id} value={t.id}>
              {t.title}
            </option>
          ))}
        </select>
      </Field>

      <Field label="Title" htmlFor="title" error={fields.title}>
        <input id="title" className="input" value={title} onChange={e => setTitle(e.target.value)} placeholder="A short headline" maxLength={80} autoComplete="off" />
      </Field>

      <Field label="The quote" htmlFor="content" error={fields.content}>
        <textarea
          id="content"
          className="input"
          value={content}
          onChange={e => setContent(e.target.value)}
          placeholder="Exactly what was said, and a little context if it helps."
          maxLength={4096}
        />
        <CharCount value={content} max={4096} />
      </Field>

      <div className="form-actions">
        <button type="button" className="btn secondary" onClick={() => navigate(-1)}>
          Cancel
        </button>
        <button type="submit" className="btn" disabled={!valid || saving}>
          {saving ? 'Saving…' : editingId ? 'Save changes' : 'Post it'}
        </button>
      </div>
    </form>
  );
}
