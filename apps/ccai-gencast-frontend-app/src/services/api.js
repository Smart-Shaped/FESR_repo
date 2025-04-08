import axios from 'axios';

const API_BASE_URL = 'http://localhost:8070';

const api = {
  getDatetimes: () => {
    return axios.get(`${API_BASE_URL}/gencast/timestamps`);
  },
  
  getLevels: () => {
    return axios.get(`${API_BASE_URL}/gencast/levels`);
  },
  
  getVariables: () => {
    return axios.get(`${API_BASE_URL}/gencast/columnNames/dataVars/excludeErrors`);
  },
  
  getCombinedValues: (datetime, level) => {
    return axios.get(`${API_BASE_URL}/gencast/testSet/filter`, {
      params: { timestamp: datetime, level }
    });
  },
  
  getPredictionData: (datetime, level) => {
    return axios.get(`${API_BASE_URL}/gencast/prediction/filter`, {
      params: { timestamp: datetime, level }
    });
  },
  
  getPointData: (datetime, level, lat, lon) => {
    return axios.get(`${API_BASE_URL}/gencast/prediction/point/filter`, {
      params: { timestamp: datetime, level, lat, lon }
    });
  },
  
  getTimeSeriesData: (level, lat, lon) => {
    return axios.get(`${API_BASE_URL}/gencast/prediction/timeSeries/filter`, {
      params: { level, lat, lon }
    });
  }
};

export default api;