# GenCast Serving

A Spring Boot application that provides a REST API for accessing GenCast weather prediction data stored in a Cassandra database.
The application serves meteorological prediction data through a set of RESTful endpoints, allowing filtering by various parameters such as level, timestamp, latitude, and longitude, making it easy to retrieve specific weather predictions. It also provides metadata about available data (levels, timestamps, column names), a combined data view that merges prediction values with their error margins, and time series data for specific geographical points. Finally, it provides OpenAPI documentation for all endpoints.

## Technology Used

- **Spring Boot**: Used as the web application framework for the REST API.
- **Springdoc OpenAPI**: Used to generate OpenAPI documentation for the REST API.
- **Apache Cassandra**: Used as the NoSQL database to store the GenCast weather prediction data.

## Configuration

The application connects to a Cassandra database. Configuration can be found in `application.properties`:

- spring.cassandra.keyspace-name=ml_keyspace
- spring.cassandra.contact-points=localhost
- spring.cassandra.port=9042
- spring.cassandra.local-datacenter=datacenter1
- spring.cassandra.schema-action=CREATE_IF_NOT_EXISTS

## Data Model

The application uses the following main data models:

- GencastModel - Represents the weather prediction data with error margins
- GencastCombinedModel - Represents the combined prediction data (value + error)
- GencastModelId - Composite primary key for the Cassandra table

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
