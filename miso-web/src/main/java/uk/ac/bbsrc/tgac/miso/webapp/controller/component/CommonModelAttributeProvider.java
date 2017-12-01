package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonModelAttributeProvider {

  @Value("${miso.bugUrl:#{null}}")
  private String bugUrl;

  @Value("${miso.instanceName:#{null}}")
  private String instanceName;

  @ModelAttribute("misoBugUrl")
  public String getBugUrl() {
    return bugUrl;
  }

  @ModelAttribute("misoInstanceName")
  public String getInstanceName() {
    return instanceName;
  }

}
