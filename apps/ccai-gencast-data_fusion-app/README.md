# CCAI GenCast Data Fusion App

This project is a data fusion service developed by Smart Shaped, focused on integrating and processing data from multiple sources for advanced analytics and operational needs.

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

  - Create a row in the `request` table for each file to be downloaded, specifying the path in the `content` field, from the `gencast` folder onwards (excluding the `gencast` folder itself).

    - The dataset file can be downloaded from this [link](https://console.cloud.google.com/storage/browser/_details/dm_graphcast/gencast/dataset/source-hres_date-2022-03-29_res-0.25_levels-13_steps-30.nc;tab=live_object?inv=1&invt=AbtEwg).

    - The 4 files from the stats folder can be downloaded from this [link](https://console.cloud.google.com/storage/browser/dm_graphcast/gencast/stats?pageState=(%22StorageObjectListTable%22:(%22f%22:%22%255B%255D%22))&inv=1&invt=AbtEwg).

- Execute the `gencast-data_fusion` application to download the necessary data and save it in HDFS; this application can be run in both Spark and standalone modes.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.gencast.DataFusionMain --master yarn --deploy-mode client ./extra_jars/gencast-data_fusion.jar
  ```
