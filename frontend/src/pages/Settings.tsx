import { useState, type FormEvent } from 'react';
import { api, ApiError } from '../lib/api';
import { useAuth } from '../lib/auth';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { Alert, Field, PageHeader } from '../components/ui';

export function SettingsPage() {
  const { user, refresh } = useAuth();
  useDocumentTitle('Settings');
  const [displayName, setDisplayName] = useState(user?.displayName ?? '');
  const [email, setEmail] = useState(user?.email ?? '');
  const [profileMsg, setProfileMsg] = useState<{ kind: 'ok' | 'error'; text: string } | null>(null);
  const [profileFields, setProfileFields] = useState<Record<string, string>>({});
  const [savingProfile, setSavingProfile] = useState(false);

  const [current, setCurrent] = useState('');
  const [next, setNext] = useState('');
  const [confirm, setConfirm] = useState('');
  const [pwMsg, setPwMsg] = useState<{ kind: 'ok' | 'error'; text: string } | null>(null);
  const [savingPw, setSavingPw] = useState(false);

  if (!user) return null;

  const saveProfile = async (e: FormEvent) => {
    e.preventDefault();
    setSavingProfile(true);
    setProfileMsg(null);
    setProfileFields({});
    try {
      await api.updateProfile({ displayName: displayName.trim(), email: email.trim() });
      await refresh();
      setProfileMsg({ kind: 'ok', text: 'Profile saved.' });
    } catch (err) {
      if (err instanceof ApiError) setProfileFields(err.fields);
      setProfileMsg({ kind: 'error', text: (err as Error).message });
    } finally {
      setSavingProfile(false);
    }
  };

  const savePassword = async (e: FormEvent) => {
    e.preventDefault();
    if (next !== confirm) {
      setPwMsg({ kind: 'error', text: "New passwords don't match." });
      return;
    }
    setSavingPw(true);
    setPwMsg(null);
    try {
      await api.changePassword({ currentPassword: current, newPassword: next });
      setCurrent('');
      setNext('');
      setConfirm('');
      setPwMsg({ kind: 'ok', text: 'Password updated.' });
    } catch (err) {
      setPwMsg({ kind: 'error', text: (err as Error).message });
    } finally {
      setSavingPw(false);
    }
  };

  return (
    <>
      <PageHeader eyebrow="Account" title="Settings" action={<span className="muted small">Signed in as <strong>@{user.username}</strong></span>} />

      <form onSubmit={saveProfile} noValidate>
        <h2 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Profile</h2>
        {profileMsg && <Alert kind={profileMsg.kind}>{profileMsg.text}</Alert>}
        <Field label="Display name" htmlFor="displayName" error={profileFields.displayName}>
          <input id="displayName" className="input" value={displayName} onChange={e => setDisplayName(e.target.value)} maxLength={50} />
        </Field>
        <Field label="Email" htmlFor="email" error={profileFields.email}>
          <input id="email" type="email" className="input" value={email} onChange={e => setEmail(e.target.value)} />
        </Field>
        <div className="form-actions">
          <button type="submit" className="btn" disabled={savingProfile || displayName.trim() === '' || !email.includes('@')}>
            {savingProfile ? 'Saving…' : 'Save changes'}
          </button>
        </div>
      </form>

      <form onSubmit={savePassword} noValidate style={{ marginTop: '3rem' }}>
        <h2 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Password</h2>
        {pwMsg && <Alert kind={pwMsg.kind}>{pwMsg.text}</Alert>}
        <Field label="Current password" htmlFor="current">
          <input id="current" type="password" className="input" value={current} onChange={e => setCurrent(e.target.value)} autoComplete="current-password" />
        </Field>
        <Field label="New password" htmlFor="next" hint="At least 8 characters.">
          <input id="next" type="password" className="input" value={next} onChange={e => setNext(e.target.value)} autoComplete="new-password" />
        </Field>
        <Field label="Confirm new password" htmlFor="confirm">
          <input id="confirm" type="password" className="input" value={confirm} onChange={e => setConfirm(e.target.value)} autoComplete="new-password" />
        </Field>
        <div className="form-actions">
          <button type="submit" className="btn" disabled={savingPw || !current || next.length < 8 || !confirm}>
            {savingPw ? 'Saving…' : 'Update password'}
          </button>
        </div>
      </form>
    </>
  );
}
