package com.smartshaped.smartfesr.gencast.ml;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.smartshaped.smartfesr.common.exception.ConfigurationException;
import com.smartshaped.smartfesr.ml.blackbox.PythonBlackbox;
import com.smartshaped.smartfesr.ml.blackbox.exception.BlackboxException;

public class GenCastBlackbox extends PythonBlackbox {

  private static final Logger logger = LogManager.getLogger(GenCastBlackbox.class);

  public GenCastBlackbox() throws ConfigurationException {
    super();
  }

  @Override
  protected List<Dataset<Row>> mergeDatasetsIfNecessary(List<Dataset<Row>> list)
      throws BlackboxException {
    return list;
  }

  @Override
  protected void postRunning() throws BlackboxException {
    // this app doesn't need post running
  }

  @Override
  protected void makeDatasetAccessible(Dataset<Row> dataset, String inputInfo)
      throws BlackboxException {

    // we need the paths from the parquet file saved in HDFS
    List<String> values =
        dataset.select("path").collectAsList().stream()
            .map(row -> row.getString(0))
            .collect(Collectors.toList());
    this.inputs = String.join(",", values);
  }

  @Override
  protected void extraPreparation() throws BlackboxException {
    super.extraPreparation();

    logger.info("Installing importlib_metadata");
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command("pip3", "install", "-U", "importlib_metadata");
    runCommand(processBuilder);

    logger.info("Installing GenCast");
    ProcessBuilder processBuilder2 = new ProcessBuilder();
    processBuilder2.command(
        "pip3", "install", "--upgrade", "https://github.com/deepmind/graphcast/archive/master.zip");
    runCommand(processBuilder2);

    logger.info("Installing jax with cuda");
    ProcessBuilder processBuilder3 = new ProcessBuilder();
    processBuilder3.command("pip3", "install", "-U", "jax[cuda12]");
    runCommand(processBuilder3);
  }
}
