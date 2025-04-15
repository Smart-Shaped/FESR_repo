# How to Run a Spark Application on YARN and View Logs

## Prerequisites

The Spark container leverages NVIDIA RAPIDS for enhanced performance on GPU-equipped systems. Ensure that your environment is configured to utilize GPUs by following the official [installation guide](https://docs.rapids.ai/install/).

## 1. Environment Preparation

### 1.1 Navigate to the `docker` Directory
   - Move into the project directory:  
     ```bash
     cd docker
     ```

### 1.2 Create Required Directories
   - Create the following directories inside `docker`:  
     ```bash
     mkdir jars hadoop spark_jars
     ```

### 1.3 Set Permissions for the `docker` Directory
   - Ensure that `docker` and its subdirectories have the correct permissions:  
     ```bash
     chmod -R 777 .
     ```

---

## 2. Configure and Start the System

### 2.1 Build the Docker Environment
   - Run the following command to build the containers:  
     ```bash
     docker compose build
     ```

### 2.2 Start the Docker Containers
   - Launch the environment in detached mode:  
     ```bash
     docker compose up -d
     ```

---

## 3. Additional Configurations

### 3.1 Copy the Application JAR
   - Place the application `.jar` file in the `docker/jars` directory.

### 3.2 Copy Hadoop Configuration Files
   - Extract the Hadoop configurations from the NameNode container and save them locally in the `hadoop` directory:  
     ```bash
     docker cp docker-namenode-1:/opt/hadoop/etc/hadoop/. ./hadoop
     ```

### 3.3 Copy Spark JAR Files
   - Copy Spark JAR files from the Spark Master container to the `spark_jars` directory:  
     ```bash
     docker cp docker-spark-master-1:/opt/bitnami/spark/jars/. ./spark_jars
     ```

### 3.4 Restart the Docker Containers
   - Restart the Docker environment to apply changes:  
     ```bash
     docker compose restart
     ```

---

## 4. Configure HDFS for Spark

### 4.1 Create Required Directories in HDFS
   - From the NameNode terminal, run the following commands:  
     ```bash
     hdfs dfs -mkdir -p /user/spark/eventLog
     hdfs dfs -mkdir /spark
     hdfs dfs -mkdir /spark/jars
     hdfs dfs -mkdir /spark/logs
     hdfs dfs -put /opt/hadoop/dfs/spark/jars/* /spark/jars
     ```

### 4.2 Set Permissions and Ownership for Spark
   - Ensure Spark has the required permissions and ownership:  
     ```bash
     hdfs dfs -chmod -R 777 /user/spark/eventLog
     hdfs dfs -chown -R spark:hadoop /spark
     hdfs dfs -chmod -R 777 /spark
     ```
   - **Temporary Option (for testing):**  
     ```bash
     hdfs dfs -chmod -R 777 /
     hdfs dfs -chown -R spark:hadoop /
     ```

---

## 5. Run the Spark Application

### 5.1 Local Mode
   - Run the application in local mode:  
     ```bash
     spark-submit --class <main_class> --master spark://spark-master:7077 ./extra_jars/<application_name>.jar
     ```

### 5.2 YARN Cluster Mode
   - Run the application in YARN cluster mode:  
     ```bash
     spark-submit --class <main_class> --master yarn --deploy-mode cluster ./extra_jars/<application_name>.jar
     ```

### 5.3 YARN Client Mode
   - Run the application in YARN client mode:  
     ```bash
     spark-submit --class <main_class> --master yarn --deploy-mode client ./extra_jars/<application_name>.jar
     ```

---

## 6. View Logs for Cluster Mode Execution

### 6.1 Use the `yarn logs` Command
   - To retrieve the logs of an application:  
     ```bash
     yarn logs -applicationId <applicationId> -log_files_pattern stderr
     ```

### 6.2 View Logs in the ResourceManager
   - Logs are accessible in the following directory:  
     ```
     /var/log/hadoop/userlogs/<applicationId>/<containerId>/stderr
     ```

### 6.3 Download Logs Locally
   - Copy the logs from the Hadoop container to the local machine:  
     ```bash
     docker cp <container_id>:/var/log/hadoop/userlogs/<applicationId>/<containerId>/stderr ./local_dir
     ```
