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
  
  public JSONObject deleteServiceRecordAttachment(HttpSession session, JSONObject json) {
    final Long id = json.getLong("id");
    final Integer hashcode = json.getInt("hashcode");
    try {
      final User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (user.isAdmin()) {
        String filename = null;
        for (final String s : misoFileManager.getFileNames(SequencerServiceRecord.class, id.toString())) {
          if (s.hashCode() == hashcode) {
            filename = s;
            break;
          }
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
