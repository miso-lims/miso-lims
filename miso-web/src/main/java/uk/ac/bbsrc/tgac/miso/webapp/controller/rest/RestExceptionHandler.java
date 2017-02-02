package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class for handling exceptions ocurring in REST Controller classes
 */
public class RestExceptionHandler {
  
  private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);
  
  private RestExceptionHandler() {
    throw new AssertionError("Instantiability not intended");
  }
  
  /**
   * Collects information from an exception, sets an appropriate HTTP Status for the response, and forms a representation of the 
   * error that may be returned to the client
   * 
   * @param request HTTP request that caused the exception
   * @param response HTTP response that will be returned to the client
   * @param exception The exception that was thrown while handling a REST request
   * @return a representation of the error to return to the client
   */
  public static RestError handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    Status status = null;
    Map<String, String> data = null;
    
    ResponseStatus rs = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
    if (rs != null) {
      // Spring-annotated exception
      status = Status.fromStatusCode(rs.value().value());
    }
    else if (exception instanceof RestException) {
      // Customized REST exception with additional data fields
      RestException restException = (RestException) exception;
      status = restException.getStatus();
      data = restException.getData();
    }
    else {
      // Unknown/unexpected exception
      status = Status.INTERNAL_SERVER_ERROR;
    }
    
    if (status.getFamily() == Status.Family.SERVER_ERROR) {
      if (data == null) data = new HashMap<>();
      data.put("exceptionClass", exception.getClass().getName());
      log.error(status.getStatusCode() + "error handling REST request", exception);
    }
    else {
      log.debug(status.getStatusCode() + "error handling REST request", exception);
    }
    
    response.setStatus(status.getStatusCode());
    
    RestError error = new RestError();
    error.setRequestUrl(request.getRequestURL().toString());
    error.setStatus(status);
    error.setDetail(exception.getLocalizedMessage());
    error.setData(data);
    
    return error;
  }
  
  /**
   * Representation of an exception that ocurred while handling a REST request
   */
  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public static class RestError {
    
    private Status status;
    private String detail;
    private String requestUrl;
    private Map<String, String> data = new HashMap<>();
    
    public RestError() {
      
    }

    @JsonIgnore
    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }
    
    /**
     * @return the HTTP status code
     */
    public int getCode() {
      return status.getStatusCode();
    }
    
    /**
     * @return the HTTP status message
     */
    public String getMessage() {
      return status.getReasonPhrase();
    }

    public String getDetail() {
      return detail;
    }

    public void setDetail(String detail) {
      this.detail = detail;
    }

    /**
     * @return the URL requested
     */
    public String getRequestUrl() {
      return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
      this.requestUrl = requestUrl;
    }

    /**
     * @return any additional error data
     */
    public Map<String, String> getData() {
      return data;
    }

    public void setData(Map<String, String> data) {
      this.data = data;
    }
    
  }
  
}
