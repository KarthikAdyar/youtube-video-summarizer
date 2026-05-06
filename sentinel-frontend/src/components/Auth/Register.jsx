import React from 'react';
import { useNavigate } from 'react-router-dom';
import auth from '../../services/auth';

export default function Register(){
  const navigate = useNavigate();

  const startOAuth = () => {
    auth.login('google');
  }

  return (
    <div className="auth-container">
      <h2>Create an account</h2>
      <p>Sign up with your Google account to access your searches and summaries.</p>
      <button className="btn-primary" onClick={startOAuth}>Sign up with Google</button>
      <p style={{marginTop: '1rem', color: '#94a3b8', fontSize: '0.9rem'}}>
        Already have an account? <span className="link" onClick={() => navigate('/login')} style={{color: '#38bdf8', cursor: 'pointer', textDecoration: 'underline'}}>Sign in</span>
      </p>
    </div>
  )
}
