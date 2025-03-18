package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

@Controller
@RequestMapping("/rest")
public class DefaultRestController extends AbstractRestController {

  @GetMapping(value = "/**", produces = "application/json")
  public void unmappedRequest(HttpServletRequest request, HttpServletResponse response) {
    throw new RestException("Invalid URL", Status.NOT_FOUND);
  }

}
