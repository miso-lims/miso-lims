/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.RunService;

/**
 * uk.ac.bbsrc.tgac.miso.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class DashboardHelperService {
  protected static final Logger log = LoggerFactory.getLogger(DashboardHelperService.class);
  @Autowired
  private ProjectService projectService;
  @Autowired
  private RunService runService;

  private StringBuilder generateDashboardCell(StringBuilder b, String misoClass, Long id, String name, String alias,
      String... aliasAlternative) {
    b.append("<a class=\"dashboardresult\" href=\"/miso/" + misoClass + "/" + id
        + "\"><div onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
    b.append("Name: <b>" + name + "</b><br/>");
    if (aliasAlternative == null || aliasAlternative.length == 0) {
      b.append("Alias");
    } else {
      b.append((isStringEmptyOrNull(aliasAlternative[0]) ? "Alias"
          : aliasAlternative[0]));
    }
    b.append(": <b>" + alias + "</b><br/>");
    b.append("</div></a>");
    return b;
  }

  private StringBuilder generateProjectDashboardCell(StringBuilder b, String misoClass, Long id, String name, String alias,
      String shortName) {
    b.append("<a class=\"dashboardresult\" href=\"/miso/" + misoClass + "/" + id
        + "\"><div onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
    b.append("Name: <b>" + name + "</b><br/>");
    if (shortName != null) {
      b.append("Short Name: <b>" + shortName + "</b><br/>");
    }
    b.append("Alias: <b>" + alias + "</b><br/>");
    b.append("</div></a>");
    return b;
  }

  public JSONObject searchProject(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Project> projects;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        projects = new ArrayList<>(projectService.listAllProjectsBySearch(searchStr));
      } else {
        projects = new ArrayList<>(projectService.listAllProjectsWithLimit(50));
      }

      if (projects.size() > 0) {
        Collections.sort(projects);
        Collections.reverse(projects);
        for (Project p : projects) {
          generateProjectDashboardCell(b, "project", p.getId(), p.getName(), p.getAlias(), p.getShortName());
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());

    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchRun(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      Collection<Run> runs;
      if (!isStringEmptyOrNull(searchStr)) {
        runs = new ArrayList<>(runService.list(0, 0, false, "startDate",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        runs = new ArrayList<>(runService.list(0, 50, false, "startDate"));
      }

      StringBuilder b = new StringBuilder();
      if (runs.size() > 0) {
        for (Run r : runs) {
          generateDashboardCell(b, "run", r.getId(), r.getName(), r.getAlias());
        }
      } else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }
}
