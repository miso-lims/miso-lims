package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

@Ajaxified
public class ServiceRecordControllerHelperService {
  
  protected static final Logger log = LoggerFactory.getLogger(ServiceRecordControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  
  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
  
  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
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
          requestManager.deleteSequencerServiceRecord(requestManager.getSequencerServiceRecordById(recordId));
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
  
  public JSONObject deleteServiceRecordAttachment(HttpSession session, JSONObject json) {
    final Long id = json.getLong("id");
    final Integer hashcode = json.getInt("hashcode");
    User user = null;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    } catch (final IOException e) {
      log.error("delete service record file", e);
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }
    try {
      if (user.isAdmin()) {
        String filename = null;
        for (final String s : misoFileManager.getFileNames(SequencerServiceRecord.class, id.toString())) {
          if (s.hashCode() == hashcode) {
            filename = s;
            break;
          }
        }
        if (filename == null) {
          return JSONUtils.SimpleJSONError("File not found");
        }
        log.info(MessageFormat.format("Attempting to delete file {0}", filename));
        misoFileManager.deleteFile(SequencerServiceRecord.class, id.toString(), filename);
        log.info(MessageFormat.format("{0} deleted", filename));
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        return JSONUtils.SimpleJSONError(MessageFormat.format("Cannot delete file id {0}.  Access denied.", id));
      }
    } catch (final IOException e) {
      log.error("delete service record file", e);
      return JSONUtils.SimpleJSONError("Cannot remove file: " + e.getMessage());
    }
  }
  
}
