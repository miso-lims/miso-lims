package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;

@Ajaxified
public class ServiceRecordControllerHelperService {
  
  protected static final Logger log = LoggerFactory.getLogger(ServiceRecordControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private ServiceRecordService serviceRecordService;
  
  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
  
  public JSONObject deleteServiceRecord(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (IOException e) {
      log.error("delete service record", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("recordId")) {
        Long recordId = json.getLong("recordId");
        try {
          ServiceRecord record = serviceRecordService.get(recordId);
          serviceRecordService.delete(record);
          return JSONUtils.SimpleJSONResponse("Service Record deleted");
        } catch (IOException e) {
          log.error("cannot delete service record", e);
          return JSONUtils.SimpleJSONError("Cannot delete service record: " + e.getMessage());
        }
      } else {
        return JSONUtils.SimpleJSONError("No Service Record specified to delete.");
      }
    } else {
      return JSONUtils.SimpleJSONError("Only admins can delete objects.");
    }
  }
  
}
