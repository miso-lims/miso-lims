package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by zakm on 07/08/2015.
 */
@Controller
@RequestMapping("/kitcomponent")
@SessionAttributes("kitComponent")
public class LogKitComponentController {

  protected static final Logger log = LoggerFactory.getLogger(LogKitComponentController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    try {
      return new ModelAndView("/pages/logKitComponent.jsp", model);
    } catch (Exception ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show sample", ex);
      }
      throw ex;
    }
  }
}