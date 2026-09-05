import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../lib/auth';
import { Avatar, Menu } from './ui';

export function TopBar() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const close = () => setOpen(false);

  const logout = () => {
    signOut();
    close();
    navigate('/');
  };

  return (
    <nav className="topbar">
      <div className="topbar-inner">
        <Link to="/" className="brand" onClick={close}>
          <span className="brand-mark" aria-hidden="true" />
          <span>
            Overheard<span className="brand-accent">Club</span>
          </span>
        </Link>

        <button type="button" className="topbar-toggle" aria-label="Toggle navigation" aria-expanded={open} onClick={() => setOpen(o => !o)}>
          {open ? '×' : '☰'}
        </button>

        <div className={`topbar-links${open ? ' open' : ''}`}>
          <NavLink to="/" end className="nav-link" onClick={close}>
            Feed
          </NavLink>
          <NavLink to="/topics" className="nav-link" onClick={close}>
            Topics
          </NavLink>
          <div className="topbar-spacer" />

          {user ? (
            <>
              <Link to="/posts/new" className="btn sm" onClick={close}>
                + New post
              </Link>
              <Menu
                label="Account menu"
                button={
                  <>
                    <Avatar name={user.displayName} large />
                    <span className="menu-name">{user.displayName}</span>
                  </>
                }
              >
                <div className="menu-header">{user.displayName}</div>
                <Link to="/settings" className="menu-item" onClick={close}>
                  Settings
                </Link>
                {user.role === 'ADMIN' && (
                  <Link to="/admin/users" className="menu-item" onClick={close}>
                    Members
                  </Link>
                )}
                <div className="menu-sep" />
                <button type="button" className="menu-item" onClick={logout}>
                  Sign out
                </button>
              </Menu>
            </>
          ) : (
            <>
              <NavLink to="/login" className="nav-link" onClick={close}>
                Sign in
              </NavLink>
              <Link to="/register" className="btn sm" onClick={close}>
                Join
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
