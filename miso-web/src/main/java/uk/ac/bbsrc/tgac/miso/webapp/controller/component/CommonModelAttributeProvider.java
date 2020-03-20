package uk.ac.bbsrc.tgac.miso.webapp.controller.component;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TransferService;

@ControllerAdvice("uk.ac.bbsrc.tgac.miso.webapp.controller.view")
public class CommonModelAttributeProvider {

  private static final Logger log = LoggerFactory.getLogger(CommonModelAttributeProvider.class);

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @Value("${miso.bugUrl:#{null}}")
  private String bugUrl;

  @Value("${miso.instanceName:#{null}}")
  private String instanceName;

  @Autowired
  private TransferService transferService;
  @Autowired
  private AuthorizationManager authorizationManager;

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

  @ModelAttribute("pendingTransfers")
  public long countPendingTransfers() {
    try {
      User user = authorizationManager.getCurrentUser();
      return user == null ? 0 : transferService.countPendingForUser(user);
    } catch (IOException e) {
      log.error("Error querying for pending transfers", e);
      return 0;
    }
  }

}
