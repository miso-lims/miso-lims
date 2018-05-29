package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice("uk.ac.bbsrc.tgac.miso.webapp.controller")
public class PageNotFoundAdvice {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView showNotFound(final NotFoundException e) {
    ModelMap model = new ModelMap();
    model.addAttribute("error", e.getMessage());
    return new ModelAndView("/pages/notFound.jsp", model);
  }
}
