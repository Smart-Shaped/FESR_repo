import React, { useState, useEffect, useMemo } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import { Container, Row, Col, Card } from 'react-bootstrap';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import api from '../services/api';
import TimeSeriesChart from './TimeSeriesChart';

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});


function MapClickHandler({ onMapClick, data, parameters }) {
  console.log("MapClickHandler mounted with parameters:", parameters);
  
  const handleClick = async (e) => {
    const { lat, lng } = e.latlng;
    console.log("Map click detected:", lat, lng);
    console.log("Available parameters:", parameters);
    
    
    if (!parameters || !parameters.datetime || !parameters.level) {
      console.error("Missing parameters for API call:", parameters);
      if (data && data.length > 0) {
        const closestPoint = findClosestPoint(lat, lng, data);
        console.log("Using closest point (missing parameters):", closestPoint);
        onMapClick({
          ...closestPoint,
          clickedAt: { lat, lng } 
        });
      }
      return;
    }
    
    try {
      console.log("Attempting API call with:", parameters.datetime, parameters.level, lng.toFixed(6), lat.toFixed(6));
      const response = await api.getPointData(
        parameters.datetime,
        parameters.level,
        lng.toFixed(6),
        lat.toFixed(6)
      );
      
      if (response.data && response.data.length > 0) {
        console.log("Point data received:", response.data[0]);
        onMapClick({
          ...response.data[0],
          clickedAt: { lat, lng }
        });
      } else {
        console.log("No specific data found, searching for nearest point");
        if (data && data.length > 0) {
          const closestPoint = findClosestPoint(lat, lng, data);
          console.log("Nearest point found:", closestPoint);
          onMapClick({
            ...closestPoint,
            clickedAt: { lat, lng }
          });
        }
      }
    } catch (error) {
      console.error("Error retrieving point data:", error);
      if (data && data.length > 0) {
        const closestPoint = findClosestPoint(lat, lng, data);
        console.log("Falling back to nearest point:", closestPoint);
        onMapClick({
          ...closestPoint,
          clickedAt: { lat, lng }
        });
      }
    }
  };
  
  useMapEvents({
    click: handleClick
  });
  
  return null;
}

function findClosestPoint(lat, lng, data) {
  let minDist = Infinity;
  let closestPoint = null;

  data.forEach(point => {
    const pointLat = point.id.lat;
    const pointLng = point.id.lon;
    const somma = Math.pow(lat - pointLat, 2) + Math.pow(lng - pointLng, 2);
    const dist = Math.sqrt(somma);

    if (dist < minDist) {
      minDist = dist;
      closestPoint = point;
    }
  });
  
  if (minDist > 10) {
    console.warn("Warning: the nearest point is very far from the click");
  }

  return closestPoint;
}

const MapSection = ({ parameters }) => {
  const [predictionData, setPredictionData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedPoint, setSelectedPoint] = useState(null);
  const [clickedPosition, setClickedPosition] = useState(null);
  
  useEffect(() => {
    const fetchData = async () => {
      if (!parameters.datetime || !parameters.level) {
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const response = await api.getPredictionData(
          parameters.datetime,
          parameters.level
        );
        
        setPredictionData(response.data);
      } catch (err) {
        console.error('Error fetching map data:', err);
        setError('Failed to fetch map data. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [parameters.datetime, parameters.level]);

  const mapCenter = useMemo(() => {
    if (predictionData && predictionData.length > 0) {
      const lats = predictionData.map(item => item.id.lat);
      const lons = predictionData.map(item => item.id.lon);
      
      return [
        lats.reduce((a, b) => a + b, 0) / lats.length,
        lons.reduce((a, b) => a + b, 0) / lons.length
      ];
    }
    return [41.9028, 12.4964];
  }, [predictionData]);

  const handleMapClick = (point) => {
    console.log("Selected point:", point);
    console.log("Point coordinates:", point.id.lat, point.id.lon);
    console.log("Complete values:", JSON.stringify(point));
    
    if (point.clickedAt) {
      setClickedPosition([point.clickedAt.lat, point.clickedAt.lng]);
    }
    
    setSelectedPoint(null);
    
    setTimeout(() => {
      const newPoint = JSON.parse(JSON.stringify(point));
      setSelectedPoint(newPoint);
      console.log("New point set:", newPoint);
    }, 50);
  };

  return (
    <Container fluid className="mt-5">
      
      {loading && <p className="text-center">Caricamento dati in corso...</p>}
      
      {error && <p className="text-center text-danger">{error}</p>}
      
      {!loading && !error && (
        <>
          <Row className="align-items-center">
            <Col md={8}>
              <h3 className="mb-4 text-center">Interactive Map</h3>
              <div style={{ height: '500px', width: '100%', marginTop: '2rem' }}>
                <MapContainer 
                  center={[mapCenter[0], mapCenter[1]]}
                  zoom={5} 
                  style={{ height: '100%', width: '100%' }}
                >
                  <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                  />
                  
                  {clickedPosition && (
                    <Marker 
                      position={clickedPosition}
                      icon={new L.Icon({
                        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
                        iconSize: [25, 41],
                        iconAnchor: [12, 41],
                        popupAnchor: [1, -34],
                        shadowSize: [41, 41]
                      })}
                    >
                      <Popup>
                        Selected point: {clickedPosition[0].toFixed(4)}, {clickedPosition[1].toFixed(4)}
                      </Popup>
                    </Marker>
                  )}
                  
                  {predictionData && predictionData.map((point, idx) => (
                    <Marker 
                      key={idx}
                      position={[point.id.lat, point.id.lon]}
                      opacity={0}
                      eventHandlers={{
                        click: () => {
                          console.log("Direct click on marker:", point);
                          handleMapClick({
                            ...point,
                            clickedAt: { lat: point.id.lat, lng: point.id.lon }
                          });
                        }
                      }}
                    >
                      <Popup>
                        Lat: {point.id.lat.toFixed(4)}, Lon: {point.id.lon.toFixed(4)}
                      </Popup>
                    </Marker>
                  ))}
                  {console.log("Parameters passed to MapClickHandler:", parameters)}
                  <MapClickHandler 
                    onMapClick={handleMapClick} 
                    data={predictionData} 
                    parameters={{...parameters}}
                  />
                </MapContainer>
              </div>
            </Col>
            <Col md={4}>
              <Card key={selectedPoint ? `point-${Date.now()}` : 'no-point'}>
                <Card.Header>Selected Point Details</Card.Header>
                <Card.Body>
                  {selectedPoint ? (
                    <div>
                      <h5>Coordinates</h5>
                      {selectedPoint.clickedAt && (
                        <>
                          <p><strong>Latitude:</strong> {selectedPoint.clickedAt.lat.toFixed(4)}</p>
                          <p><strong>Longitude:</strong> {selectedPoint.clickedAt.lng.toFixed(4)}</p>
                        </>
                      )}
                      
                      <h5>Values</h5>
                      <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
                        {Object.entries(selectedPoint)
                          .filter(([key]) => key !== 'id' && key !== 'clickedAt')
                          .map(([key, value], index) => (
                            <p key={`${key}-${index}-${Date.now()}`}>
                              <strong>{key}:</strong> {typeof value === 'number' ? value.toFixed(4) : value}
                            </p>
                          ))}
                      </div>
                      
                      <div className="mt-3 alert alert-info">
                        <small>
                          Note: The values shown are related to the closest available point in the dataset.
                          The data grid has a limited resolution and may not have exact values for every coordinate.
                        </small>
                      </div>
                    </div>
                  ) : (
                    <p>Click on the map to select a point and view details.</p>
                  )}
                </Card.Body>
              </Card>
            </Col>
          </Row>
          
          <Row>
            <Col>
              <TimeSeriesChart 
                selectedPoint={selectedPoint} 
                level={parameters.level} 
              />
            </Col>
          </Row>
        </>
      )}
    </Container>
  );
};

export default MapSection;