package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CommonModelAttributeProvider {

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @Value("${miso.bugUrl:#{null}}")
  private String bugUrl;

  @Value("${miso.instanceName:#{null}}")
  private String instanceName;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("detailedSample")
  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  @ModelAttribute("misoBugUrl")
  public String getBugUrl() {
    return bugUrl;
  }

  @ModelAttribute("misoInstanceName")
  public String getInstanceName() {
    return instanceName;
  }

}
