import { API_BASE_URL, getToken, ApiRequestError } from './api';
async function req(path: string, method = 'GET', body?: object): Promise<any> {
  const token = getToken();
  const response = await fetch(`${API_BASE_URL}/api/marketplace${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
  });
  const text = await response.text();
  let data: any = null;
  if (text) {
    try { data = JSON.parse(text); } catch { data = null; }
  }
  if (!response.ok) {
    throw new ApiRequestError(data?.message || `Request failed (${response.status})`, data?.error || 'REQUEST_FAILED', response.status);
  }
  return data;
}
export const marketplaceApi={
  comments:(id:number)=>req(`/posts/${id}/comments`),addComment:(id:number,content:string)=>req(`/posts/${id}/comments`,'POST',{content}),editComment:(id:number,content:string)=>req(`/comments/${id}`,'PUT',{content}),deleteComment:(id:number)=>req(`/comments/${id}`,'DELETE'),
  favorites:()=>req('/favorites'),addFavorite:(id:number)=>req(`/favorites/${id}`,'POST'),removeFavorite:(id:number)=>req(`/favorites/${id}`,'DELETE'),
  following:()=>req('/following'),follow:(id:number)=>req(`/following/${id}`,'POST'),unfollow:(id:number)=>req(`/following/${id}`,'DELETE'),userPosts:(id:number)=>req(`/users/${id}/posts`),
  notifications:()=>req('/notifications'),read:(id:number)=>req(`/notifications/${id}/read`,'PUT'),readAll:()=>req('/notifications/read-all','PUT'),
  report:(id:number,reason:string)=>req(`/posts/${id}/reports`,'POST',{reason}),reports:()=>req('/reports/mine')
};
