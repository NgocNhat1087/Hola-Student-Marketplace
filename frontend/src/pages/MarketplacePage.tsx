import { useEffect, useState } from 'react';
import { marketplaceApi as api } from '../services/marketplaceApi';
import { useAuth } from '../context/AuthContext';

type Item = Record<string, any>;
export function MarketplacePage() {
  const { user } = useAuth();
  const [postId, setPostId] = useState('1');
  const [userId, setUserId] = useState('1');
  const [content, setContent] = useState('');
  const [reason, setReason] = useState('');
  const [comments, setComments] = useState<Item[]>([]);
  const [favorites, setFavorites] = useState<Item[]>([]);
  const [following, setFollowing] = useState<Item[]>([]);
  const [posts, setPosts] = useState<Item[]>([]);
  const [notifications, setNotifications] = useState<Item[]>([]);
  const [reports, setReports] = useState<Item[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const run = async (action: () => Promise<unknown>) => { try { setError(''); await action(); setMessage('Action completed'); await refresh(); } catch (e) { setError(e instanceof Error ? e.message : 'Request failed'); } };
  const refresh = async () => {
    if (!user) return;
    const result = await Promise.allSettled([api.favorites(),api.following(),api.notifications(),api.reports()]);
    if(result[0].status==='fulfilled') setFavorites(result[0].value);
    if(result[1].status==='fulfilled') setFollowing(result[1].value);
    if(result[2].status==='fulfilled') setNotifications(result[2].value);
    if(result[3].status==='fulfilled') setReports(result[3].value);
  };
  useEffect(() => { refresh(); }, [user]);
  const id = () => { const n=Number(postId); if(!Number.isSafeInteger(n)||n<=0) throw new Error('Invalid post ID'); return n; };
  const uid = () => { const n=Number(userId); if(!Number.isSafeInteger(n)||n<=0) throw new Error('Invalid user ID'); return n; };
  if (!user) return <div className="container section"><h1>Marketplace</h1><p>Please log in to use marketplace features.</p></div>;
  return <div className="container section marketplace"><h1>Marketplace features</h1><p>UC19 · UC20 · UC21 · UC22 · UC23</p>{message&&<p role="status">{message}</p>}{error&&<p role="alert" style={{color:'crimson'}}>{error}</p>}
    <section><h2>UC19 · Product comments</h2><label>Post ID <input type="number" min="1" value={postId} onChange={e=>setPostId(e.target.value)}/></label><button onClick={()=>run(async()=>setComments(await api.comments(id())))}>Load comments</button><textarea placeholder="Your comment" value={content} onChange={e=>setContent(e.target.value)}/><button onClick={()=>run(async()=>{await api.addComment(id(),content);setContent('');setComments(await api.comments(id()));})}>Add comment</button>{comments.map(c=><article key={c.id}><p>#{c.id} · User {c.userId}: {c.content}</p>{String(c.userId)===String(user.id)&&<><button onClick={()=>{const next=window.prompt('Edit comment',c.content);if(next!==null)run(async()=>{await api.editComment(c.id,next);setComments(await api.comments(id()));});}}>Edit</button><button onClick={()=>run(async()=>{await api.deleteComment(c.id);setComments(await api.comments(id()));})}>Delete</button></>}</article>)}</section>
    <section><h2>UC20 · Favorites</h2><button onClick={()=>run(()=>api.addFavorite(id()))}>Favorite post #{postId}</button>{favorites.map(f=><article key={f.postId}>{f.title} (#{f.postId}) <button onClick={()=>run(()=>api.removeFavorite(f.postId))}>Remove</button></article>)}</section>
    <section><h2>UC21 · Follow users</h2><label>User ID <input type="number" min="1" value={userId} onChange={e=>setUserId(e.target.value)}/></label><button onClick={()=>run(()=>api.follow(uid()))}>Follow</button><button onClick={()=>run(async()=>setPosts(await api.userPosts(uid())))}>View user's posts</button>{posts.map(p=><article key={p.id}>#{p.id} · {p.title}</article>)}<h3>Following</h3>{following.map(f=><article key={f.userId}>{f.email} (#{f.userId}) <button onClick={()=>run(()=>api.unfollow(f.userId))}>Unfollow</button></article>)}</section>
    <section><h2>UC22 · Notifications</h2><button onClick={()=>run(()=>api.readAll())}>Mark all read</button><button onClick={()=>run(async()=>refresh())}>Refresh</button>{notifications.map(n=><article key={n.id}><strong>{n.title}</strong> · {n.content} · {n.isRead?'Read':'Unread'} {!n.isRead&&<button onClick={()=>run(()=>api.read(n.id))}>Mark read</button>}</article>)}</section>
    <section><h2>UC23 · Report a violation</h2><p>Report post #{postId}</p><textarea placeholder="Reason for reporting" value={reason} onChange={e=>setReason(e.target.value)}/><button onClick={()=>run(async()=>{await api.report(id(),reason);setReason('');})}>Submit report</button><h3>My reports</h3>{reports.map(r=><article key={r.id}>Post #{r.postId} · {r.reason} · {r.status}</article>)}</section>
  </div>;
}
