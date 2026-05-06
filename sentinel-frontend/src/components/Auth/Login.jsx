import React from 'react';
import auth from '../../services/auth';

export default function Login(){
  const startOAuth = () => {
    auth.login('google');
  }

  return (
    <div className="auth-container">
      <h2>Sign in</h2>
      <p>Sign in with your Google account to access your searches and summaries.</p>
      <button className="btn-primary" onClick={startOAuth}>Sign in with Google</button>
    </div>
  )
}
