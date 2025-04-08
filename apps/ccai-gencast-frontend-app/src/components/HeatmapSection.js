import React, { useEffect, useState, useRef, useMemo } from 'react';
import Plot from 'react-plotly.js';
import api from '../services/api';
import { Container, Row, Col, Spinner } from 'react-bootstrap';

const HeatmapSection = ({ parameters }) => {
  const [originalData, setOriginalData] = useState(null);
  const [predictionData, setPredictionData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [updateCounter, setUpdateCounter] = useState(0);
  const lastLoadedParams = useRef({ datetime: '', level: '' });

  const processHeatmapData = (data, variableName) => {
    if (!data || !data.length) {
      console.warn(`No data available to process ${variableName}`);
      return null;
    }

    const variableExists = data.some(item => variableName in item);
    if (!variableExists) {
      console.warn(`The variable "${variableName}" does not exist in the data. Available keys: ${Object.keys(data[0]).join(', ')}`);
      return null;
    }

    const latitudes = [...new Set(data.map(item => item.id.lat))].sort((a, b) => a - b);
    const longitudes = [...new Set(data.map(item => item.id.lon))].sort((a, b) => a - b);
    const zValues = Array(latitudes.length).fill().map(() => Array(longitudes.length).fill(null));

    let validValuesCount = 0;
    let nullValuesCount = 0;
    let undefinedValuesCount = 0;
    data.forEach(item => {
      const latIndex = latitudes.indexOf(item.id.lat);
      const lonIndex = longitudes.indexOf(item.id.lon);
      
      if (item[variableName] === null) nullValuesCount++;
      if (item[variableName] === undefined) undefinedValuesCount++;
      
      if (latIndex !== -1 && lonIndex !== -1 && item[variableName] !== null && item[variableName] !== undefined) {
        zValues[latIndex][lonIndex] = item[variableName];
        validValuesCount++;
      }
    });

    if (validValuesCount === 0) {
      console.warn(`No valid values found for variable "${variableName}". Null values: ${nullValuesCount}, undefined: ${undefinedValuesCount}, total elements: ${data.length}`);
      return null;
    }

    console.log(`Processed ${validValuesCount} valid values for ${variableName} (out of ${data.length} total)`);
    return {
      x: longitudes,
      y: latitudes,
      z: zValues
    };
  };

  useEffect(() => {
    if (parameters.variable) {
      setUpdateCounter(prev => prev + 1);
    }
  }, [parameters.variable]);

  useEffect(() => {
    const fetchData = async () => {
      if (!parameters.datetime || !parameters.level || !parameters.variable) {
        return;
      }

      if (parameters.datetime === lastLoadedParams.current.datetime && 
          parameters.level === lastLoadedParams.current.level &&
          originalData && predictionData) {
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const responseTestDataset = await api.getCombinedValues(
          parameters.datetime,
          parameters.level,
        );

        const responsePredictionDataset = await api.getPredictionData(
          parameters.datetime,
          parameters.level,
        );

        setOriginalData(responseTestDataset.data);
        setPredictionData(responsePredictionDataset.data);
        
        lastLoadedParams.current = {
          datetime: parameters.datetime,
          level: parameters.level
        };
      } catch (err) {
        console.error('Error fetching heatmap data:', err);
        setError('Failed to fetch data. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [parameters.datetime, parameters.level, parameters.variable, originalData, predictionData]);

  const processedData = useMemo(() => {
    if (!originalData || !predictionData || !parameters.variable) {
      return { targetData: null, predictionData: null, errorData: null };
    }

    const targetDataProcessed = processHeatmapData(originalData, parameters.variable);
    const predictionDataProcessed = processHeatmapData(predictionData, parameters.variable);
    
    const errorVariableName = `${parameters.variable}Error`;
    let errorData = processHeatmapData(predictionData, errorVariableName);
    
    if (!errorData && targetDataProcessed && predictionDataProcessed) {
      console.log(`Calcolando la differenza manualmente per ${parameters.variable}`);
    }

    return {
      targetData: targetDataProcessed,
      predictionData: predictionDataProcessed,
      errorData
    };
  }, [originalData, predictionData, parameters.variable]);

  const createHeatmapConfig = (data, title, colorscale = 'Viridis', isErrorPlot = false) => {
    if (!data) return null;

    const displayTitle = data.mappedName !== data.originalName 
      ? `${title} (${data.mappedName})` 
      : title;
    
    let zmin, zmax;
    if (isErrorPlot) {
      let maxAbsValue = 0;
      for (let i = 0; i < data.z.length; i++) {
        for (let j = 0; j < data.z[i].length; j++) {
          if (data.z[i][j] !== null) {
            maxAbsValue = Math.max(maxAbsValue, Math.abs(data.z[i][j]));
          }
        }
      }
      zmin = -maxAbsValue;
      zmax = maxAbsValue;
      console.log(`Symmetric range for error plot: ${zmin} to ${zmax}`);
    }

    return {
      data: [{
        type: 'contour',
        x: data.x, 
        y: data.y, 
        z: data.z,
        colorscale: colorscale,
        contours: {
          coloring: 'heatmap',
          showlabels: true,
        },
        colorbar: {
          title: displayTitle,
          thickness: 20,
          orientation: 'h',  
          y: -0.2,           
          yanchor: 'top',    
          len: 0.9,          
          x: 0.5,            
          xanchor: 'center', 
        },
        hoverinfo: 'x+y+z',
        zmin: isErrorPlot ? zmin : undefined,
        zmax: isErrorPlot ? zmax : undefined,
      }],
      layout: {
        title: displayTitle,
        xaxis: {
          title: 'Longitude',
          range: [Math.min(...data.x), Math.max(...data.x)],
        },
        yaxis: {
          title: 'Latitude',
          range: [Math.min(...data.y), Math.max(...data.y)],
          scaleanchor: 'x',
          scaleratio: 1,
        },
        margin: { t: 50, r: 0, l: 50, b: 120 },
        height: 480,
        width: 400,
        geo: {
          scope: 'world',
          resolution: 50,
          showland: true,
          landcolor: 'rgb(217, 217, 217)',
          showocean: true,
          oceancolor: 'rgb(255, 255, 255)',
          showlakes: true,
          lakecolor: 'rgb(255, 255, 255)',
          showrivers: true,
          rivercolor: 'rgb(255, 255, 255)',
          showcountries: true,
          countrycolor: 'rgb(255, 255, 255)',
          showcoastlines: true,
          coastlinecolor: 'rgb(255, 255, 255)',
          projection: {
            type: 'mercator'
          }
        },
      }
    };
  };

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}>
        <Spinner animation="border" />
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ height: '400px' }}>
        <div className="text-danger">{error}</div>
      </Container>
    );
  }

  return (
    <Container fluid className="mt-4">
      <Row>
        <Col md={4}>
          <div className="heatmap-container">
            <h4 className="text-center">Real Data</h4>
            {processedData.targetData ? (
              <Plot
                key={`target-${parameters.variable}-${updateCounter}`}
                {...createHeatmapConfig(processedData.targetData, parameters.variable)}
                config={{ responsive: true }}
              />
            ) : (
              <div className="text-center">
                <p>No data available for variable: {parameters.variable}</p>
                <small className="text-muted">Check console for details</small>
              </div>
            )}
          </div>
        </Col>
        <Col md={4}>
          <div className="heatmap-container">
            <h4 className="text-center">Predicted Data</h4>
            {processedData.predictionData ? (
              <Plot
                key={`prediction-${parameters.variable}-${updateCounter}`}
                {...createHeatmapConfig(processedData.predictionData, parameters.variable)}
                config={{ responsive: true }}
              />
            ) : (
              <div className="text-center">
                <p>No prediction data available</p>
                <small className="text-muted">Check console for details</small>
              </div>
            )}
          </div>
        </Col>
        <Col md={4}>
          <div className="heatmap-container">
            <h4 className="text-center">Delta</h4>
            {processedData.errorData ? (
              <Plot
                key={`error-${parameters.variable}-${updateCounter}`}
                {...createHeatmapConfig(
                  processedData.errorData, 
                  `${parameters.variable} Error`, 
                  [
                    [0, 'red'],
                    [0.5, 'white'],
                    [1, 'blue']
                  ],
                  true
                )}
                config={{ responsive: true }}
              />
            ) : (
              <div className="text-center">
                <p>No error data available</p>
                <small className="text-muted">Check console for details</small>
              </div>
            )}
          </div>
        </Col>
      </Row>
    </Container>
  );
};

export default HeatmapSection;