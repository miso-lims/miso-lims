package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Controller
public class ListBoxesController {
  protected static final Logger log = LoggerFactory.getLogger(ListBoxesController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/boxes/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Box> jsonRest() throws IOException {
    return requestManager.listAllBoxes();
  }

  @RequestMapping("/boxes")
  public ModelAndView listBoxes(ModelMap model) throws Exception {
    try {
      model.addAttribute("boxUses", requestManager.listAllBoxUses());
      return new ModelAndView("/pages/listBoxes.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list Kit Descriptors", ex);
      }
      throw ex;
    }
  }
}
