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
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: davey Date: 25-May-2010 Time: 16:39:52
 */
@Ajaxified
public class ExperimentSearchService {

  protected static final Logger log = LoggerFactory.getLogger(ExperimentSearchService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject illuminaExperimentSearch(HttpSession session, JSONObject json) {
    StringBuffer sb = new StringBuffer();
    String searchStr = (String) json.get("str");
    String resultId = (String) json.get("id");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();

        StringBuilder b = new StringBuilder();

        int numMatches = 0;
        for (Experiment exp : requestManager.listAllExperiments()) {
          String experimentName = exp.getName() == null ? null : exp.getName().toLowerCase();
          long experimentId = exp.getId();

          if (experimentName != null && (experimentName.equals(str) || experimentName.contains(str))) {
            b.append("<li onclick=\"Search.insertResult(&#39;" + resultId + "&#39;,&#39;" + experimentId + "&#39;)\">" + exp.getName() + "("
                + exp.getAlias() + ")</li>");
            numMatches++;
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        } else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      } else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
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