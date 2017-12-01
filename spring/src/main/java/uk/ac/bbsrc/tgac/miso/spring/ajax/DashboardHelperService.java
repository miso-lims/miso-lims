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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

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
  private ExperimentService experimentService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private RunService runService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private StudyService studyService;
  @Autowired
  private PoolService poolService;

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
          generateDashboardCell(b, "project", p.getProjectId(), p.getName(), p.getAlias());
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

  public JSONObject searchPool(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      Collection<Pool> pools;
      if (!isStringEmptyOrNull(searchStr)) {
        pools = new ArrayList<>(poolService.list(0, 0, false, "id",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        pools = new ArrayList<>(poolService.list(0, 50, false, "id"));
      }

      StringBuilder b = new StringBuilder();
      if (pools.size() > 0) {
        for (Pool p : pools) {
          generateDashboardCell(b, "pool", p.getId(), p.getName(), p.getAlias());
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

  public JSONObject searchStudy(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Study> studies;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        studies = new ArrayList<>(studyService.listBySearch(searchStr));
      } else {
        studies = new ArrayList<>(studyService.listWithLimit(50));
      }

      if (studies.size() > 0) {
        Collections.sort(studies);
        Collections.reverse(studies);
        for (Study s : studies) {
          generateDashboardCell(b, "study", s.getId(), s.getName(), s.getAlias());
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

  public JSONObject searchExperiment(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Experiment> experiments;
      StringBuilder b = new StringBuilder();
      if (!isStringEmptyOrNull(searchStr)) {
        experiments = new ArrayList<>(experimentService.listAllBySearch(searchStr));
      } else {
        experiments = new ArrayList<>(experimentService.listAllWithLimit(50));
      }

      if (experiments.size() > 0) {
        Collections.sort(experiments);
        Collections.reverse(experiments);
        for (Experiment e : experiments) {
          generateDashboardCell(b, "experiment", e.getId(), e.getName(), e.getAlias());
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

  public JSONObject searchLibraryDilution(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      Collection<LibraryDilution> dilutions;
      if (!isStringEmptyOrNull(searchStr)) {
        dilutions = new ArrayList<>(dilutionService.list(0, 0, false, "id",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        dilutions = new ArrayList<>(dilutionService.list(0, 50, false, "id"));
      }

      StringBuilder b = new StringBuilder();
      if (dilutions.size() > 0) {
        for (LibraryDilution ld : dilutions) {
          if (ld != null) {
            if (ld.getLibrary() != null) {
              String libraryAliasAndName = ld.getLibrary().getAlias() + " (" + ld.getLibrary().getName() + ")";
              generateDashboardCell(b, "library", ld.getLibrary().getId(), ld.getName(), libraryAliasAndName, "From Library");
            }
          }
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

  public JSONObject searchLibrary(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");

    try {
      Collection<Library> libraries;
      if (!isStringEmptyOrNull(searchStr)) {
        libraries = new ArrayList<>(libraryService.list(0, 0, false, "id",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        libraries = new ArrayList<>(libraryService.list(0, 50, false, "id"));
      }

      StringBuilder b = new StringBuilder();
      if (libraries.size() > 0) {
        for (Library l : libraries) {
          generateDashboardCell(b, "library", l.getId(), l.getName(), l.getAlias());
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

  public JSONObject searchSample(HttpSession session, JSONObject json) throws IOException {
    String searchStr = json.getString("str");
    try {
      Collection<Sample> samples;
      if (!isStringEmptyOrNull(searchStr)) {
        samples = new ArrayList<>(sampleService.list(0, 0, false, "id",
            PaginationFilter.parse(searchStr, SecurityContextHolder.getContext().getAuthentication().getName(), x -> {
              // Discard errors
            })));
      } else {
        samples = new ArrayList<>(sampleService.list(0, 50, false, "id"));
      }

      StringBuilder b = new StringBuilder();
      if (samples.size() > 0) {
        for (Sample s : samples) {
          generateDashboardCell(b, "sample", s.getId(), s.getName(), s.getAlias());
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

  public JSONObject showLatestReceivedSamples(HttpSession session, JSONObject json) {
    try {
      StringBuilder b = new StringBuilder();
      Collection<Sample> samples = sampleService.listByReceivedDate(100);

      Set<Long> uniqueProjects = new HashSet<>();

      if (samples.size() > 0) {
        for (Sample s : samples) {
          if (s.getReceivedDate() != null && !uniqueProjects.contains(s.getProject().getId())) {
            b.append("<a class=\"dashboardresult\" href=\"/miso/project/" + s.getProject().getId()
                + "\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
            b.append("Name: <b>" + s.getProject().getName() + "</b><br/>");
            b.append("Alias: <b>" + s.getProject().getAlias() + "</b><br/>");
            b.append("Last Received: <b>" + LimsUtils.formatDate(s.getReceivedDate()) + "</b><br/>");
            b.append("</div>");

            uniqueProjects.add(s.getProject().getId());
          }
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

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }
}
