# CCAI GenCast ML App

This project is the machine learning application for the GenCast model, developed by Smart Shaped. It is designed to perform inference using the GenCast model on weather data, leveraging distributed computing and GPU acceleration where available.

## Technologies Used

- **Apache Spark**: Used for distributed data processing and orchestration.
- **Apache Hadoop**: Supports integration with Hadoop Distributed File System for large-scale data storage.
- **Apache Cassandra**: Used to store the inference data and the difference with respect to the real values of the test set.

## Directory Structure

```bash
src/
├── main/
│   ├── java/...
│   └── resources/
│       ├── blackbox_methods.py
│       ├── framework-config.yml
│       ├── gencast_pipeline.py
│       ├── local-config.yml
│       ├── requirements.txt
│       ├── script.py
│       └── typeMapping.yml
```

## How to Run

- Execute the `gencast-ml` application to perform the inference using the pre-trained model with data up to 2021 at a resolution of 0.25°. This application requires a GPU to run and should be executed in standalone mode.

  ```bash
  spark-submit --class com.smartshaped.smartfesr.gencast.ml.GenCastMLApp --master spark://spark-master:7077 ./extra_jars/gencast-ml.jar
  ```

  - The result is the writing of the inference data and the difference with respect to the real values of the test set in the Cassandra table `gencastprediction` in the `ml_keyspace` keyspace.

- The serving application can be used to access the prediction data from the database and make the accessible for the frontend.

Note: The study uses the pre-trained model provided by Google in this [Google bucket](https://console.cloud.google.com/storage/browser/_details/dm_graphcast/gencast/params/GenCast%200p25deg%20Operational%20%3C2022.npz;tab=live_object?inv=1&invt=AbtElg).
