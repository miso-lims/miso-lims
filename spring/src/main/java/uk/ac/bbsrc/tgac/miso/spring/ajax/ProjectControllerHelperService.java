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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class ProjectControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ProjectControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private IssueTrackerManager issueTrackerManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private SampleService sampleService;

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setIssueTrackerManager(IssueTrackerManager issueTrackerManager) {
    this.issueTrackerManager = issueTrackerManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public JSONObject validateProjectShortName(HttpSession session, JSONObject json) {
    if (!json.has("shortName")) {
      return JSONUtils.SimpleJSONError("No shortName specified");
    } else {
      String shortName = json.getString("shortName");
      ValidationResult shortNameValidation = namingScheme.validateProjectShortName(shortName);
      if (shortNameValidation.isValid()) {
        log.info("Project shortName OK!");
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        log.error("Project shortName not valid: " + shortName);
        return JSONUtils.SimpleJSONError(shortNameValidation.getMessage());
      }
    }
  }

  public JSONObject addProjectOverview(HttpSession session, JSONObject json) {
    final Long projectId = json.getLong("projectId");
    final String principalInvestigator = (String) json.get("principalInvestigator");
    final Integer numProposedSamples = json.getInt("numProposedSamples");

    try {
      final Project project = projectService.get(projectId);
      final ProjectOverview overview = new ProjectOverview();
      overview.setNumProposedSamples(numProposedSamples);
      overview.setPrincipalInvestigator(principalInvestigator);
      overview.setProject(project);
      overview.setLocked(false);
      project.getOverviews().add(overview);
      projectService.saveProjectOverview(overview);
      projectService.saveProject(project);
    } catch (final IOException e) {
      log.error("add project overview", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject unlockProjectOverview(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      overview.setLocked(false);
      projectService.saveProjectOverview(overview);
      projectService.saveProject(overview.getProject());
    } catch (final IOException e) {
      log.error("unlock project overview", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject lockProjectOverview(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      overview.setLocked(true);
      projectService.saveProjectOverview(overview);
      projectService.saveProject(overview.getProject());
    } catch (final IOException e) {
      log.error("lock project overview", e);
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("ok");
  }

  public JSONObject generateSampleDeliveryForm(HttpSession session, JSONObject json) {
    Boolean plate = false;
    if ("yes".equals(json.getString("plate"))) {
      plate = true;
    }
    final Long projectId = json.getLong("projectId");
    final List<Sample> samples = new ArrayList<>();
    if (json.has("samples")) {
      try {
        JSONArray sampleIds = JSONArray.fromObject(json.getString("samples"));
        for (int index = 0; index < sampleIds.size(); index++) {
          samples.add(sampleService.get(sampleIds.getLong(index)));
        }
        final File f = misoFileManager.getNewFile(Project.class, projectId.toString(),
            "SampleInformationForm-" + LimsUtils.getCurrentDateAsString() + ".odt");

        FormUtils.createSampleDeliveryForm(samples, f, plate);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      } catch (final Exception e) {
        log.error("generate sample deliver form", e);
        return JSONUtils.SimpleJSONError("Failed to create sample delivery form: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("No samples selected to generate delivery form");
    }
  }

  public JSONObject downloadBulkSampleInputForm(HttpSession session, JSONObject json) {
    if (json.has("projectId") && json.has("documentFormat")) {
      final Long projectId = json.getLong("projectId");
      final String documentFormat = json.getString("documentFormat");
      try {
        final File f = misoFileManager.getNewFile(Project.class, projectId.toString(),
            "BulkInputForm-" + LimsUtils.getCurrentDateAsString() + "." + documentFormat);
        FormUtils.createSampleInputSpreadsheet(projectService.get(projectId).getSamples(), f);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      } catch (final Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to get bulk input form: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
    }
  }

  public JSONObject visualiseBulkSampleInputForm(HttpSession session, JSONObject json) {
    final JSONObject samplelist = (JSONObject) session.getAttribute("bulksamples");
    if (samplelist == null) {
      final JSONObject error = (JSONObject) session.getAttribute("bulkerror");
      if (error != null) {
        return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session: " + error.getString("bulkerror"));
      }
      return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session.");
    }

    final JSONArray samples = samplelist.getJSONArray("bulksamples");
    if (samples != null) {
      final StringBuilder sb = new StringBuilder();
      sb.append("<div style='width: 100%;'>");
      final ObjectMapper mapper = new ObjectMapper();
      for (final JSONObject sam : (Iterable<JSONObject>) samples) {
        Sample s = null;
        try {
          s = mapper.readValue(sam.toString(), SampleImpl.class);
          sb.append("<a class=\"dashboardresult\" href=\"/miso/sample/" + s.getId()
              + "\"><div onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          sb.append("Name: <b>" + s.getName() + "</b><br/>");
          sb.append("Alias: <b>" + s.getAlias() + "</b><br/>");

          final Set<Pool> pools = new HashSet<>();

          if (!pools.isEmpty()) {
            sb.append("Pools: <ul>");
            for (final Pool p : pools) {
              sb.append("<li>").append(p.getName()).append(" (").append(p.getAlias()).append(") - ")
                  .append(p.getPoolDilutions().size())
                  .append(" dilutions<li>");
            }
            sb.append("</ul><br/>");
          }

          sb.append("</div></a>");
        } catch (final IOException e) {
          log.error("visualise bulk sample input form", e);
        } finally {
          sb.append("</div>");
          session.removeAttribute("bulksamples");
        }
      }

      return JSONUtils.SimpleJSONResponse(sb.toString());
    } else {
      return JSONUtils.SimpleJSONError("Failed to get bulk input sheet from session.");
    }
  }

  public JSONObject downloadPlateInputForm(HttpSession session, JSONObject json) {
    if (json.has("projectId") && json.has("documentFormat")) {
      final Long projectId = json.getLong("projectId");
      final String documentFormat = json.getString("documentFormat");
      try {
        final File f = misoFileManager.getNewFile(Project.class, projectId.toString(),
            "PlateInputForm-" + LimsUtils.getCurrentDateAsString() + "." + documentFormat);
        // TODO select a single sample to base sheet on?
        FormUtils.createPlateInputSpreadsheet(f);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      } catch (final Exception e) {
        log.error("download plate input", e);
        return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
      }
    } else {
      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
    }
  }

  public JSONObject watchOverview(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      projectService.addProjectWatcher(overview.getProject(), user);
      return JSONUtils.SimpleJSONResponse("OK");
    } catch (final IOException e) {
      log.error("watch overview", e);
    }
    return JSONUtils.SimpleJSONError("Unable to watch/unwatch overview");
  }

  public JSONObject unwatchOverview(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      projectService.removeProjectWatcher(overview.getProject(), user);
      if (!overview.getProject().getSecurityProfile().getOwner().equals(user)) {
        return JSONUtils.SimpleJSONResponse("OK");
      } else {
        return JSONUtils.SimpleJSONError("Cannot unwatch an entity of which you are the owner.");
      }
    } catch (final IOException e) {
      log.error("unwatch overview", e);
    }
    return JSONUtils.SimpleJSONError("Unable to watch/unwatch overview");
  }

  public JSONObject listWatchOverview(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    final StringBuilder sb = new StringBuilder();
    final JSONObject j = new JSONObject();
    try {
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      sb.append("<ul class='bullets' style='margin-left: -30px;'>");
      for (final User theUser : overview.getWatchers()) {
        sb.append("<li>");
        sb.append(theUser.getFullName());
        sb.append("</li>");
      }
      sb.append("</ul>");
      j.put("watchers", sb.toString());
      return j;
    } catch (final IOException e) {
      log.error("list watch overview", e);
    }
    return JSONUtils.SimpleJSONError("Unable to list watchers");
  }

  public JSONObject listSamplesByProject(HttpSession session, JSONObject json) {
    final Long projectId = json.getLong("projectId");

    try {
      final JSONObject j = new JSONObject();
      final JSONArray jsonArray = new JSONArray();
      for (final Sample sample : sampleService.listByProjectId(projectId)) {
        jsonArray.add("{'id':'" + sample.getId() + "'," + "'name':'" + sample.getName() + "'," + "'alias':'" + sample.getAlias() + "',"
            + "'type':'" + sample.getSampleType() + "'," + "'description':'" + sample.getDescription() + "'}");
      }
      j.put("array", jsonArray);
      return j;
    } catch (final IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject addSampleGroup(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);

      final Set<Sample> samples = new HashSet<>();
      if (json.has("samples")) {
        final JSONArray a = JSONArray.fromObject(json.get("samples"));
        for (final JSONObject j : (Iterable<JSONObject>) a) {
          if (j.has("sampleId")) {
            samples.add(sampleService.get(j.getLong("sampleId")));
          } else {
            return JSONUtils.SimpleJSONError("Unable to add Sample Group: invalid sample set JSON has missing sampleId");
          }
        }
      }

      overview.setSampleGroup(samples);

      projectService.saveProjectOverview(overview);
      projectService.saveProject(overview.getProject());

      return JSONUtils.SimpleJSONResponse("OK");
    } catch (final IOException e) {
      log.error("add sample group", e);
      return JSONUtils.SimpleJSONError("Unable to add Sample Group: " + e.getMessage());
    }
  }

  public JSONObject addSamplesToGroup(HttpSession session, JSONObject json) {
    final Long overviewId = json.getLong("overviewId");
    try {
      final ProjectOverview overview = projectService.getProjectOverviewById(overviewId);
      if (json.has("samples")) {
        final JSONArray a = JSONArray.fromObject(json.get("samples"));
        for (final JSONObject j : (Iterable<JSONObject>) a) {
          if (j.has("sampleId")) {
            final Sample s = sampleService.get(j.getLong("sampleId"));
            if (overview.getSamples().contains(s)) {
              log.error("Sample group already contains " + s.getName());
            } else {
              overview.getSamples().add(s);
            }
          } else {
            return JSONUtils.SimpleJSONError("Unable to add Sample Group: invalid sample set JSON has missing sampleId");
          }
        }
      }

      projectService.saveProjectOverview(overview);
      projectService.saveProject(overview.getProject());

      return JSONUtils.SimpleJSONResponse("OK");
    } catch (final IOException e) {
      log.error("add samples to group", e);
      return JSONUtils.SimpleJSONError("Unable to add Sample Group: " + e.getMessage());
    }
  }
}
