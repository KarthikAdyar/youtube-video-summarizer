import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './summary.css';

const SummaryView = ({ history }) => {
  const { jobId } = useParams();
  const navigate = useNavigate();

  // Find the summary in state. If refreshed, we could fetch from API here.
  const item = history.find(h => h.jobId === jobId);

  if (!item) {
    return (
      <div className="max-width-wrapper">
        <button onClick={() => navigate('/')} className="btn-secondary">← BACK</button>
        <p style={{ marginTop: '2rem', color: 'white' }}>Loading summary data...</p>
      </div>
    );
  }

  return (
    <div className="max-width-wrapper animate-in fade-in">
      <button onClick={() => navigate('/')} className="btn-secondary">← BACK TO DASHBOARD</button>
      
      <div className="result-card" style={{ marginTop: '2.5rem' }}>
        <h2>{item.title || "Video Analysis"}</h2>
        <div className="summary-text">
          {item.summaryText || item.summary_text}
        </div>
      </div>
    </div>
  );
};

export default SummaryView;