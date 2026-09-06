import { useState, type FormEvent } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { api, ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { Alert, Field, PageHeader } from '../components/ui';

function useAfterAuth() {
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? '/';
  return () => navigate(from, { replace: true });
}

export function LoginPage() {
  const { signIn } = useAuth();
  const done = useAfterAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  useDocumentTitle('Sign in');

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      const res = await api.login({ username, password });
      signIn(res.token, res.user);
      done();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} style={{ maxWidth: '24rem' }}>
      <PageHeader eyebrow="Members" title="Welcome back" />
      {error && <Alert kind="error">{error}</Alert>}
      <Field label="Username or email" htmlFor="username">
        <input id="username" className="input" value={username} onChange={e => setUsername(e.target.value)} autoComplete="username" autoFocus />
      </Field>
      <Field label="Password" htmlFor="password">
        <input id="password" type="password" className="input" value={password} onChange={e => setPassword(e.target.value)} autoComplete="current-password" />
      </Field>
      <button type="submit" className="btn block" disabled={!username || !password || busy}>
        {busy ? 'Signing in…' : 'Sign in'}
      </button>
      <p className="muted small center mt">
        New here? <Link to="/register">Create an account</Link>
      </p>
    </form>
  );
}

export function RegisterPage() {
  const { signIn } = useAuth();
  const done = useAfterAuth();
  const [form, setForm] = useState({ username: '', email: '', displayName: '', password: '', confirm: '' });
  const [fields, setFields] = useState<Record<string, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  useDocumentTitle('Join');

  const set = (k: keyof typeof form) => (e: { target: { value: string } }) => setForm(f => ({ ...f, [k]: e.target.value }));
  const mismatch = form.confirm !== '' && form.confirm !== form.password;
  const valid = form.username.length >= 3 && form.email.includes('@') && form.displayName.trim() !== '' && form.password.length >= 8 && form.confirm === form.password;

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!valid) return;
    setBusy(true);
    setError(null);
    setFields({});
    try {
      const res = await api.register({ username: form.username, email: form.email, password: form.password, displayName: form.displayName.trim() });
      signIn(res.token, res.user);
      done();
    } catch (err) {
      if (err instanceof ApiError) setFields(err.fields);
      setError((err as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} style={{ maxWidth: '24rem' }} noValidate>
      <PageHeader eyebrow="Join" title="Create your account" />
      {error && <Alert kind="error">{error}</Alert>}
      <Field label="Display name" htmlFor="displayName" error={fields.displayName} hint="How you'll appear on posts.">
        <input id="displayName" className="input" value={form.displayName} onChange={set('displayName')} maxLength={50} autoFocus />
      </Field>
      <Field label="Username" htmlFor="username" error={fields.username} hint="Letters, digits, dot, dash or underscore.">
        <input id="username" className="input" value={form.username} onChange={set('username')} autoComplete="username" maxLength={50} />
      </Field>
      <Field label="Email" htmlFor="email" error={fields.email}>
        <input id="email" type="email" className="input" value={form.email} onChange={set('email')} autoComplete="email" />
      </Field>
      <Field label="Password" htmlFor="password" error={fields.password} hint="At least 8 characters.">
        <input id="password" type="password" className="input" value={form.password} onChange={set('password')} autoComplete="new-password" />
      </Field>
      <Field label="Confirm password" htmlFor="confirm" error={mismatch ? "Passwords don't match." : undefined}>
        <input id="confirm" type="password" className={`input${mismatch ? ' invalid' : ''}`} value={form.confirm} onChange={set('confirm')} autoComplete="new-password" />
      </Field>
      <button type="submit" className="btn block" disabled={!valid || busy}>
        {busy ? 'Creating…' : 'Create account'}
      </button>
      <p className="muted small center mt">
        Already a member? <Link to="/login">Sign in</Link>
      </p>
    </form>
  );
}
