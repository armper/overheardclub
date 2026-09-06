import { useEffect, useRef, useState, type ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { initial } from '../lib/format';

export function Avatar({ name, large }: { name: string; large?: boolean }) {
  return <span className={`avatar${large ? ' lg' : ''}`}>{initial(name)}</span>;
}

export function PageHeader({ eyebrow, title, action }: { eyebrow?: string; title: string; action?: ReactNode }) {
  return (
    <div className="page-header">
      <div>
        {eyebrow && <span className="eyebrow">{eyebrow}</span>}
        <h1>{title}</h1>
      </div>
      {action}
    </div>
  );
}

export function Empty({ children, moose }: { children: ReactNode; moose?: boolean }) {
  return (
    <div className="empty">
      {moose && <img src="/moose.svg" alt="" />}
      {children}
    </div>
  );
}

export function FeedSkeleton({ rows = 3 }: { rows?: number }) {
  return (
    <div className="feed" aria-busy="true">
      {Array.from({ length: rows }).map((_, i) => (
        <div className="post-card" key={i}>
          <div className="skeleton w25" />
          <div className="skeleton w75" />
          <div className="skeleton w50" />
        </div>
      ))}
    </div>
  );
}

export function Alert({ kind, children }: { kind?: 'error' | 'ok'; children: ReactNode }) {
  return <div className={`alert${kind ? ` ${kind}` : ''}`}>{children}</div>;
}

export function Field({
  label,
  htmlFor,
  error,
  hint,
  children,
}: {
  label: string;
  htmlFor: string;
  error?: string;
  hint?: ReactNode;
  children: ReactNode;
}) {
  return (
    <div className="field">
      <label htmlFor={htmlFor}>{label}</label>
      {children}
      {error ? <span className="hint error">{error}</span> : hint ? <span className="hint">{hint}</span> : null}
    </div>
  );
}

export function CharCount({ value, max }: { value: string; max: number }) {
  return (
    <span className={`char-count${value.length > max ? ' over' : ''}`}>
      {value.length} / {max}
    </span>
  );
}

export function BackLink({ to, children }: { to: string; children: ReactNode }) {
  return (
    <Link to={to} className="btn ghost sm" style={{ marginBottom: '1rem', marginLeft: '-0.5rem' }}>
      ← {children}
    </Link>
  );
}

/** A small dropdown menu that closes on outside click or Escape. */
export function Menu({ button, children, label }: { button: ReactNode; children: ReactNode; label: string }) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && setOpen(false);
    document.addEventListener('mousedown', onClick);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('mousedown', onClick);
      document.removeEventListener('keydown', onKey);
    };
  }, [open]);

  return (
    <div className="menu" ref={ref}>
      <button type="button" className="menu-button" aria-haspopup="menu" aria-expanded={open} aria-label={label} onClick={() => setOpen(o => !o)}>
        {button}
      </button>
      {open && (
        <div className="menu-panel" role="menu" onClick={() => setOpen(false)}>
          {children}
        </div>
      )}
    </div>
  );
}

export function Confirm({
  title,
  body,
  confirmLabel = 'Delete',
  onConfirm,
  onCancel,
  busy,
}: {
  title: string;
  body: ReactNode;
  confirmLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
  busy?: boolean;
}) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onCancel();
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [onCancel]);
  return (
    <div className="modal-backdrop" onClick={onCancel}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="confirm-title" onClick={e => e.stopPropagation()}>
        <h2 id="confirm-title">{title}</h2>
        <p className="muted">{body}</p>
        <div className="form-actions">
          <button type="button" className="btn secondary" onClick={onCancel} disabled={busy}>
            Cancel
          </button>
          <button type="button" className="btn danger" onClick={onConfirm} disabled={busy}>
            {busy ? 'Working…' : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
