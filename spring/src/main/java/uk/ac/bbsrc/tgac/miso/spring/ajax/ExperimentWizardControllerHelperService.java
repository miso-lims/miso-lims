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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 19-Apr-2011
 * Time: 12:04:04
 * To change this template use File | Settings | File Templates.
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
    }
    catch (IOException e) {
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
    }
    catch (IOException e) {
      log.debug("Failed to change ReportType", e);
      return JSONUtils.SimpleJSONError("Failed to load study types");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject addStudyExperiment(HttpSession session, JSONObject json) {
    String studyType = null;
    Long projectId = null;
//    String limitStr = null;
    String studyId = null;
    List<Long> ids = new ArrayList();

    JSONArray a = JSONArray.fromObject(json.get("form"));
    for (JSONObject j : (Iterable<JSONObject>) a) {

      if (j.getString("name").equals("projectId")) {
        projectId = Long.parseLong(j.getString("value"));
      }
      if (j.getString("name").equals("expids")) {
        ids.add(Long.parseLong(j.getString("value")));
      }
      else if (j.getString("name").equals("studyType")) {
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
      requestManager.saveStudy(s);

      studyId = s.getStudyId().toString();

      for (Long i : ids) {
        String title = null;
        String alias = null;
        String description = null;
        String platformIdStr = null;
        String poolBarcode = null;

        for (JSONObject j : (Iterable<JSONObject>) a) {
          if (j.getString("name").equals("title" + i)) {
            title = j.getString("value");
          }
          else if (j.getString("name").equals("alias" + i)) {
            alias = j.getString("value");
          }
          else if (j.getString("name").equals("description" + i)) {
            description = j.getString("value");
          }
          else if (j.getString("name").equals("platform" + i)) {
            platformIdStr = j.getString("value");
          }
          else if (j.getString("name").equals("pool" + i)) {
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
        requestManager.saveExperiment(e);

      }
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return JSONUtils.JSONObjectResponse("studyId", studyId);
  }

  public JSONObject addExperimentForm(HttpSession session, JSONObject json) {
    String newidstr = json.get("newid").toString();
    Long newId = Long.parseLong(newidstr);
    String html =
            "            <div id=\"new" + (newId + 1) + "\"><a href=\"#\" class=\"add\" onclick=\"addExperimentForm(" + (newId + 1) + ");\">Add a new experiment</a>\n" +
            "                   </div><br/>" +
            "<div class=\"experimentwizard ui-corner-all\" id=\"exp" + newId + "\">" +
            "<table class=\"in\">\n" +
            "                <tr>\n" +
            "                    <input type=\"hidden\" class=\"expids\" name=\"expids\" value=\"" + newId + "\"/>" +
            "                    <td class=\"h\">Title:</td>\n" +
            "                    <td><input type=\"text\" id=\"title" + newId + "\" class=\"needcheck\" name=\"title" + newId + "\"/>" +
            "<span onclick=\"confirmRemoveExperiment(" + newId + ");\" class=\"float-right ui-icon ui-icon-circle-close\" style=\"cursor:pointer;\"></span>\n" +
            "                </td></tr>\n" +
            "                <tr>\n" +
            "                    <td class=\"h\">Alias:</td>\n" +
            "                    <td><input type=\"text\" id=\"alias" + newId + "\" class=\"needcheck\" name=\"alias" + newId + "\"/></td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td class=\"h\">Description:</td>\n" +
            "                    <td><input type=\"text\" id=\"description" + newId + "\" class=\"needcheck\" name=\"description" + newId + "\"/></td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td>Platform:</td>\n" +
            "                    <td><select name=\"platform" + newId + "\" onchange=\"loadPoolsbyPlatform(this, " + newId + ");\">\n" +
            populatePlatform() +
            "                    </select>\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "            </table>\n" +
            "<div id=\"pools" + newId + "\"></div>" +
            "            </div>\n";

    return JSONUtils.JSONObjectResponse("html", html);
  }

  public String populatePlatform() {

    StringBuilder a = new StringBuilder();
    try {

      for (Platform platform : requestManager.listAllPlatforms()) {
        a.append("<option value=\"" + platform.getPlatformId() + "\">" + platform.getNameAndModel() + "</option>");
      }
    }
    catch (IOException e) {
      log.debug("Failed", e);
    }
    return a.toString();
  }

  public JSONObject loadPoolsbyPlatform(HttpSession session, JSONObject json) {
    StringBuilder a = new StringBuilder();
    try {
      String newidstr = json.get("newid").toString();
      Long newId = Long.parseLong(newidstr);
      String platformidstr = json.get("platformId").toString();
      Long platformId = Long.parseLong(platformidstr);
      Platform platform = requestManager.getPlatformById(platformId);
      a.append("<table class=\"in\">" +
               "<tr>" +
               "<td class=\"h\">Pool:</td>" + "<td><input type=\"text\" id='" + newId + "' name=\"pool" + newId + "\" value=\"\" onKeyup=\"timedFunc(poolSearchType(this,'" + platform.getPlatformType().getKey() + "'),200);\"/>" +
               "<div id='poolresult" + newId + "'></div></td></tr></table>");
    }
    catch (IOException e) {
      log.debug("Failed", e);
    }

    return JSONUtils.JSONObjectResponse("html", a.toString());
  }

  public JSONObject editloadPoolsbyPlatform(HttpSession session, JSONObject json) {
    StringBuilder a = new StringBuilder();
    try {
      String platformidstr = json.get("platformId").toString();
      Long platformId = Long.parseLong(platformidstr);
      Platform platform = requestManager.getPlatformById(platformId);
      a.append("<table class=\"in\">" +
               "<tr>" +
               "<td class=\"h\">Pool:</td>" + "<td><input type=\"text\" id='poolinput' name=\"pool\" value=\"\" onKeyup=\"timedFunc(editpoolSearchType(this,'" + platform.getPlatformType().getKey() + "'),200);\"/>" +
               "<div id='poolresult'></div></td></tr></table>");
    }
    catch (IOException e) {
      log.debug("Failed", e);
    }

    return JSONUtils.JSONObjectResponse("html", a.toString());
  }

  public JSONObject poolSearchType(HttpSession session, JSONObject json) {
    StringBuffer sb = new StringBuffer();
    String searchStr = (String) json.get("str");
    String resultId = (String) json.get("id");
    String platformType = (String) json.get("type");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();

        Collection<Pool<? extends Poolable>> pools = requestManager.listAllPoolsByPlatform(PlatformType.valueOf(platformType));
        /*
        List<? extends Pool> pools = new ArrayList<Pool>();
        if (platformType.equals("Illumina")) {
          pools = requestManager.listAllIlluminaPools();
        }
        else if (platformType.equals("LS454")) {
          pools = requestManager.listAll454Pools();
        }
        else if (platformType.equals("Solid")) {
          pools = requestManager.listAllSolidPools();
        }
        */
        int numMatches = 0;
        for (Pool p : pools) {
          String poolName = p.getName() == null ? null : p.getName();
          String poolBarcode = p.getIdentificationBarcode() == null ? null : p.getIdentificationBarcode();
          StringBuilder ds = new StringBuilder();
          if (p.getDilutions().size() == 0) {
            ds.append("none");
          }
          else {
            for (Dilution dl : (List<Dilution>) p.getDilutions()) {
              ds.append(dl.getName() + " ");
            }
          }
          long poolId = p.getPoolId();

          if ((poolBarcode != null && (poolBarcode.toLowerCase().equals(str) || poolBarcode.toLowerCase().contains(str)))
              || (poolName != null && (poolName.toLowerCase().equals(str) || poolName.toLowerCase().contains(str)))) {
            b.append("<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\"" +
                     " onclick=\"insertPoolResult(&#39;" + resultId + "&#39;,&#39;" + poolBarcode + "&#39;)\">" +
                     "<b>Pool: " + poolName + "(" + poolBarcode + ")</b><br/>" +
                     "Dilutions: " + ds.toString() +
                     "</div>");
            numMatches++;
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      }
      else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed");
    }
  }

  public JSONObject editpoolSearchType(HttpSession session, JSONObject json) {
    StringBuffer sb = new StringBuffer();
    String searchStr = (String) json.get("str");
    String resultId = "poolinput";
    String platformType = (String) json.get("type");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (searchStr.length() > 1) {
        String str = searchStr.toLowerCase();
        StringBuilder b = new StringBuilder();
        Collection<Pool<? extends Poolable>> pools = requestManager.listAllPoolsByPlatform(PlatformType.valueOf(platformType));
/*
        if (platformType.equals("Illumina")) {
          pools = requestManager.listAllIlluminaPools();
        }
        else if (platformType.equals("LS454")) {
          pools = requestManager.listAll454Pools();
        }
        else if (platformType.equals("Solid")) {
          pools = requestManager.listAllSolidPools();
        }
*/
        int numMatches = 0;
        for (Pool p : pools) {
          String poolName = p.getName() == null ? null : p.getName();
          String poolBarcode = p.getIdentificationBarcode() == null ? null : p.getIdentificationBarcode();
          StringBuilder ds = new StringBuilder();
          if (p.getDilutions().size() == 0) {
            ds.append("none");
          }
          else {
            for (Dilution dl : (List<Dilution>) p.getDilutions()) {
              ds.append(dl.getName() + " ");
            }
          }
          long poolId = p.getPoolId();

          if ((poolBarcode != null && (poolBarcode.toLowerCase().equals(str) || poolBarcode.toLowerCase().contains(str)))
              || (poolName != null && (poolName.toLowerCase().equals(str) || poolName.toLowerCase().contains(str)))) {
            b.append("<div onmouseover=\"this.className='autocompleteboxhighlight'\" onmouseout=\"this.className='autocompletebox'\" class=\"autocompletebox\"" +
                     " onclick=\"editinsertPoolResult(&#39;" + poolId + "&#39,&#39;" + poolBarcode + "&#39;)\">" +
                     "<b>Pool: " + poolName + "(" + poolBarcode + ")</b><br/>" +
                     "Dilutions: " + ds.toString() +
                     "</div>");
            numMatches++;
          }
        }
        if (numMatches == 0) {
          return JSONUtils.JSONObjectResponse("html", "No matches");
        }
        else {
          return JSONUtils.JSONObjectResponse("html", "<div class=\"autocomplete\"><ul>" + b.toString() + "</ul></div>");
        }
      }
      else {
        return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
      }
    }
    catch (IOException e) {
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
