package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/rest")
public class DefaultRestController extends RestController {
  
  @RequestMapping(value="/**", method=RequestMethod.GET, produces="application/json")
  public void unmappedRequest(HttpServletRequest request, HttpServletResponse response) {
    throw new RestException("Invalid URL", Status.NOT_FOUND);
  }
  
}
