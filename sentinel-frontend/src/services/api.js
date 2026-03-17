import axios from 'axios'

const API_BASE = 'http://localhost:8081/api/v1';

export const startSummaryJob = async (videoUrl) => {
    const response = await axios.post(`${API_BASE}/summarize`, { url: videoUrl })
    return response.data;
}

export const fetchSummaryStatus = async(jobId) => {
    try{
        const response = await axios.get(`${API_BASE}/status/${jobId}`);
        return response.data;
    }
    catch(error){
        if (error.response && error.response.status === 404) return null;
        throw error;
    }
}