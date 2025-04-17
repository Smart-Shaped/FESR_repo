# GenCast Prediction Viewer

GenCast Prediction Viewer is a web application designed to visualize and analyze meteorological prediction data. The application provides an interactive interface for comparing real data with predicted data, calculating error metrics, and visualizing geographical distributions of meteorological variables. It offers the following features:

- Interactive parameter selection (datetime, atmospheric level, variables)
- Heatmap visualization of meteorological data
- Side-by-side comparison of real and predicted data
- Error visualization with symmetric color scales
- Interactive map interface with Leaflet
- Time series analysis for selected geographical points
- Responsive design for various screen sizes

## Technology Used

- **React.js**: Front-end framework for building reusable UI components
- **Bootstrap**: CSS framework for responsive, mobile-first UI
- **Plotly.js**: Data visualization library for interactive, web-based visualizations
- **Leaflet**: Interactive map library for visualizing geographical data
- **Axios**: Promise-based HTTP client for making API requests to the back-end service

## Getting Started

### Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)

### Installation

1. Install dependencies

```bash
npm install
```

2. Start the development server

```bash
npm start
```

3. Open your browser and navigate to `http://localhost:3000`

## Project Structure

```bash
ccai-gencast-frontend-app/
├── README.md
├── package-lock.json
├── package.json
├── public/
│   ├── 3s-logo.png
│   ├── index.html
│   ├── logo.png
│   └── logo.svg
└── src/
    ├── 3s-logo.png
    ├── App.css
    ├── App.js
    ├── App.test.js
    ├── components/
    │   ├── Footer.js
    │   ├── HeatmapSection.js
    │   ├── MapSection.js
    │   ├── NavBar.js
    │   └── TimeSeriesChart.js
    ├── index.css
    ├── index.js
    ├── logo.png
    ├── logo.svg
    ├── reportWebVitals.js
    ├── services/
    │   └── api.js
    └── setupTests.js
```

## Usage

1. Select parameters from the navigation bar (datetime, level, variable)
2. View the heatmap visualization showing real data, predicted data, and error metrics
3. Interact with the map to select specific geographical points
4. Analyze time series data for selected points

## API Integration

The application connects to a backend API that provides:

- Available parameters (datetimes, levels, variables)
- Geographical data for selected parameters
- Time series data for specific locations
