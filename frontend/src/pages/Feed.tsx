import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type Post } from '../lib/api';
import { useAuth } from '../lib/auth';
import { useDocumentTitle } from '../lib/useDocumentTitle';
import { PostCard } from '../components/PostCard';
import { Empty, FeedSkeleton } from '../components/ui';

type Sort = 'top' | 'latest';

export function usePostFeed(params: { sort: Sort; topicId?: number }) {
  const [posts, setPosts] = useState<Post[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth();

  const load = useCallback(
    async (p: number, append: boolean) => {
      append ? setLoadingMore(true) : setLoading(true);
      setError(null);
      try {
        const res = await api.posts({ sort: params.sort, topicId: params.topicId, page: p, size: 10 });
        setPosts(prev => (append ? [...prev, ...res.items] : res.items));
        setPage(res.page);
        setHasMore(res.hasMore);
      } catch (e) {
        setError((e as Error).message);
      } finally {
        setLoading(false);
        setLoadingMore(false);
      }
    },
    [params.sort, params.topicId],
  );

  // Reload when the sort, topic or signed-in user changes (reactions are per user).
  useEffect(() => {
    load(0, false);
  }, [load, user?.id]);

  const replace = (p: Post) => setPosts(prev => prev.map(x => (x.id === p.id ? p : x)));

  return { posts, hasMore, loading, loadingMore, error, loadMore: () => load(page + 1, true), replace };
}

export function PostFeed({ sort, topicId, hideTopic, emptyMessage }: { sort: Sort; topicId?: number; hideTopic?: boolean; emptyMessage: string }) {
  const feed = usePostFeed({ sort, topicId });
  const { user } = useAuth();

  if (feed.loading) return <FeedSkeleton />;
  if (feed.error) return <Empty>{feed.error}</Empty>;
  if (feed.posts.length === 0) {
    return (
      <Empty>
        <p>{emptyMessage}</p>
        {user ? (
          <Link className="btn" to={topicId ? `/posts/new?topic=${topicId}` : '/posts/new'}>
            Post something
          </Link>
        ) : (
          <Link className="btn" to="/register">
            Join to post
          </Link>
        )}
      </Empty>
    );
  }
  return (
    <>
      <div className="feed">
        {feed.posts.map(p => (
          <PostCard key={p.id} post={p} hideTopic={hideTopic} onChange={feed.replace} />
        ))}
      </div>
      {feed.hasMore && (
        <div className="center mt">
          <button type="button" className="btn secondary" onClick={feed.loadMore} disabled={feed.loadingMore}>
            {feed.loadingMore ? 'Loading…' : 'Load more'}
          </button>
        </div>
      )}
    </>
  );
}

export function FeedPage() {
  const { user } = useAuth();
  const [sort, setSort] = useState<Sort>('top');
  useDocumentTitle();

  return (
    <>
      {!user && (
        <section className="hero">
          <span className="eyebrow">A private club for quotes</span>
          <h1>
            The things people say.
            <br />
            Overheard, posted, reacted to.
          </h1>
          <p>
            Overheard Club is a small feed of the funny, strange and brilliant things you catch in passing. Read along, or join
            to post your own and react to everyone else's.
          </p>
          <div className="row wrap">
            <Link className="btn" to="/register">
              Join the club
            </Link>
            <Link className="btn secondary" to="/login">
              Sign in
            </Link>
          </div>
          <ul className="hero-reactions" aria-hidden="true">
            <li>👍</li>
            <li>❤️</li>
            <li>😂</li>
            <li>🤯</li>
            <li>🙄</li>
          </ul>
        </section>
      )}

      <div className="page-header">
        <div>
          <span className="eyebrow">Feed</span>
          <h1>{sort === 'top' ? 'Top this week' : 'Latest'}</h1>
        </div>
        <div className="segmented" role="tablist">
          <button type="button" role="tab" className={sort === 'top' ? 'active' : ''} aria-selected={sort === 'top'} onClick={() => setSort('top')}>
            Top
          </button>
          <button type="button" role="tab" className={sort === 'latest' ? 'active' : ''} aria-selected={sort === 'latest'} onClick={() => setSort('latest')}>
            Latest
          </button>
        </div>
      </div>

      <PostFeed sort={sort} emptyMessage={sort === 'top' ? 'Nothing has been posted this week yet.' : 'Nothing here yet.'} />
    </>
  );
}
