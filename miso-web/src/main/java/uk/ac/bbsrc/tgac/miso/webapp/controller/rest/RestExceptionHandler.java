package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestExceptionHandler.RestError.DataFormat;

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
    RestError error = new RestError();
    error.setRequestUrl(request.getRequestURL().toString());
    error.setDetail(exception.getLocalizedMessage());
    
    ResponseStatus rs = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
    if (exception instanceof NestedServletException) {
      NestedServletException nested = (NestedServletException) exception;
      if (nested.getCause() instanceof Exception) {
        return handleException(request, response, (Exception) nested.getCause());
      }
    }
    if (rs != null) {
      // Spring-annotated exception
      error.setStatus(Status.fromStatusCode(rs.value().value()));
    } else if (exception instanceof RestException) {
      // Customized REST exception with additional data fields
      RestException restException = (RestException) exception;
      error.setStatus(restException.getStatus());
      error.setData(restException.getData());
    } else if (exception instanceof ValidationException) {
      ValidationException valException = (ValidationException) exception;
      error.setStatus(Status.BAD_REQUEST);
      error.setData(valException.getErrorsByField());
      error.setDataFormat(DataFormat.VALIDATION);
    } else {
      // Unknown/unexpected exception
      error.setStatus(Status.INTERNAL_SERVER_ERROR);
    }
    
    if (error.getStatus().getFamily() == Status.Family.SERVER_ERROR) {
      if (error.getData() == null) {
        error.setData(new HashMap<>());
      }
      error.getData().put("exceptionClass", exception.getClass().getName());
      log.error(error.getStatus().getStatusCode() + " error handling REST request", exception);
    } else {
      log.debug(error.getStatus().getStatusCode() + " error handling REST request", exception);
    }
    
    response.setStatus(error.getStatus().getStatusCode());
    
    return error;
  }
  
  /**
   * Representation of an exception that ocurred while handling a REST request
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class RestError {
    
    public enum DataFormat {
      CUSTOM("custom"),
      VALIDATION("validation");

      private final String text;

      private DataFormat(String text) {
        this.text = text;
      }

      public String getText() {
        return text;
      }
    }

    private Status status;
    private String detail;
    private String requestUrl;
    private String dataFormat = DataFormat.CUSTOM.getText();
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
     * @return the type of information that is included in the data map. May be "custom" or a more specific/useful format
     */
    public String getDataFormat() {
      return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
      this.dataFormat = dataFormat.getText();
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
