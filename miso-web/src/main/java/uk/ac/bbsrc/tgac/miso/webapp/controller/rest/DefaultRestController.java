package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestExceptionHandler.RestError;

@Controller
@RequestMapping("/rest")
public class DefaultRestController {
  
  @RequestMapping(value="/**", method=RequestMethod.GET, produces="application/json")
  public void unmappedRequest(HttpServletRequest request, HttpServletResponse response) {
    if (response.containsHeader("x-authentication-failed")) {
      throw new RestException(Status.UNAUTHORIZED);
    }
    throw new RestException("Invalid URL", Status.NOT_FOUND);
  }
  
  @ExceptionHandler(Exception.class)
  public @ResponseBody RestError handleError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    return RestExceptionHandler.handleException(request, response, exception);
  }
  
}
