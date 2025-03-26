package com.smartshaped.smartfesr.datafusion.exception;

/**
 * Exception thrown when an error occurs inside the DataFusion Layer class and those that extends
 * it.
 */
public class DataFusionLayerException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Constructs a new DataFusionLayerException with the specified detail message. */
  public DataFusionLayerException(String message) {
    super("Exception in the DataFusion Layer. Caused by: \n" + message);
  }

  /** Constructs a new DataFusionLayerException with the specified cause. */
  public DataFusionLayerException(Throwable err) {
    super("Exception in the DataFusion Layer. Caused by : \n" + err.getMessage(), err);
  }

  /** Constructs a new DataFusionLayerException with the specified detail message and cause. */
  public DataFusionLayerException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
