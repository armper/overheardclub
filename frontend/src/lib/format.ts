const rtf = new Intl.RelativeTimeFormat('en', { numeric: 'auto' });

/** "3 hours ago", "yesterday", "2 weeks ago" — or a date once it's older than a month. */
export function timeAgo(iso: string): string {
  const then = new Date(iso).getTime();
  const diff = (then - Date.now()) / 1000;
  const abs = Math.abs(diff);
  if (abs < 45) return 'just now';
  if (abs < 3600) return rtf.format(Math.round(diff / 60), 'minute');
  if (abs < 86400) return rtf.format(Math.round(diff / 3600), 'hour');
  if (abs < 7 * 86400) return rtf.format(Math.round(diff / 86400), 'day');
  if (abs < 30 * 86400) return rtf.format(Math.round(diff / (7 * 86400)), 'week');
  return new Date(iso).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

export function fullDate(iso: string): string {
  return new Date(iso).toLocaleString();
}

export function initial(name: string): string {
  return (name.trim().charAt(0) || '?').toUpperCase();
}

export function plural(n: number, word: string): string {
  return `${n} ${word}${n === 1 ? '' : 's'}`;
}
