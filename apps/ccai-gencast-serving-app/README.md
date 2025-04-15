# GenCast Serving

A Spring Boot application that provides a REST API for accessing GenCast weather prediction data stored in a Cassandra database.

## Overview

GenCast Serving is designed to serve meteorological prediction data through a set of RESTful endpoints. The application allows filtering data by various parameters such as level, timestamp, latitude, and longitude, making it easy to retrieve specific weather predictions.

## Features

- Retrieve weather predictions with various filtering options
- Get metadata about available data (levels, timestamps, column names)
- Combined data view that merges prediction values with their error margins
- Time series data for specific geographical points
- OpenAPI documentation for all endpoints

## Technology Stack

- Java 21
- Spring Boot 3.4.2
- Spring Data Cassandra
- Springdoc OpenAPI
- Lombok
- Maven

## Prerequisites

- JDK 21
- Apache Cassandra
- Maven

## Configuration

The application connects to a Cassandra database. Configuration can be found in `application.properties`:

## Properties
- spring.cassandra.keyspace-name=ml_keyspace
- spring.cassandra.contact-points=localhost
- spring.cassandra.port=9042
- spring.cassandra.local-datacenter=datacenter1
- spring.cassandra.schema-action=CREATE_IF_NOT_EXISTS

## API Endpoints 

- /gencast/first500 - Get first 500 rows of GenCast predictions
- /gencast/timestamps - Get all unique timestamps
- /gencast/levels - Get all unique levels
- /gencast/combinedValues500 - Get first 500 combined Gencast predictions
- /gencast/testSet/filter - Get filtered points by level and timestamp
- /gencast/prediction/filter - Get Gencast predictions by level and timestamp
- /gencast/prediction/point/filter - Get filtered points by level, timestamp, latitude, and longitude
- /gencast/prediction/timeSeries/filter - Get filtered points by level, latitude, and longitude
- /gencast/columnNames/dataVars - Get all column names excluding IDs
- /gencast/columnNames/dataVars/excludeErrors - Get column names excluding errors

## Building and running the application 

To build the project:

```bash
mvn clean package
 ```

To run the application:

```bash
java -jar target/gencast_serving-1.0.0-SNAPSHOT.jar
 ```

Or using Maven:

```bash
mvn spring-boot:run
 ```

## API Documentation
Once the application is running, you can access the OpenAPI documentation at:

```plaintext
http://localhost:8080/swagger-ui.html
 ```

## Data Model
The application uses the following main data models:

- GencastModel - Represents the weather prediction data with error margins
- GencastCombinedModel - Represents the combined prediction data (value + error)
- GencastModelId - Composite primary key for the Cassandra table