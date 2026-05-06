const TOKEN_KEY = 'sentinel_token';

export function setToken(token){
  if(token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

export function getToken(){
  return localStorage.getItem(TOKEN_KEY);
}

export function logout(){
  setToken(null);
  window.location.href = '/';
}

export function login(provider = 'google'){
  // Query backend to ensure provider is configured, then redirect
  fetch('http://localhost:8081/auth/providers')
    .then(r => r.json())
    .then(list => {
      const found = list.find(p => p.id === provider || p.id === 'google');
      if(!found){
        alert('OAuth provider not configured on server.');
        return;
      }
      window.location.href = `http://localhost:8081/oauth2/authorization/${found.id}`;
    })
    .catch(err => {
      console.error('Failed to query providers', err);
      alert('Failed to start login.');
    });
}

export function parseJwt(token){
  try{
    const parts = token.split('.');
    if(parts.length<2) return null;
    const payload = parts[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decodeURIComponent(escape(json)));
  }catch(e){
    return null;
  }
}

export function getUserFromToken(){
  const token = getToken();
  if(!token) return null;
  const p = parseJwt(token);
  if(!p) return null;
  return { sub: p.sub, exp: p.exp, name: p.name || null, email: p.email || null };
}

export async function register(email, password, name){
  throw new Error('Local registration disabled. Use OAuth login.');
}

export async function localLogin(email, password){
  throw new Error('Local login disabled. Use OAuth login.');
}

// attach to default export for backward compatibility
export default { setToken, getToken, login, logout, getUserFromToken };
