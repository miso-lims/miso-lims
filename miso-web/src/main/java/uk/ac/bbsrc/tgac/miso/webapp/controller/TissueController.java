package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;

@Controller
public class TissueController implements ServletContextAware {
  protected static final Logger log = LoggerFactory.getLogger(TissueController.class);
  
  ServletContext servletContext;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private TissueOriginService tissueOriginService;
  
  public void setTissueOriginService(TissueOriginService tissueOriginService) {
    this.tissueOriginService = tissueOriginService;
  }
  
  //@ModelAttribute
  
  
  @RequestMapping(value = "/tissueOrigins", method = RequestMethod.GET)
  public ModelAndView tissueOrigins()  throws IOException {
    return new ModelAndView("/pages/tissueOrigins.jsp");
  }
  
  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }
}
