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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: bianx Date: 19-Apr-2011 Time: 12:04:04 To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class ExperimentWizardControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PoolControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject loadExperimentPlatform(HttpSession session, JSONObject json) {

    StringBuilder b = new StringBuilder();
    try {
      for (Platform platform : requestManager.listAllPlatforms()) {
        b.append("<option value=\"" + platform.getPlatformId() + "\">" + platform.getNameAndModel() + "</option>");
      }
    } catch (IOException e) {
      log.debug("Failed to change ReportType", e);
      return JSONUtils.SimpleJSONError("Failed to load platform");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject loadExperimentStudyTypes(HttpSession session, JSONObject json) {

    StringBuilder b = new StringBuilder();
    try {
      for (String st : requestManager.listAllStudyTypes()) {
        b.append("<option value=\"" + st + "\">" + st + "</option>");
      }
    } catch (IOException e) {
      log.debug("Failed to change ReportType", e);
      return JSONUtils.SimpleJSONError("Failed to load study types");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject addStudyExperiment(HttpSession session, JSONObject json) {
    String studyType = null;
    Long projectId = null;
    String studyId = null;
    List<Long> ids = new ArrayList();

    JSONArray a = JSONArray.fromObject(json.get("form"));
    for (JSONObject j : (Iterable<JSONObject>) a) {

      if (j.getString("name").equals("projectId")) {
        projectId = Long.parseLong(j.getString("value"));
      }
      if (j.getString("name").equals("expids")) {
        ids.add(Long.parseLong(j.getString("value")));
      } else if (j.getString("name").equals("studyType")) {
        studyType = j.getString("value");
      }
    }

    try {
      Project p = requestManager.getProjectById(projectId);
      Study s = new StudyImpl();
      s.setProject(p);
      s.setAlias(p.getAlias());
      s.setDescription(p.getDescription());
      s.setSecurityProfile(p.getSecurityProfile());
      s.setStudyType(studyType);
      s.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
      requestManager.saveStudy(s);

      studyId = String.valueOf(s.getId());

      for (Long i : ids) {
        String title = null;
        String alias = null;
        String description = null;
        String platformIdStr = null;
        String poolBarcode = null;

        for (JSONObject j : (Iterable<JSONObject>) a) {
          if (j.getString("name").equals("title" + i)) {
            title = j.getString("value");
          } else if (j.getString("name").equals("alias" + i)) {
            alias = j.getString("value");
          } else if (j.getString("name").equals("description" + i)) {
            description = j.getString("value");
          } else if (j.getString("name").equals("platform" + i)) {
            platformIdStr = j.getString("value");
          } else if (j.getString("name").equals("pool" + i)) {
            poolBarcode = j.getString("value");
          }
        }
        Long platformId = Long.parseLong(platformIdStr);

        Experiment e = new ExperimentImpl();
        e.setStudy(s);
        e.setDescription(description);
        e.setAlias(alias);
        e.setTitle(title);
        e.setPlatform(requestManager.getPlatformById(platformId));
        if (poolBarcode != null) {
          e.setPool(requestManager.getPoolByBarcode(poolBarcode));
        }
        e.setLastModifier(securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName()));
        requestManager.saveExperiment(e);

      }
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return JSONUtils.JSONObjectResponse("studyId", studyId);
  }

  public JSONObject addExperimentForm(HttpSession session, JSONObject json) {
    String newidstr = json.get("newid").toString();
    Long newId = Long.parseLong(newidstr);
    String html = "            <div id=\"new" + (newId + 1) + "\"><a href=\"#\" class=\"add\" onclick=\"Experiment.ui.addExperimentForm("
        + (newId + 1) + ");\">Add a new experiment</a>\n" + "                   </div><br/>"
        + "<div class=\"experimentwizard ui-corner-all\" id=\"exp" + newId + "\">" + "<table class=\"in\">\n" + "                <tr>\n"
        + "                    <input type=\"hidden\" class=\"expids\" name=\"expids\" value=\"" + newId + "\"/>"
        + "                    <td class=\"h\">Title:</td>\n" + "                    <td><input type=\"text\" id=\"title" + newId
        + "\" class=\"needcheck\" name=\"title" + newId + "\"/>" + "<span onclick=\"Experiment.ui.confirmRemoveExperiment(" + newId
        + ");\" class=\"float-right ui-icon ui-icon-circle-close\" style=\"cursor:pointer;\"></span>\n" + "                </td></tr>\n"
        + "                <tr>\n" + "                    <td class=\"h\">Alias:</td>\n"
        + "                    <td><input type=\"text\" id=\"alias" + newId + "\" class=\"needcheck\" name=\"alias" + newId + "\"/></td>\n"
        + "                </tr>\n" + "                <tr>\n" + "                    <td class=\"h\">Description:</td>\n"
        + "                    <td><input type=\"text\" id=\"description" + newId + "\" class=\"needcheck\" name=\"description" + newId
        + "\"/></td>\n" + "                </tr>\n" + "                <tr>\n" + "                    <td>Platform:</td>\n"
        + "                    <td><select name=\"platform" + newId + "\" onchange=\"Experiment.pool.loadPoolsByPlatform(this, " + newId
        + ");\">\n" + populatePlatform() + "                    </select>\n" + "                    </td>\n" + "                </tr>\n"
        + "            </table>\n" + "          <div class=\"note\">\n" + "            <h2>Selected pool:</h2>\n"
        + "            <div id=\"selPool" + newId + "\" class=\"elementList ui-corner-all\"></div></div>" + "<div id=\"poolList" + newId
        + "\" class='elementList' style='height:120px; overflow:auto'></div>" + "            </div>\n";

    return JSONUtils.JSONObjectResponse("html", html);
  }

  public String populatePlatform() {
    StringBuilder a = new StringBuilder();
    try {
      for (Platform platform : requestManager.listAllPlatforms()) {
        a.append("<option value=\"" + platform.getPlatformId() + "\">" + platform.getNameAndModel() + "</option>");
      }
    } catch (IOException e) {
      log.debug("Failed", e);
    }
    return a.toString();
  }

  public JSONObject loadPoolsByPlatform(HttpSession session, JSONObject json) {
    StringBuilder a = new StringBuilder();
    try {
      if (json.has("platformId") && !isStringEmptyOrNull(json.getString("platformId"))) {
        Long platformId = json.getLong("platformId");
        Platform platform = requestManager.getPlatformById(platformId);
        if (platform != null) {
          PlatformType pt = platform.getPlatformType();
          List<Pool<? extends Poolable>> pools = new ArrayList<Pool<? extends Poolable>>(requestManager.listAllPoolsByPlatform(pt));
          Collections.sort(pools);
          for (Pool p : pools) {
            a.append("<div bind='" + p.getId()
                + "' onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard' style='position:relative' ");
            if (json.has("newid") && !isStringEmptyOrNull(json.getString("newid"))) {
              a.append("ondblclick='Experiment.pool.experimentSelectPool(this," + json.getString("newid") + ");'");
            } else {
              a.append("ondblclick='Experiment.pool.experimentSelectPool(this);'");
            }
            a.append(">");
            a.append("<span style='float:left'>");
            a.append("<b>" + p.getName() + "</b> <i>" + p.getDilutions().size() + " dilution(s)</i>");
            a.append("</span>");
            a.append("<span class='pType' style='float: right; font-size: 24px; font-weight: bold; color:#BBBBBB'>"
                + p.getPlatformType().getKey() + "</span>");
            a.append("</div>");
          }
          return JSONUtils.JSONObjectResponse("html", a.toString());
        } else {
          return JSONUtils.SimpleJSONError("Failed to load pools: no such platform");
        }
      } else {
        return JSONUtils.SimpleJSONError("Failed to load pools: no platform specified");
      }
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed to load pools: " + e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
