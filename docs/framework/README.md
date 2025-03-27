# SmartFESR

A modular and scalable framework designed to support machine learning applications - emphasising transparency, interoperability, and usability.

As of now, we have released three layers (DataFusion Layer, and ML Layer).

To implement your own version of any abstract layer you have to:

- Build the project running at the level of the SmartFESR pom.xml the following command:
```bash
mvn clean install
```
- Generate a Maven project and add SmartFESR as dependency on your maven pom.xml as below: 

```bash
<dependency>
	<groupId>com.smartshaped</groupId>
	<artifactId>smart_fesr</artifactId>
	<version>0.0.1</version>
</dependency>
```
- Add the maven-shade-plugin to generate a shaded jar in order to submit your layer implementation as a Spark application (keep in mind the framework is based on Java 11)

```bash
<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-shade-plugin</artifactId>
			<version>3.6.0</version>
			<executions>
				<execution>
					<phase>package</phase>
					<goals>
						<goal>shade</goal>
					</goals>
					<configuration>
						<filters>
							<filter>
								<artifact>*:*</artifact>
								<excludes>
									<exclude>META-INF/*.SF</exclude>
									<exclude>META-INF/*.DSA</exclude>
									<exclude>META-INF/*.RSA</exclude>
								</excludes>
							</filter>
						</filters>
						<transformers>
							<transformer
									implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
									<resource>
										META-INF/services/org.apache.spark.sql.sources.DataSourceRegister
									</resource>
							</transformer>
						</transformers>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

After this, you can choose to extend any of the following layers:

- [ML Layer](#ml-layer-documentation)

---

# ML Layer Documentation

## How to Develop an ML Application

To develop a machine learning application using the ML Layer, follow these steps:

### 1. Create a Class that Extends `com.smartshaped..ml.MLLayer`
- Ensure that the class constructor is **public**.

### 2. Create at Least One Class that Extends `com.smartshaped..ml.HdfsReader`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file along with the HDFS path from which the data will be read.
- Optionally, override the `processRawData` method to add custom processing for the raw data.

### 3. Create a Class that Extends `com.smartshaped..ml.Pipeline`
- Declare this class in the YAML file.
- Override the `start` method to implement the specific machine learning logic. 
  - Ensure that the `setModel` and `setPredictions` methods are called at the end of the pipeline.

### 4. Create a Class that Extends `com.smartshaped..ml.ModelSaver`
- Ensure that the class constructor is **public**.
- Declare this class in the YAML file.

### 5. Create a Class that Extends `com.smartshaped..common.utils.TableModel`
- Define the table fields as class attributes.
- Specify the name of the primary key as a **string**.
- Create a `typeMapping.yml` file to define the mapping between Java field types and CQL (Cassandra Query Language) types.
- Declare this class in the YAML file.

### 6. Create a Class Containing the `main` Method
- Call the `start` method of `MLLayer` inside the `main` method.
- Specify this class in the `spark-submit` command.

---

# Execution Instructions

To generate the `.jar` file, run the following command from your project directory:

```bash
mvn clean install
```

Then, follow the [Docker documentation](../docker/README.md)
