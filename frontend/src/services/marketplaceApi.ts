import { API_BASE_URL, getToken, ApiRequestError } from './api';
async function req(path:string,method='GET',body?:object):Promise<any>{
  const response=await fetch(`${API_BASE_URL}/api/marketplace${path}`,{method,headers:{'Content-Type':'application/json',...(getToken()?{Authorization:`Bearer ${getToken()}`}:{})},body:body?JSON.stringify(body):undefined});
  const data=await response.json();if(!response.ok)throw new ApiRequestError(data.message||'Request failed',data.error,response.status);return data;
}
export const marketplaceApi={
  comments:(id:number)=>req(`/posts/${id}/comments`),addComment:(id:number,content:string)=>req(`/posts/${id}/comments`,'POST',{content}),editComment:(id:number,content:string)=>req(`/comments/${id}`,'PUT',{content}),deleteComment:(id:number)=>req(`/comments/${id}`,'DELETE'),
  favorites:()=>req('/favorites'),addFavorite:(id:number)=>req(`/favorites/${id}`,'POST'),removeFavorite:(id:number)=>req(`/favorites/${id}`,'DELETE'),
  following:()=>req('/following'),follow:(id:number)=>req(`/following/${id}`,'POST'),unfollow:(id:number)=>req(`/following/${id}`,'DELETE'),userPosts:(id:number)=>req(`/users/${id}/posts`),
  notifications:()=>req('/notifications'),read:(id:number)=>req(`/notifications/${id}/read`,'PUT'),readAll:()=>req('/notifications/read-all','PUT'),
  report:(id:number,reason:string)=>req(`/posts/${id}/reports`,'POST',{reason}),reports:()=>req('/reports/mine')
};
