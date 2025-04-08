# FESR Repository

This repository contains the source code for the SmartFESR framework, a modular and scalable framework designed to support machine learning applications. The framework is designed to be highly customizable and to support a wide range of use cases.

## Docker

The framework can be used with Docker. The Docker documentation can be found in the [docs/docker](docs/docker) directory.

## Replication Study of the GenCast Model inference

This repository contains the source code for a replication study of the GenCast model, a state-of-the-art model for weather forecasting. The study uses the SmartFESR framework to download and process the data, and to perform the inference using the pre-trained model.

### Hardware requirements

According to the [official documentation](https://github.com/google-deepmind/graphcast/blob/main/docs/cloud_vm_setup.md), the GenCast model in inference with GPU requires at least 300GB of RAM and 60GB of vRAM.

### Steps

The steps to reproduce the study are as follows:

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

- Execute the `gencast-ml` application to perform the inference using the pre-trained model with data up to 2021 at a resolution of 0.25°. This application requires a GPU to run and should be executed in standalone mode.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.gencast.ml.GenCastMLApp --master spark://spark-master:7077 ./extra_jars/gencast-ml.jar
  ```

  - The result is the writing of the inference data and the difference with respect to the real values of the test set in the Cassandra table `gencastprediction` in the `ml_keyspace` keyspace.

- The serving application can be used to access the prediction data from the database and make the accessible for the frontend.

Note: The study uses the pre-trained model provided by Google in this [Google bucket](https://console.cloud.google.com/storage/browser/_details/dm_graphcast/gencast/params/GenCast%200p25deg%20Operational%20%3C2022.npz;tab=live_object?inv=1&invt=AbtElg).

## Communication with Surface
