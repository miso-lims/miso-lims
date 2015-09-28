/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.json.JsonSanitizer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class StudyControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(StudyControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private SubmissionManager submissionManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSubmissionManager(SubmissionManager submissionManager) {
    this.submissionManager = submissionManager;
  }

  public JSONObject deleteStudy(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("studyId")) {
        Long studyId = json.getLong("studyId");
        try {
          requestManager.deleteStudy(requestManager.getStudyById(studyId));
          return JSONUtils.SimpleJSONResponse("Study deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete study: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No study specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only admins can delete objects.");
    }
  }

  public JSONObject listStudiesDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Study study : requestManager.listAllStudies()) {
        jsonArray.add(JsonSanitizer.sanitize("[\"" + study.getName() + "\",\"" +
                     study.getAlias() + "\",\"" +
                     study.getDescription() + "\",\"" +
                     study.getStudyType() + "\",\"" +
                     "<a href=\"/miso/study/" + study.getId() + "\"><span class=\"fa fa-pencil-square-o fa-lg\"></span></a>" + "\"]"));

      }
      j.put("array", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }
}