import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { askQuestion } from '../../services/api';
import './summary.css';

const SummaryView = ({ history }) => {
  const { jobId } = useParams();
  const navigate = useNavigate();

  const [question, setQuestion] = useState('');
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(false);

  // Find the summary in state. If refreshed, we could fetch from API here.
  const item = history.find(h => h.jobId === jobId);

  const handleAsk = async () => {
    if (!question.trim() || !item) return;

    const userQuestion = question;
    setQuestion('');
    setMessages(prev => [...prev, { role: 'user', text: userQuestion }]);
    setLoading(true);

    try {
      const data = await askQuestion(item.videoId, userQuestion);
      setMessages(prev => [...prev, { role: 'assistant', text: data.answer }]);
    } catch (err) {
      setMessages(prev => [...prev, { role: 'assistant', text: 'Sorry, failed to get an answer.' }]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleAsk();
    }
  };

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

      {/* Chat / Q&A Section */}
      <div className="qa-section" style={{ marginTop: '3rem' }}>
        <h3 className="qa-title">Ask about this video</h3>
        
        <div className="qa-messages">
          {messages.length === 0 && (
            <p className="qa-placeholder">Ask a question about the video content...</p>
          )}
          {messages.map((msg, i) => (
            <div key={i} className={`qa-message qa-${msg.role}`}>
              <div className="qa-bubble">{msg.text}</div>
            </div>
          ))}
          {loading && (
            <div className="qa-message qa-assistant">
              <div className="qa-bubble qa-thinking">Thinking...</div>
            </div>
          )}
        </div>

        <div className="qa-input-row">
          <input
            className="qa-input"
            placeholder="e.g. What were the main takeaways?"
            value={question}
            onChange={e => setQuestion(e.target.value)}
            onKeyDown={handleKeyDown}
            disabled={loading}
          />
          <button className="btn-primary qa-send" onClick={handleAsk} disabled={loading || !question.trim()}>
            Ask
          </button>
        </div>
      </div>
    </div>
  );
};

export default SummaryView;