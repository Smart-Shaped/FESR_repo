import React, { useEffect, useState } from 'react';
import { Navbar, Container, Nav, NavDropdown, Button } from 'react-bootstrap';
import api from '../services/api';

const NavBar = ({ onParametersChange }) => {
  const [datetimes, setDatetimes] = useState([]);
  const [levels, setLevels] = useState([]);
  const [variables, setVariables] = useState([]);
  const [selectedDatetime, setSelectedDatetime] = useState('');
  const [selectedLevel, setSelectedLevel] = useState('');
  const [selectedVariable, setSelectedVariable] = useState('');

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [datetimesRes, levelsRes, variablesRes] = await Promise.all([
          api.getDatetimes(),
          api.getLevels(),
          api.getVariables()
        ]);
        
        setDatetimes(datetimesRes.data);
        setLevels(levelsRes.data);
        setVariables(variablesRes.data);
        
        if (datetimesRes.data.length > 0) setSelectedDatetime(datetimesRes.data[0]);
        if (levelsRes.data.length > 0) setSelectedLevel(levelsRes.data[0]);
        if (variablesRes.data.length > 0) setSelectedVariable(variablesRes.data[5]);
      } catch (error) {
        console.error('Error fetching dropdown options:', error);
      }
    };
    
    fetchOptions();
  }, []);

  const handleQueryClick = () => {
    if (selectedDatetime && selectedLevel && selectedVariable) {
      onParametersChange({
        datetime: selectedDatetime,
        level: selectedLevel,
        variable: selectedVariable
      });
    } else {
      alert('Please select all parameters before running the query');
    }
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="p-0">
      <Container fluid>
        <Navbar.Brand href="#home" className="d-flex align-items-center p-0">
          <img
            src={process.env.PUBLIC_URL + '/logo.svg'}
            width="200"
            height="60"
            className="d-inline-block align-top"
            alt="Logo"
            onError={(e) => {
              console.error('Error loading logo:', e);
              e.target.src = 'https://via.placeholder.com/200x60';
              e.target.onerror = null;
            }}
          />
          <span className="fs-4 ms-3">GenCast Prediction Viewer</span>
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto d-flex align-items-center">
            <NavDropdown title={`Datetime: ${selectedDatetime || 'Select'}`} id="datetime-dropdown" className="mx-2">
              {datetimes.map((datetime, index) => (
                <NavDropdown.Item 
                  key={index} 
                  onClick={() => setSelectedDatetime(datetime)}
                >
                  {datetime}
                </NavDropdown.Item>
              ))}
            </NavDropdown>
            
            <NavDropdown title={`Level: ${selectedLevel || 'Select'}`} id="level-dropdown" className="mx-2">
              {levels.map((level, index) => (
                <NavDropdown.Item 
                  key={index} 
                  onClick={() => setSelectedLevel(level)}
                >
                  {level}
                </NavDropdown.Item>
              ))}
            </NavDropdown>
            
            <NavDropdown title={`Variable: ${selectedVariable || 'Select'}`} id="variable-dropdown" className="mx-2">
              {variables.map((variable, index) => (
                <NavDropdown.Item 
                  key={index} 
                  onClick={() => setSelectedVariable(variable)}
                >
                  {variable}
                </NavDropdown.Item>
              ))}
            </NavDropdown>
            
            <Button 
              variant="success" 
              className="query-button"
              onClick={handleQueryClick}
            >
              Run Query
            </Button>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavBar;