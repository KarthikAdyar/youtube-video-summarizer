import axios from 'axios'
import { getToken } from './auth'

const API_BASE = 'http://localhost:8081/api/v1';

const client = axios.create();

client.interceptors.request.use((config) => {
    const token = getToken();
    if(token){
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export const startSummaryJob = async (videoUrl) => {
    const response = await client.post(`${API_BASE}/summarize`, { url: videoUrl })
    return response.data;
}

export const fetchSummaryStatus = async(jobId) => {
    try{
        const response = await client.get(`${API_BASE}/status/${jobId}`);
        return response.data;
    }
    catch(error){
        if (error.response && error.response.status === 404) return null;
        throw error;
    }
}

export const fetchHistory = async () => {
    const response = await client.get(`${API_BASE}/history`);
    return response.data;
}