# CCAI Surface Data Fusion App

This project is a data fusion service developed by Smart Shaped, focused on integrating and processing data from Surface for advanced analytics and operational needs.

## Technologies Used

- **Apache Spark**: Used for big data processing.
- **Apache Cassandra**: Used to store informations for data fusion.
- **Apache Hadoop**: Supports integration with Hadoop Distributed File System for large-scale data storage.

## Directory Structure

```bash
src/
├── main/
│   ├── java/...
│   └── resources/
│       ├── framework-config.yml
│       ├── local-config.yml
│       └── typeMapping.yml
```

## How to Run

- Cassandra database setup:

  - create a keyspace called `data_fusion_keyspace` with a replication factor of 1, according to the query

    ```sql
    CREATE KEYSPACE IF NOT EXISTS data_fusion_keyspace
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
    ```

  - create table called `request` with this query

    ```sql
    CREATE TABLE IF NOT EXISTS data_fusion_keyspace.request (
        id UUID PRIMARY KEY,
        content TEXT,
        datafusionids TEXT,
        state TEXT
    );
    ```

  - Create a row in the `request`, specifying the FTP remote folder (where Surface data are saved) in the `content` field.

    ```sql
    INSERT INTO data_fusion_keyspace.request (id, content, datafusionids, state)
    VALUES (uuid(), '/shared', 'data_fusion1', 'false');
    ```

- Execute the `surface-data_fusion` application to download the necessary data and save it in HDFS; this application can be run in both Spark and standalone modes.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.surface.datafusion.DataFusionMain --master yarn --deploy-mode client ./extra_jars/surface-data_fusion-0.0.1.jar
  ```
