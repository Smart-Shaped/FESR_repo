package com.smartshaped.smart_fesr.data_fusion.exception;

/** Exception thrown when an error occurs in the DataFusion class and those that extends it. */
public class DataFusionException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Creates a new DataFusionException with the given message. */
  public DataFusionException(String message) {
    super("Exception in the DataFusion. Caused by: \n" + message);
  }

  /** Creates a new DataFusionException with the given message and throwable. */
  public DataFusionException(Throwable err) {
    super("Exception in the DataFusion. Caused by : \n" + err.getMessage(), err);
  }

  /** Creates a new DataFusionException with the given error message and throwable. */
  public DataFusionException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
