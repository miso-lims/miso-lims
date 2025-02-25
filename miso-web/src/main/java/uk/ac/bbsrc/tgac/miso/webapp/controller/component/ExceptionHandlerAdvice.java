package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice("uk.ac.bbsrc.tgac.miso.webapp.controller.view")
public class ExceptionHandlerAdvice {

  private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

  @Value("${miso.bugUrl:#{null}}")
  private String bugUrl;

  @Value("${miso.instanceName:#{null}}")
  private String instanceName;

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView showNotFound(final NotFoundException e) {
    return fromExceptionMessage("Page Not Found", e, false);
  }

  @ExceptionHandler(ClientErrorException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ModelAndView showClientError(final ClientErrorException e) {
    return fromExceptionMessage("Bad Request", e, true);
  }

  @ExceptionHandler(ServerErrorException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView showServerError(final ServerErrorException e) {
    return fromExceptionMessage("Server Error", e, true);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView showUnknownError(final Exception e) {
    logException(e);
    return withMessages("Server Error",
        "An unexpected error has occurred. If the problem persists, please report it to your MISO administrators",
        true);
  }

  private ModelAndView fromExceptionMessage(String genericMessage, Exception e, boolean possibleBug) {
    if (possibleBug) {
      logException(e);
    }
    return withMessages(genericMessage, e.getMessage(), possibleBug);
  }

  private ModelAndView withMessages(String genericMessage, String specificMessage, boolean showBugUrl) {
    ModelMap model = new ModelMap();
    model.addAttribute("misoBugUrl", bugUrl);
    model.addAttribute("misoInstanceName", instanceName);
    model.addAttribute("genericMessage", genericMessage);
    model.addAttribute("specificMessage", specificMessage);
    model.addAttribute("showBugUrl", showBugUrl);
    return new ModelAndView("/WEB-INF/pages/handledError.jsp", model);
  }

  private void logException(Exception e) {
    log.error("Returning error page for exception", e);
  }

}
