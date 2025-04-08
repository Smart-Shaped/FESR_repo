import React, { useState } from 'react';
import { Container } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import NavBar from './components/NavBar';
import HeatmapSection from './components/HeatmapSection';
import MapSection from './components/MapSection';
import Footer from './components/Footer';

function App() {
  const [parameters, setParameters] = useState({
    datetime: '',
    level: '',
    variable: ''
  });

  const handleParametersChange = (newParameters) => {
    setParameters(newParameters);
  };

  return (
    <div className="App">
      <NavBar onParametersChange={handleParametersChange} />
      <Container fluid>
        <HeatmapSection parameters={parameters} />
        <MapSection parameters={parameters} />
      </Container>
      <Footer />
    </div>
  );
}

export default App;
