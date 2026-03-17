import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { fetchSummaryStatus, startSummaryJob } from "./services/api";
import Home from "./components/Home/Home";
import SummaryView from "./components/Summary/Summary";
import "./App.css";

function App() {
  const [url, setUrl] = useState("");
  const [status, setStatus] = useState("idle");
  const [history, setHistory] = useState([]);
  const [currSummaryText, setCurrSummaryText] = useState("");

  const fetchHistory = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/v1/history");
      const data = await response.json();
      setHistory(data);
    } catch (err) {
      console.error("Failed to fetch history", err);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleProcess = async () => {
  if (!url) return;
  setStatus("processing");
  setCurrSummaryText(""); // Clear previous text

  try {
    const data = await startSummaryJob(url);

    // 1. Check if the backend gave us the full summary immediately (Cache Hit)
    if (data.summaryText || data.summary_text) {
      setCurrSummaryText(data.summaryText || data.summary_text);
      setStatus("success");
      fetchHistory(); // Refresh history to show the new "viewed" item
      return;
    }

    // 2. Otherwise, it's a Cache Miss - start polling with the jobId
    const jobId = data.jobId || data; // Handles if backend returns {jobId: "..."} or just "..."
    
    const interval = setInterval(async () => {
      try {
        const statusData = await fetchSummaryStatus(jobId);
        
        if (statusData) {
          setCurrSummaryText(statusData.summaryText || statusData.summary_text);
          setStatus("success");
          clearInterval(interval);
          fetchHistory();
        }
      } catch (err) {
        console.log("Worker still processing...");
      }
    }, 3000);

  } catch (err) {
    console.error("Process error:", err);
    setStatus("error");
  }
};

  return (
    <Router>
      <div className="app-container">
        <Routes>
          <Route
            path="/"
            element={
              <Home
                url={url}
                setUrl={setUrl}
                status={status}
                history={history}
                handleProcess={handleProcess}
                currSummaryText={currSummaryText}
              />
            }
          />
          <Route
            path="/summary/:jobId"
            element={<SummaryView history={history} />}
          />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
