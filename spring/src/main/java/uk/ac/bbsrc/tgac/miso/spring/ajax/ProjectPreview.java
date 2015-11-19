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
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: bian Date: 16-Mar-2010 Time: 14:11:37
 * 
 */
@Ajaxified
public class ProjectPreview {
  protected static final Logger log = LoggerFactory.getLogger(ProjectPreview.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject previewProject(HttpSession session, JSONObject json) {
    StringBuffer sb = new StringBuffer();
    String projectId = (String) json.get("projectId");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Project project = requestManager.getProjectById(Long.parseLong(projectId));

      session.setAttribute("project", project);

      String studyHTML = "";
      for (Study r : project.getStudies()) {
        studyHTML += "<li><a href='/miso/study/" + r.getId() + "/project/" + project.getId() + "'>" + r.getName() + "</a></li>";
      }
      StringBuilder b = new StringBuilder();
      b.append("<div onclick=\"Effect.toggle('preview" + projectId + "','blind'); return false;\">"
          + "<img src=\"/styles/images/moreinfo.png\" class=\"previewimage\"/></div>");
      b.append("<br/><div id=\"preview" + projectId + "\" class='exppreview'>");
      b.append("Description: <b>" + project.getDescription() + "</b><br/>");
      b.append("Owner: <b>" + project.getSecurityProfile().getOwner().getFullName() + "</b><br/>");
      b.append("studies: <ul class=\"bullets\">" + studyHTML + "</ul>");
      b.append("</div>");
      return JSONUtils.SimpleJSONResponse(b.toString());

    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed");
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

}
