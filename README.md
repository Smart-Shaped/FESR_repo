# FESR Repository

This repository contains the source code for the SmartFESR framework, a modular and scalable framework designed to support machine learning applications. The framework is designed to be highly customizable and to support a wide range of use cases.

## Documentation

The documentation for the framework can be found in the [docs/framework](docs/framework) directory.

## Docker

The framework can be used with Docker. The Docker documentation can be found in the [docs/docker](docs/docker) directory.

## Replication Study of the GenCast Model inference

This repository contains the source code for a replication study of the GenCast model, a state-of-the-art model for weather forecasting. The study uses the SmartFESR framework to download and process the data, and to perform the inference using the pre-trained model.

The steps to reproduce the study are as follows:

- Create a table in the Cassandra database called `request` with one row for each file to be downloaded, specifying the path in the `content` field, from the `gencast` folder onwards (excluding the `gencast` folder itself).

  - The dataset file can be downloaded from this [link](https://console.cloud.google.com/storage/browser/_details/dm_graphcast/gencast/dataset/source-hres_date-2022-03-29_res-0.25_levels-13_steps-30.nc;tab=live_object?inv=1&invt=AbtEwg).

  - The 4 files from the stats folder can be downloaded from this [link](https://console.cloud.google.com/storage/browser/dm_graphcast/gencast/stats?pageState=(%22StorageObjectListTable%22:(%22f%22:%22%255B%255D%22))&inv=1&invt=AbtEwg).

- Execute the `gencast-data_fusion` application to download the necessary data and save it in HDFS; this application can be run in both Spark and standalone modes.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.gencast.DataFusionMain --master yarn --deploy-mode client ./extra_jars/gencast-data_fusion.jar
  ```

- Execute the `gencast-ml` application to perform the inference using the pre-trained model with data up to 2021 at a resolution of 0.25°. This application requires a GPU to run and should be executed in standalone mode.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.gencast.ml.GenCastMLApp --master spark://spark-master:7077 ./extra_jars/gencast-ml-app.jar
  ```

- The result is the writing of the inference data and the difference with respect to the real values of the test set in the Cassandra table `gencastprediction`.

- The serving application can be used to consume the data from the database.

Note: The study uses the pre-trained model provided by Google in this [Google bucket](https://console.cloud.google.com/storage/browser/_details/dm_graphcast/gencast/params/GenCast%200p25deg%20Operational%20%3C2022.npz;tab=live_object?inv=1&invt=AbtElg).

## Communication with Surface
