package com.smartshaped.smart_fesr.data_fusion.exception;

/** Exception thrown when an error occurs in the DataFusionSaver class. */
public class DataFusionSaverException extends Exception {

  private static final long serialVersionUID = 1L;

  /** Creates a new instance with the given message. */
  public DataFusionSaverException(String message) {
    super("Exception in the DataFusionSaver. Caused by : \n" + message);
  }

  /** Creates a new instance wrapping the given throwable. */
  public DataFusionSaverException(Throwable err) {
    super("Exception in the DataFusionSaver. Caused by : \n" + err.getMessage(), err);
  }

  /** Creates a new instance with the given message and wrapping the given throwable. */
  public DataFusionSaverException(String errMessage, Throwable err) {
    super(errMessage + "\n" + err.getMessage(), err);
  }
}
