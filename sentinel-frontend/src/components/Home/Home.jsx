import React from 'react';
import { useNavigate } from 'react-router-dom';
import './home.css';
import auth, { getUserFromToken } from '../../services/auth';

const Home = ({ url, setUrl, status, history, handleProcess, currSummaryText }) => {
  const navigate = useNavigate();
  const user = getUserFromToken();

  return (
    <div className="max-width-wrapper">
      <header className="header">
        <div className="header-left">
          <h1>SENTINEL</h1>
          <p className="subtitle">High-fidelity YouTube Summaries</p>
        </div>
        <div>
          {user ? (
            <div className="user-info">
              <div className="user-avatar">{((user.name || user.email || user.sub).charAt(0)).toUpperCase()}</div>
              <span className="user-greeting">Hi {user.name || user.email || user.sub}</span>
              <button className="btn-logout" onClick={() => auth.logout()}>Logout</button>
            </div>
          ) : (
            <button className="btn-login" onClick={() => navigate('/login')}>Sign In</button>
          )}
        </div>
      </header>

        <div className="input-group">
        <input 
          placeholder="Paste YouTube Link..."
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          disabled={!user}
        />
        <button 
          className="btn-primary"
          onClick={handleProcess}
          disabled={status === 'processing' || !url || !user}
        >
          {status === 'processing' ? 'PROCESSING' : 'SUMMARIZE'}
        </button>
      </div>

      {!user && (
        <p className="login-prompt">
          Sign in with <span onClick={() => navigate('/login')}>Google</span> to summarize videos
        </p>
      )}

      {status === 'processing' && (
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p className="loading-text">DISTILLING VIDEO...</p>
        </div>
      )}

      {currSummaryText && <p className='summary-text'>{currSummaryText}</p>}



      {history.length > 0 && (
        <div className="history-section">
          <h2 className="history-title">RECENTLY VIEWED</h2>
          <div className="history-scroll-container">
            {history.map((item) => (
              <div 
                key={item.jobId} 
                className="history-card"
                onClick={() => navigate(`/summary/${item.jobId}`)}
              >
                <h3>{item.title || "Video Analysis"}</h3>
                <p className="history-preview">
                  {item.summaryText || item.summary_text}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Home;