import React, { useState, useEffect } from 'react';
import { Card, Spinner, Alert } from 'react-bootstrap';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import api from '../services/api';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const TimeSeriesChart = ({ selectedPoint, level }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [timeSeriesData, setTimeSeriesData] = useState(null);
  const [selectedVariable, setSelectedVariable] = useState('temperature');

  useEffect(() => {
    const fetchTimeSeriesData = async () => {
      if (!selectedPoint || !selectedPoint.id) {
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const lat = selectedPoint.id.lat;
        const lon = selectedPoint.id.lon;
        console.log("Fetching time series data for closest point:", lat.toFixed(6), lon.toFixed(6), level);
        
        const response = await api.getTimeSeriesData(
          level,
          lat.toFixed(4),
          lon.toFixed(4)
        );
        
        setTimeSeriesData(response.data);
        console.log("Time series data received:", response.data);
      } catch (err) {
        console.error("Error fetching time series data:", err);
        setError("Unable to load time series data. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchTimeSeriesData();
  }, [selectedPoint, level]);

  const prepareChartData = () => {
    if (!timeSeriesData || timeSeriesData.length === 0) {
      return null;
    }

    const sortedData = [...timeSeriesData].sort((a, b) => 
      new Date(a.id.dateTime) - new Date(b.id.dateTime)
    );

    const labels = sortedData.map(item => {
      const date = new Date(item.id.dateTime);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    });

    const datasets = [];
    
    if (sortedData[0][selectedVariable] !== undefined) {
      datasets.push({
        label: formatVariableName(selectedVariable),
        data: sortedData.map(item => item[selectedVariable]),
        borderColor: 'rgb(53, 162, 235)',
        backgroundColor: 'rgba(53, 162, 235, 0.5)',
      });
    }

    return {
      labels,
      datasets
    };
  };

  const prepareErrorChartData = () => {
    if (!timeSeriesData || timeSeriesData.length === 0) {
      return null;
    }

    const sortedData = [...timeSeriesData].sort((a, b) => 
      new Date(a.id.dateTime) - new Date(b.id.dateTime)
    );

    const labels = sortedData.map(item => {
      const date = new Date(item.id.dateTime);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    });

    const datasets = [];
    
    const errorField = `${selectedVariable}Error`;
    if (sortedData[0][errorField] !== undefined) {
      datasets.push({
        label: `Error ${formatVariableName(selectedVariable)}`,
        data: sortedData.map(item => item[errorField]),
        borderColor: 'rgb(255, 99, 132)',
        backgroundColor: 'rgba(255, 99, 132, 0.5)',
      });
    }

    return {
      labels,
      datasets
    };
  };

  const formatVariableName = (name) => {
    return name
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, str => str.toUpperCase());
  };

  const getAvailableVariables = () => {
    if (!timeSeriesData || timeSeriesData.length === 0) {
      return [];
    }

    return Object.keys(timeSeriesData[0])
      .filter(key => key !== 'id' && !key.endsWith('Error'));
  };

  const chartData = prepareChartData();
  const errorChartData = prepareErrorChartData();

  return (
    <>
      <Card className="mt-4">
        <Card.Header>
          <div className="d-flex justify-content-center align-items-center gap-3">
            <h5 className="mb-0">Time Series Plot of </h5>
            {!loading && timeSeriesData && (
              <select 
                className="form-select form-select-sm" 
                style={{ width: 'auto' }}
                value={selectedVariable}
                onChange={(e) => setSelectedVariable(e.target.value)}
              >
                {getAvailableVariables().map(variable => (
                  <option key={variable} value={variable}>
                    {formatVariableName(variable)}
                  </option>
                ))}
              </select>
            )}
          </div>
        </Card.Header>
        <Card.Body>
          {loading && (
            <div className="text-center p-5">
              <Spinner animation="border" role="status">
                <span className="visually-hidden">Caricamento...</span>
              </Spinner>
              <p className="mt-2">Loading time series data...</p>
            </div>
          )}
          
          {error && (
            <Alert variant="danger">{error}</Alert>
          )}
          
          {!loading && !error && !timeSeriesData && (
            <p className="text-center">Select a point on the map to view the time series.</p>
          )}
          
          {!loading && !error && timeSeriesData && timeSeriesData.length === 0 && (
            <p className="text-center">No data available for this point.</p>
          )}
          
          {!loading && !error && chartData && (
            <div style={{ height: '400px' }}>
              <Line 
                data={chartData} 
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: {
                    legend: {
                      position: 'top',
                    },
                    tooltip: {
                      callbacks: {
                        label: function(context) {
                          let label = context.dataset.label || '';
                          if (label) {
                            label += ': ';
                          }
                          if (context.parsed.y !== null) {
                            label += context.parsed.y.toFixed(4);
                          }
                          return label;
                        }
                      }
                    }
                  },
                  scales: {
                    x: {
                      title: {
                        display: true,
                        text: 'Date and Time'
                      }
                    },
                    y: {
                      title: {
                        display: true,
                        text: formatVariableName(selectedVariable)
                      }
                    }
                  }
                }}
              />
            </div>
          )}
        </Card.Body>
      </Card>
      
      {!loading && !error && errorChartData && errorChartData.datasets.length > 0 && (
        <Card className="mt-4">
          <Card.Header>
            <h5 className="mb-0">Error of {formatVariableName(selectedVariable)}</h5>
          </Card.Header>
          <Card.Body>
            <div style={{ height: '400px' }}>
              <Line 
                data={errorChartData} 
                options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: {
                    legend: {
                      position: 'top',
                    },
                    tooltip: {
                      callbacks: {
                        label: function(context) {
                          let label = context.dataset.label || '';
                          if (label) {
                            label += ': ';
                          }
                          if (context.parsed.y !== null) {
                            label += context.parsed.y.toFixed(4);
                          }
                          return label;
                        }
                      }
                    }
                  },
                  scales: {
                    x: {
                      title: {
                        display: true,
                        text: 'Date and Time'
                      }
                    },
                    y: {
                      title: {
                        display: true,
                        text: `Error ${formatVariableName(selectedVariable)}`
                      }
                    }
                  }
                }}
              />
            </div>
          </Card.Body>
        </Card>
      )}
    </>
  );
};

export default TimeSeriesChart;