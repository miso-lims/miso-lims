package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.service.exception.BulkValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;

/**
 * Class for handling exceptions occurring in REST Controller classes
 */
public class RestExceptionHandler {
  
  private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);
  
  private RestExceptionHandler() {
    throw new AssertionError("Instantiability not intended");
  }
  
  /**
   * Collects information from an exception, sets an appropriate HTTP Status for the response, and forms a representation of the
   * error that may be returned to the client. e.g.
   * 
   * <pre>
   * {
   *   requestUrl: URL,
   *   detail: exception message,
   *   code: HTTP status code,
   *   message: HTTP status reason phrase,
   *   dataFormat: {custom|validation|bulk validation},
   *   data: format depends on dataFormat
   * }
   * </pre>
   * 
   * @param request HTTP request that caused the exception
   * @param response HTTP response that will be returned to the client
   * @param exception The exception that was thrown while handling a REST request
   * @return a representation of the error to return to the client
   */
  public static ObjectNode handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode error = mapper.createObjectNode();
    error.put("requestUrl", request.getRequestURL().toString());
    String detailMessage = exception.getLocalizedMessage();
    Status status = null;

    ResponseStatus rs = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
    if (exception instanceof NestedServletException) {
      NestedServletException nested = (NestedServletException) exception;
      if (nested.getCause() instanceof Exception) {
        return handleException(request, response, (Exception) nested.getCause());
      }
    }
    if (rs != null) {
      // Spring-annotated exception
      status = Status.fromStatusCode(rs.value().value());
    } else if (exception instanceof RestException) {
      // Customized REST exception with additional data fields
      RestException restException = (RestException) exception;
      status = restException.getStatus();
      addDataMap(error, restException.getData());
    } else if (exception instanceof BulkValidationException) {
      BulkValidationException bulkValidationException = (BulkValidationException) exception;
      status = Status.BAD_REQUEST;
      RestUtils.addBulkValidationData(error, bulkValidationException);
      error.put("dataFormat", "bulk validation");
    } else if (exception instanceof ValidationException) {
      ValidationException valException = (ValidationException) exception;
      status = Status.BAD_REQUEST;
      addDataMap(error, valException.getErrorsByField());
      error.put("dataFormat", "validation");
    } else if (ExceptionUtils.getRootCause(exception) instanceof IOException
        && StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(exception), "Broken pipe")) {
      response.setStatus(Status.SERVICE_UNAVAILABLE.getStatusCode());
          return null;
    } else {
          // Unknown/unexpected exception
          detailMessage = "An unexpected error has occurred";
          status = Status.INTERNAL_SERVER_ERROR;
    }
    
    error.put("detail", detailMessage);
    error.put("code", status.getStatusCode());
    error.put("message", status.getReasonPhrase());

    if (status.getFamily() == Status.Family.SERVER_ERROR) {
      log.error(status.getStatusCode() + " error handling REST request", exception);
    } else {
      log.debug(status.getStatusCode() + " error handling REST request", exception);
    }
    
    if (!error.has("dataFormat")) {
      error.put("dataFormat", "custom");
    }
    response.setStatus(status.getStatusCode());
    
    return error;
  }

  private static void addDataMap(ObjectNode node, Map<String, String> data) {
    if (data == null || data.isEmpty()) {
      return;
    }
    ObjectNode mapNode = node.putObject("data");
    for (Entry<String, String> entry : data.entrySet()) {
      mapNode.put(entry.getKey(), entry.getValue());
    }
  }
  
}
