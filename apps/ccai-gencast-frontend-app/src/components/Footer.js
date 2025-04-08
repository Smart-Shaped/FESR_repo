import React from 'react';
import { Container } from 'react-bootstrap';

const Footer = () => {
  return (
    <footer className="mt-5 py-3 bg-light">
      <Container fluid className="text-center">
        <div className="mb-3">
          <img 
            src={process.env.PUBLIC_URL + '../3s-logo.png'} 
            alt="FINANZIATO DALLA REGIONE ABRUZZO A VALERE SUL PR FESR ABRUZZO 2021-2027, CODICE CUP C29J24000080007"
            className="img-fluid footer-banner"
            style={{ 
              maxHeight: '150px', 
              width: 'auto',
              maxWidth: '90%'
            }}
          />
        </div>
        <p className="text-muted small">© {new Date().getFullYear()} Smart Shaped srl</p>
      </Container>
    </footer>
  );
};

export default Footer;