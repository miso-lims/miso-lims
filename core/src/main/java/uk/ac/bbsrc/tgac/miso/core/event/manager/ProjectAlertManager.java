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

package uk.ac.bbsrc.tgac.miso.core.event.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.event.listener.MisoListener;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.3
 */
@Service
public class ProjectAlertManager {
  private static final Logger log = LoggerFactory.getLogger(ProjectAlertManager.class);
  final Map<Long, Project> projects = new HashMap<>();

  @Value("${miso.alerting.enabled}")
  private boolean enabled = true;

  @Autowired
  private MisoListener projectListener;
  @Autowired
  private MisoListener projectOverviewListener;
  @Autowired
  private SecurityManager securityManager;

  public MisoListener getProjectListener() {
    return projectListener;
  }

  public void setProjectListener(MisoListener projectListener) {
    this.projectListener = projectListener;
  }

  public MisoListener getProjectOverviewListener() {
    return projectOverviewListener;
  }

  public void setProjectOverviewListener(MisoListener projectOverviewListener) {
    this.projectOverviewListener = projectOverviewListener;
  }

  public void applyListeners(Project project) {
    project.addListener(getProjectListener());
  }

  public void removeListeners(Project project) {
    project.removeListener(getProjectListener());
  }

  public void applyListeners(ProjectOverview overview) {
    overview.addListener(getProjectOverviewListener());
  }

  public void removeListeners(ProjectOverview overview) {
    overview.removeListener(getProjectOverviewListener());
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void push(Project project) {
    if (enabled) {
      if (project != null) {
        Project clone = partialCopy(project);
        if (clone != null) {
          if (projects.containsKey(project.getId())) {
            if (clone.getProgress() != null) {
              log.debug("Not replacing Project " + clone.getId() + ": " + clone.getProgress().name());
            }
          } else {
            applyListeners(clone);

            for (ProjectOverview po : clone.getOverviews()) {
              applyListeners(po);
            }

            projects.put(project.getId(), clone);
            if (clone.getProgress() != null) {
              log.debug("Queued Project " + clone.getId() + ": " + clone.getProgress().name());
            }
          }
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void pop(Project project) {
    if (enabled) {
      if (project != null) {
        Project clone = projects.get(project.getId());
        if (clone != null) {
          removeListeners(clone);
          for (ProjectOverview po : clone.getOverviews()) {
            removeListeners(po);
            po = null;
          }

          clone = null;
          projects.remove(project.getId());
          log.debug("Dequeued " + project.getId());
        }
      }
    } else {
      log.warn("Alerting system disabled.");
    }
  }

  public void update(Project p) throws IOException {
    if (enabled) {
      // don't just replace the object - set required fields otherwise we have to reset all the object's listeners
      Project clone = projects.get(p.getId());
      if (clone == null) {
        log.debug("Update: no clone - pushing");
        // new project - add all ProjectWatchers!
        for (User u : securityManager.listUsersByGroupName("ProjectWatchers")) {
          p.addWatcher(u);
          for (ProjectOverview po : p.getOverviews()) {
            po.addWatcher(u);
          }
        }
        push(p);
      } else {
        log.debug("Update: got clone of " + clone.getId());
        clone.setProgress(p.getProgress());

        for (ProjectOverview po : p.getOverviews()) {
          ProjectOverview cloneOverview = clone.getOverviewById(po.getId());
          if (cloneOverview != null) {
            cloneOverview.setAllSampleQcPassed(po.getAllSampleQcPassed());
            cloneOverview.setLibraryPreparationComplete(po.getLibraryPreparationComplete());
            cloneOverview.setAllLibrariesQcPassed(po.getAllLibrariesQcPassed());
            cloneOverview.setAllPoolsConstructed(po.getAllPoolsConstructed());
            cloneOverview.setAllRunsCompleted(po.getAllRunsCompleted());
            cloneOverview.setPrimaryAnalysisCompleted(po.getPrimaryAnalysisCompleted());
          } else {
            log.debug("Original project has an overview, but it seems it hasn't been cloned.");
          }
        }

        pop(clone);
        push(p);
      }
    }
  }

  public void addWatcher(ProjectOverview overview, User user) throws IOException {
    for (Project clone : projects.values()) {
      ProjectOverview cloneOverview = clone.getOverviewById(overview.getId());
      if (cloneOverview != null) {
        log.debug("Added watcher " + user.getUserId() + " to overview " + overview.getId());
        cloneOverview.addWatcher(user);
        clone.addWatcher(user);
        break;
      }
    }
  }

  public void removeWatcher(ProjectOverview overview, User user) throws IOException {
    for (Project clone : projects.values()) {
      ProjectOverview cloneOverview = clone.getOverviewById(overview.getId());
      if (cloneOverview != null) {
        log.debug("Removed watcher " + user.getUserId() + " from overview " + overview.getId());
        cloneOverview.removeWatcher(user);
        clone.removeWatcher(user);
        break;
      }
    }
  }

  public void addWatcher(Project project, User user) throws IOException {
    if (user != null) {
      Project clone = projects.get(project.getId());
      log.debug("Added watcher " + user.getUserId() + " to project " + project.getId());
      if (clone == null) {
        project.addWatcher(user);
        for (ProjectOverview po : project.getOverviews()) {
          ProjectOverview pOverview = project.getOverviewById(po.getId());
          if (pOverview != null) {
            pOverview.addWatcher(user);
          }
        }

        push(project);
      } else {
        clone.addWatcher(user);
        for (ProjectOverview po : clone.getOverviews()) {
          ProjectOverview cloneOverview = clone.getOverviewById(po.getId());
          if (cloneOverview != null) {
            cloneOverview.addWatcher(user);
          }
        }
      }
    }
  }

  public void removeWatcher(Project project, User user) throws IOException {
    Project clone = projects.get(project.getId());
    if (clone == null) {
      project.removeWatcher(user);
      for (ProjectOverview po : project.getOverviews()) {
        ProjectOverview pOverview = project.getOverviewById(po.getId());
        if (pOverview != null) {
          pOverview.removeWatcher(user);
        }
      }

      push(project);
    } else {
      clone.removeWatcher(user);
      for (ProjectOverview po : clone.getOverviews()) {
        ProjectOverview cloneOverview = clone.getOverviewById(po.getId());
        if (cloneOverview != null) {
          cloneOverview.removeWatcher(user);
        }
      }
    }
  }

  public void updateGroupWatcher(User user) throws IOException {
    for (Project p : projects.values()) {
      if (user.getGroups() != null && user.getGroups().contains(securityManager.getGroupByName("ProjectWatchers"))) {
        addWatcher(p, user);
      } else {
        if (p.getSecurityProfile() != null && p.getSecurityProfile().getOwner() != null
            && !p.getSecurityProfile().getOwner().equals(user)) {
          removeWatcher(p, user);
        }
      }
    }
  }

  /**
   * Creates a minimal copy of the project to be used for change tracking in the alerting system. Only relevant fields are copied
   * 
   * @param project
   *          the project to copy
   * @return the copy
   */
  private Project partialCopy(Project project) {
    Project clone = new ProjectImpl();
    clone.setId(project.getId());
    clone.setAlias(project.getAlias());
    clone.setName(project.getName());
    clone.setProgress(project.getProgress());
    Set<ProjectOverview> projectOverviews = Sets.newHashSet();
    for (ProjectOverview projectOverview : project.getOverviews()) {
      projectOverviews.add(partialCopy(projectOverview));
    }
    clone.setOverviews(projectOverviews);

    for (User u : project.getWatchers()) {
      clone.addWatcher(u);
    }
    return clone;
  }

  /**
   * Creates a minimal copy of the ProjectOverview to be used for change tracking in the alerting system. Only relevant fields are copied
   * 
   * @param projectOverview
   *          The ProjectOverview to copy
   * @return the copy
   */
  private ProjectOverview partialCopy(ProjectOverview projectOverview) {
    ProjectOverview clone = new ProjectOverview();
    clone.setId(projectOverview.getId());
    clone.setProject(projectOverview.getProject());
    clone.setAllSampleQcPassed(projectOverview.getAllSampleQcPassed());
    clone.setLibraryPreparationComplete(projectOverview.getLibraryPreparationComplete());
    clone.setAllLibrariesQcPassed(projectOverview.getAllLibrariesQcPassed());
    clone.setAllPoolsConstructed(projectOverview.getAllPoolsConstructed());
    clone.setAllRunsCompleted(projectOverview.getAllRunsCompleted());
    clone.setPrimaryAnalysisCompleted(projectOverview.getPrimaryAnalysisCompleted());
    return clone;
  }
}
