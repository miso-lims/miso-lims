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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Reportable;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.ReportingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.ReportsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
@Deprecated
public class ReportingControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ReportingControllerHelperService.class);

  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public JSONObject getTableColumns(HttpSession session, JSONObject json) {
    String table = json.getString("table");
    StringBuilder sb = new StringBuilder();
    try {
      ArrayList<String> columns = DbUtils.getColumns(interfaceTemplate, table);

      sb.append("<div class='divhighlight' id='").append(table).append("_div'").append("><h2>").append(table).append("</h2><div id='")
          .append(table).append("_columnlist'>");
      sb.append("<table class='paramtable' table='" + table + "'>");
      for (String s : columns) {
        sb.append("<tr>");
        sb.append("<td><input type='checkbox' table='" + table + "' column='" + s + "' id='" + table + "-" + s + "' value='" + s
            + "' onchange='toggleAddColumnToQuerySet(\"" + table + "\", this);'/>").append(s).append("</td>");
        sb.append("<td>").append(sqlOptionsDropdown(table, s)).append("</td>");
        sb.append("<td>").append("<input type='text' id='" + s + "_value'/>").append("</td>");
        sb.append("</tr>");
      }
      sb.append("</table></div></div>");
    } catch (MetaDataAccessException e) {
      log.error("get table columns", e);
      return JSONUtils.JSONObjectResponse("html", "Error: " + e.getMessage());
    } catch (SQLException e) {
      log.error("get table columns", e);
      return JSONUtils.JSONObjectResponse("html", "Error: " + e.getMessage());
    }
    return JSONUtils.JSONObjectResponse("html", sb.toString());
  }

  public JSONObject processQueryParameters(HttpSession session, JSONObject json) {
    JSONObject tables = json.getJSONObject("parameters").getJSONObject("qparams");
    log.info(tables.toString());
    for (Object tObj : tables.entrySet()) {
      JSONObject table = (JSONObject) tObj;
      for (Object tKey : table.keySet()) {
        System.out.println((String) tKey);
        JSONArray columns = (JSONArray) table.get(tKey);

        for (Object cObj : columns) {
          JSONObject column = (JSONObject) cObj;
          for (Object cKey : column.keySet()) {
            System.out.println("\t" + (String) cKey);
            JSONArray modifiers = (JSONArray) column.get(cKey);

            for (Object mObj : modifiers) {
              JSONObject modifier = (JSONObject) mObj;
              for (Object mKey : modifier.keySet()) {
                System.out.println("\t" + (String) mKey + " -> " + (String) modifier.get(mKey));
              }
            }
          }
        }
      }
    }

    return JSONUtils.JSONObjectResponse("html", "foo");
  }

  private String sqlOptionsDropdown(String table, String column) {
    StringBuilder sb = new StringBuilder();
    sb.append("<select table='" + table + "' column='" + column + "'>");
    sb.append("<option value='-'>*</option>");
    sb.append("<option value='eq'>Equals</option>");
    sb.append("<option value='li'>Like</option>");
    sb.append("<option value='lt'>Less than</option>");
    sb.append("<option value='gt'>Greater than</option>");
    sb.append("<option value='le'>Less than or equal</option>");
    sb.append("<option value='ge'>Greater than or equal</option>");
    sb.append("<option value='ne'>Not equal</option>");
    sb.append("</select>");
    return sb.toString();
  }

  public JSONObject changeReportType(HttpSession session, JSONObject json) {

    StringBuilder b = new StringBuilder();
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      if (json.has("reportType") && !isStringEmptyOrNull((String) json.get("reportType"))) {
        b.append("<input type=\"text\" onkeyup=\"Utils.timer.timedFunc(reportSearch(this), 300);\" size=\"20\" id=\"search"
            + json.get("reportType") + "\" name=\"search" + json.get("reportType") + "\"/>");
        b.append("<form id=\"reportform\" name=\"reportform\" method=\"POST\" >");
        b.append(
            "<button type=\"button\" id=\"generateReportButton\" class=\"fg-button ui-state-default ui-corner-all\"  onclick=\"generateReport('reportform');\">Generate Report</button><br/>");
        b.append("<input type=\"hidden\" name=\"reportType\" value=\"" + json.get("reportType") + "\"/>");
        if (json.get("reportType").equals("Project")) {
          b.append(
              "<br/><input type=\"checkbox\" name=\"incChart\" value=\"incChart\" checked=\"checked\"/>Include Project Status Chart &nbsp;&nbsp;&nbsp;");
          b.append("<input type=\"checkbox\" name=\"incOverview\" checked=\"checked\"/>Include Project Overview &nbsp;&nbsp;&nbsp;");
          b.append("<input type=\"checkbox\" name=\"incSamples\" checked=\"checked\"/>Include Project Samples");
          b.append("<table class=\"list\" id=\"table\">");
          b.append("<thead>\n" + "    <tr>\n"
              + "        <th><input class=\"chkbox\" type=\"checkbox\" onclick=\"Utils.ui.checkAll(document.reportform.ids)\"/> All</th>\n"
              + "        <th>Project Name</th>\n" + "        <th>Project Alias</th>\n" + "        <th>Project Description</th>\n"
              + "        <th>Progress</th>\n" + "    </tr>\n" + "    </thead>\n" + "    <tbody id=\"search" + json.get("reportType")
              + "result\">");

          b.append("</tbody>");
        } else if (json.get("reportType").equals("Sample")) {
          b.append(
              "<br/><input type=\"checkbox\" name=\"incChart\" value=\"incChart\" checked=\"checked\"/>Include Charts &nbsp;&nbsp;&nbsp;");
          b.append("<input type=\"checkbox\" name=\"incQC\" checked=\"checked\"/>Include QC Detail &nbsp;&nbsp;&nbsp;");
          b.append("<table class=\"list\" id=\"table\">");
          b.append("<thead>\n" + "    <tr>\n"
              + "        <th><input class=\"chkbox\" type=\"checkbox\" onClick=\"Utils.ui.checkAll(document.reportform.ids)\"/> All</th>\n"
              + "        <th>Sample Name</th>\n" + "        <th>Sample Alias</th>\n" + "        <th>Sample Description</th>\n"
              + "        <th>Sample Type</th>\n" + "    </tr>\n" + "    </thead>\n" + "    <tbody id=\"search" + json.get("reportType")
              + "result\">");
          b.append("</tbody>");
        } else if (json.get("reportType").equals("Run")) {
          b.append(
              "<br/><input type=\"checkbox\" name=\"incChart\" value=\"incChart\" checked=\"checked\"/>Include Charts &nbsp;&nbsp;&nbsp;");
          b.append("<input type=\"checkbox\" name=\"incAlias\" checked=\"checked\"/>Include Run Alias &nbsp;&nbsp;&nbsp;");
          b.append("<table class=\"list\" id=\"table\">");
          b.append("<thead>\n" + "    <tr>\n"
              + "        <th><input class=\"chkbox\" type=\"checkbox\" onClick=\"Utils.ui.checkAll(document.reportform.ids)\"/> All</th>\n"
              + "        <th>Run Name</th>\n" + "        <th>Run Alias</th>\n" + "        <th>Status</th>\n" + "    </tr>\n"
              + "    </thead>\n" + "    <tbody id=\"search" + json.get("reportType") + "result\">");
          b.append("</tbody>");
        } else {
          b.append("Unrecognised ReportType");
        }
        b.append("</form>");
        b.append("</table>");
      }
    } catch (IOException e) {
      log.debug("Failed to change ReportType", e);
      return JSONUtils.SimpleJSONError("Failed to change ReportType");
    }
    return JSONUtils.JSONObjectResponse("html", b.toString());
  }

  public JSONObject searchProject(HttpSession session, JSONObject json) {
    String searchStr = (String) json.get("str");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!isStringEmptyOrNull(searchStr)) {
        if (searchStr.length() > 1) {
          String str = searchStr.toLowerCase();

          StringBuilder b = new StringBuilder();

          int numMatches = 0;
          for (Project project : requestManager.listAllProjects()) {
            String projectName = project.getName() == null ? null : project.getName().toLowerCase();
            String projectAlias = project.getAlias() == null ? null : project.getAlias().toLowerCase();
            String projectDesc = project.getDescription() == null ? null : project.getDescription().toLowerCase();

            if (projectName != null && (projectName.equals(str) || projectName.contains(str) || projectAlias.equals(str)
                || projectAlias.contains(str) || projectDesc.equals(str) || projectDesc.contains(str))) {
              b.append("<tr><td><input class=\"chkbox\" id=\"" + project.getProjectId() + "\" type=\"checkbox\" name=\"ids\" value=\""
                  + project.getProjectId() + "\" id=\"" + project.getProjectId() + "\"/></td>");
              b.append("<td>" + project.getName());
              b.append("</td>");
              b.append("<td> " + project.getAlias());
              b.append("</td>");
              b.append("<td> " + project.getDescription());
              b.append("</td>");
              b.append("<td> " + project.getProgress().name());
              b.append("</td>");
              b.append("</tr>");
              numMatches++;
            }
          }

          if (numMatches == 0) {
            b.append("No matches");
          }
          return JSONUtils.JSONObjectResponse("html", b.toString());
        } else {
          return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
        }
      } else {
        StringBuilder b = new StringBuilder();
        for (Project project : requestManager.listAllProjects()) {
          b.append("<tr><td><input class=\"chkbox\" id=\"" + project.getProjectId() + "\" type=\"checkbox\" name=\"ids\" value=\""
              + project.getProjectId() + "\" id=\"" + project.getProjectId() + "\"/></td>");
          b.append("<td>" + project.getName());
          b.append("</td>");
          b.append("<td> " + project.getAlias());
          b.append("</td>");
          b.append("<td> " + project.getDescription());
          b.append("</td>");
          b.append("<td> " + project.getProgress().name());
          b.append("</td>");
          b.append("</tr>");
        }
        return JSONUtils.JSONObjectResponse("html", b.toString());
      }
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  // sample

  public JSONObject searchSample(HttpSession session, JSONObject json) {
    String searchStr = (String) json.get("str");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!isStringEmptyOrNull(searchStr)) {
        if (searchStr.length() > 1) {
          String str = searchStr.toLowerCase();

          StringBuilder b = new StringBuilder();

          int numMatches = 0;
          for (Sample sample : requestManager.listAllSamples()) {
            String sampleName = sample.getName() == null ? null : sample.getName().toLowerCase();
            String sampleAlias = sample.getAlias() == null ? null : sample.getAlias().toLowerCase();
            String sampleDesc = sample.getDescription() == null ? null : sample.getDescription().toLowerCase();

            if (sampleName != null && (sampleName.equals(str) || sampleName.contains(str) || sampleAlias.equals(str)
                || sampleAlias.contains(str) || sampleDesc.equals(str) || sampleDesc.contains(str))) {
              b.append("<tr><td><input type=\"checkbox\" class=\"chkbox\" name=\"ids\" value=\"" + sample.getId() + "\" id=\""
                  + sample.getId() + "\"/></td>");
              b.append("<td>" + sample.getName());
              b.append("</td>");
              b.append("<td> " + sample.getAlias());
              b.append("</td>");
              b.append("<td> " + sample.getDescription());
              b.append("</td>");
              b.append("<td> " + sample.getSampleType());
              b.append("</td>");
              b.append("</tr>");
              numMatches++;
            }
          }

          if (numMatches == 0) {
            b.append("No matches");
          }
          return JSONUtils.JSONObjectResponse("html", b.toString());
        } else {
          return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
        }
      } else {
        StringBuilder b = new StringBuilder();
        for (Sample sample : requestManager.listAllSamples()) {
          b.append("<tr><td><input type=\"checkbox\" class=\"chkbox\" name=\"ids\" value=\"" + sample.getId() + "\" id=\"" + sample.getId()
              + "\"/></td>");
          b.append("<td>" + sample.getName());
          b.append("</td>");
          b.append("<td> " + sample.getAlias());
          b.append("</td>");
          b.append("<td> " + sample.getDescription());
          b.append("</td>");
          b.append("<td> " + sample.getSampleType());
          b.append("</td>");
          b.append("</tr>");
        }
        return JSONUtils.JSONObjectResponse("html", b.toString());
      }
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  // run

  public JSONObject searchRun(HttpSession session, JSONObject json) {
    String searchStr = (String) json.get("str");
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!isStringEmptyOrNull(searchStr)) {
        if (searchStr.length() > 1) {
          String str = searchStr.toLowerCase();

          StringBuilder b = new StringBuilder();

          int numMatches = 0;
          for (Run run : requestManager.listAllRuns()) {
            String runName = run.getName() == null ? null : run.getName().toLowerCase();
            String runAlias = run.getAlias() == null ? null : run.getAlias().toLowerCase();
            String runDesc = run.getDescription() == null ? null : run.getDescription().toLowerCase();

            if (runName != null && (runName.equals(str) || runName.contains(str) || runAlias.equals(str) || runAlias.contains(str)
                || runDesc.equals(str) || runDesc.contains(str))) {
              b.append("<tr><td><input type=\"checkbox\" class=\"chkbox\" name=\"ids\" value=\"" + run.getId() + "\" id=\"" + run.getId()
                  + "\"/></td>");
              b.append("<td>" + run.getName());
              b.append("</td>");
              b.append("<td> " + run.getAlias());
              b.append("</td>");
              b.append("<td> " + run.getStatus().getHealth().getKey());
              b.append("</td>");
              b.append("</tr>");
              numMatches++;
            }
          }

          if (numMatches == 0) {
            b.append("No matches");
          }
          return JSONUtils.JSONObjectResponse("html", b.toString());
        } else {
          return JSONUtils.JSONObjectResponse("html", "Need a longer search pattern ...");
        }
      } else {
        StringBuilder b = new StringBuilder();
        for (Run run : requestManager.listAllRuns()) {
          b.append("<tr><td><input type=\"checkbox\" class=\"chkbox\" name=\"ids\" value=\"" + run.getId() + "\" id=\"" + run.getId()
              + "\"/></td>");
          b.append("<td>" + run.getName());
          b.append("</td>");
          b.append("<td> " + run.getAlias());
          b.append("</td>");
          b.append("<td> " + run.getStatus().getHealth().getKey());
          b.append("</td>");
          b.append("</tr>");
        }
        return JSONUtils.JSONObjectResponse("html", b.toString());
      }
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject generateReport(HttpSession session, JSONObject json) {
    String type = null;
    String html = null;
    JSONArray a = JSONArray.fromObject(json.get("form"));
    List<Reportable> reportables = new ArrayList<Reportable>();
    List<String> options = new ArrayList();

    for (JSONObject j : (Iterable<JSONObject>) a) {
      if (j.getString("name").equals("incChart")) {
        options.add("Chart");
        break;
      }
    }

    if (a.getJSONObject(0).getString("value").equals("Project")) {
      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("ids")) {
          try {
            Project p = requestManager.getProjectById(new Long(j.getString("value")));
            if (p != null) {
              reportables.add(p);
            }
          } catch (IOException e) {
            log.debug(" ", e);
          }
        } else if (j.getString("name").equals("incOverview")) {
          options.add("Overview");
        } else if (j.getString("name").equals("incSamples")) {
          options.add("Samples");
        }
      }
      type = "Project";

      try {
        if (type != null) {
          html = ReportsUtils.buildHTMLReport(reportables, type, options);
        } else {
          return JSONUtils.SimpleJSONError("Null ReportType");
        }
      } catch (ReportingException e) {
        log.error("generate report", e);
        return JSONUtils.SimpleJSONError("Failed to generate");
      }
    } else if (a.getJSONObject(0).getString("value").equals("Sample")) {
      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("ids")) {
          try {
            Sample s = requestManager.getSampleById(new Long(j.getString("value")));
            if (s != null) {
              reportables.add(s);
            }
          } catch (IOException e) {
            log.debug(" ", e);
          }
        } else if (j.getString("name").equals("incQC")) {
          options.add("QC");
        }
      }
      type = "Sample";

      try {
        if (type != null) {
          html = ReportsUtils.buildHTMLReport(reportables, type, options);
        } else {
          return JSONUtils.SimpleJSONError("Null ReportType");
        }
      } catch (ReportingException e) {
        log.error("generate report", e);
        return JSONUtils.SimpleJSONError("Failed to generate");
      }
    } else if (a.getJSONObject(0).getString("value").equals("Run")) {
      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("ids")) {
          try {
            Run r = requestManager.getRunById(new Long(j.getString("value")));
            if (r != null) {
              reportables.add(r);
            }
          } catch (IOException e) {
            log.debug(" ", e);
          }
        } else if (j.getString("name").equals("incAlias")) {
          options.add("Alias");
        } else if (j.getString("name").equals("incDescription")) {
          options.add("Description");
        }
      }
      type = "Run";

      try {
        if (type != null) {
          html = ReportsUtils.buildHTMLReport(reportables, type, options);
        } else {
          return JSONUtils.SimpleJSONError("Null ReportType");
        }
      } catch (ReportingException e) {
        log.error("generate report", e);
        return JSONUtils.SimpleJSONError("Failed to generate");
      }
    }
    return JSONUtils.JSONObjectResponse("html", html);
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
