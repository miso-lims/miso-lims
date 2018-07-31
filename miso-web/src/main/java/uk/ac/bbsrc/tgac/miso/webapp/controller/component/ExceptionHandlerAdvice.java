package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice("uk.ac.bbsrc.tgac.miso.webapp.controller")
public class ExceptionHandlerAdvice {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView showNotFound(final NotFoundException e) {
    return fromExceptionMessage("Page Not Found", e, false);
  }

  @ExceptionHandler(ServerErrorException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView showServerError(final ServerErrorException e) {
    return fromExceptionMessage("Server Error", e, true);
  }

  private ModelAndView fromExceptionMessage(String genericMessage, Exception e, boolean showBugUrl) {
    ModelMap model = new ModelMap();
    model.addAttribute("genericMessage", genericMessage);
    model.addAttribute("specificMessage", e.getMessage());
    model.addAttribute("showBugUrl", showBugUrl);
    return new ModelAndView("/pages/handledError.jsp", model);
  }

}
