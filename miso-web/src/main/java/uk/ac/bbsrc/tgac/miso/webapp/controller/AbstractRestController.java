package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Abstract class meant to be extended by all REST controllers. Adds exception handling for all
 * exceptions
 */
public abstract class AbstractRestController {

  @Autowired
  private JsonMapper mapper;

  /**
   * converts an Exception into a RestError model to be returned in response to a REST request.
   * Ensures that the correct response status is set.
   * 
   * @param request the request being handled when the exception occurred
   * @param response the response to be sent
   * @param exception the exception to be handled
   * @return the RestError containing exception and HTTP response details
   */
  @ExceptionHandler
  private @ResponseBody ObjectNode handleError(HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return RestExceptionHandler.handleException(request, response, exception, getJsonMapper());
  }

  protected JsonMapper getJsonMapper() {
    return mapper;
  }

}
