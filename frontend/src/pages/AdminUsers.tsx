import { useEffect, useState } from 'react';
import { api, type AdminUser } from '../lib/api';
import { useAuth } from '../lib/auth';
import { timeAgo } from '../lib/format';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { Alert, Avatar, PageHeader } from '../components/ui';

export function AdminUsersPage() {
  const { user: me } = useAuth();
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [error, setError] = useState<string | null>(null);
  useDocumentTitle('Members');

  useEffect(() => {
    api.adminUsers().then(setUsers).catch(e => setError((e as Error).message));
  }, []);

  const toggleRole = async (u: AdminUser) => {
    setError(null);
    try {
      const updated = await api.setRole(u.id, u.role === 'ADMIN' ? 'USER' : 'ADMIN');
      setUsers(prev => prev.map(x => (x.id === updated.id ? updated : x)));
    } catch (err) {
      setError((err as Error).message);
    }
  };

  return (
    <>
      <PageHeader eyebrow="Admin" title="Members" action={<span className="muted small">{users.length} total</span>} />
      {error && <Alert kind="error">{error}</Alert>}
      <div className="table-wrap">
        <table className="table">
          <thead>
            <tr>
              <th>Member</th>
              <th>Email</th>
              <th>Posts</th>
              <th>Joined</th>
              <th>Role</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.id}>
                <td>
                  <span className="author">
                    <Avatar name={u.displayName} />
                    {u.displayName} <span className="muted">@{u.username}</span>
                  </span>
                </td>
                <td className="muted">{u.email}</td>
                <td>{u.postCount}</td>
                <td className="muted">{timeAgo(u.createdAt)}</td>
                <td>
                  <span className="chip">{u.role === 'ADMIN' ? 'Admin' : 'Member'}</span>
                </td>
                <td style={{ textAlign: 'right' }}>
                  {u.id !== me?.id && (
                    <button type="button" className="btn secondary sm" onClick={() => toggleRole(u)}>
                      {u.role === 'ADMIN' ? 'Make member' : 'Make admin'}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
