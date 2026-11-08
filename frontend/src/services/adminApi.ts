import { API_BASE_URL, ApiRequestError, getToken } from './api';
export type AdminTab = 'posts'|'reports'|'complaints'|'users'|'payments'|'banks';
async function request(path:string, method='GET', payload?:unknown):Promise<unknown>{
  const res=await fetch(`${API_BASE_URL}/api/admin${path}`,{method,headers:{'Content-Type':'application/json',Authorization:`Bearer ${getToken()||''}`},body:payload===undefined?undefined:JSON.stringify(payload)});
  const json=await res.json().catch(()=>null);
  if(!res.ok)throw new ApiRequestError(json?.message||`HTTP ${res.status}`,json?.error,res.status);
  return json;
}
export const adminApi={
  get:(p:string)=>request(p),post:(p:string,data:unknown)=>request(p,'POST',data),put:(p:string,data:unknown)=>request(p,'PUT',data),
  list:(tab:AdminTab,q='')=>request(({posts:'/posts/pending',reports:'/reports',complaints:'/complaints',users:'/users?q='+encodeURIComponent(q),payments:'/payments/reconciliation',banks:'/bank-accounts'})[tab]),
};
