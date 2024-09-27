package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Most of MISO was previously forced into a /miso context. To preserve links from this time, this
 * controller redirects any requests to /miso by removing the "/miso" portion of the URL
 */
@Controller
@RequestMapping("/miso")
public class MisoContextController {

  @RequestMapping("/**")
  public RedirectView redirectOldServletMapping(HttpServletRequest request) {
    String original = request.getRequestURI();
    String target = original.replaceFirst("/miso/", "/");

    String queryString = request.getQueryString();
    if (queryString != null) {
      target += "?" + queryString;
    }

    RedirectView redirect = new RedirectView(target, true);
    redirect.setExposeModelAttributes(false);
    redirect.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
    return redirect;
  }

}
