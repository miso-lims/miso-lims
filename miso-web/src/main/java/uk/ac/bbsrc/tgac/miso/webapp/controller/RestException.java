package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.Response.Status;

/**
 * Unchecked exception class to encapsulate error data from a failed REST call. Includes HTTP Status
 * to indicate type of error from an HTTP perspective, and a data field that may be populated with
 * any additional useful information
 */
public class RestException extends RuntimeException {

  private static final long serialVersionUID = -8185316218767057395L;

  private Status status = Status.INTERNAL_SERVER_ERROR; // default (unknown/unanticipated error)
  private Map<String, String> data;

  public RestException() {
    super();
  }

  public RestException(String message, Status status) {
    super(message);
    this.status = status;
  }

  public RestException(String message, Status status, Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  public RestException(Status status) {
    super();
    this.status = status;
  }

  public RestException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RestException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestException(String message) {
    super(message);
  }

  public RestException(Throwable cause) {
    super(cause);
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Map<String, String> getData() {
    return data;
  }

  public void setData(Map<String, String> data) {
    this.data = data;
  }

  public void addData(String key, String value) {
    if (data == null)
      data = new HashMap<>();
    data.put(key, value);
  }

}
