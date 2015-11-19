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
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: bian Date: 10-Mar-2010 Time: 13:10:10
 * 
 */
@Ajaxified
public class ExperimentPreview {
  protected static final Logger log = LoggerFactory.getLogger(ExperimentPreview.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  @Deprecated
  public JSONObject previewExperiment(HttpSession session, JSONObject json) {
    String experimentId = (String) json.get("experimentId");

    try {
      Experiment e = requestManager.getExperimentById(Long.parseLong(experimentId));
      Collection<Run> runs = requestManager.listRunsByExperimentId(e.getId());

      session.setAttribute("experiment", e);

      StringBuilder rb = new StringBuilder();
      for (Run r : runs) {
        rb.append("<li><a href='/miso/run/").append(r.getId()).append("'>").append(r.getName()).append("</a></li>");
      }

      StringBuilder sb = new StringBuilder();
      if (e.getPool() != null) {
        for (Dilution dil : e.getPool().getDilutions()) {
          Sample s = dil.getLibrary().getSample();
          sb.append("<li><a href='/miso/sample/").append(s.getId()).append("'>").append(s.getName()).append("</a></li>");
        }
      }

      StringBuilder b = new StringBuilder();
      b.append("<div onclick=\"Effect.toggle('preview" + experimentId + "','blind'); return false;\">"
          + "<img src=\"/styles/images/moreinfo.png\" class=\"previewimage\"/></div>");
      b.append("<br/><div id=\"preview" + experimentId + "\" class='exppreview'>");
      b.append("Title: <b>").append(e.getTitle()).append("</b><br/>");
      b.append("Description: <b>").append(e.getDescription()).append("</b><br/>");
      b.append("Samples: <ul class=\"bullets\">").append(sb.toString()).append("</ul>");
      b.append("Runs: <ul class=\"bullets\">").append(rb.toString()).append("</ul>");
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