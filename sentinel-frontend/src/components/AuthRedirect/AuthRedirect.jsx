import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { setToken } from '../../services/auth';

const AuthRedirect = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if(token){
      setToken(token);
    }
    // remove token param from url and navigate home
    navigate('/');
  }, [navigate]);

  return <div>Finishing login...</div>
}

export default AuthRedirect;
