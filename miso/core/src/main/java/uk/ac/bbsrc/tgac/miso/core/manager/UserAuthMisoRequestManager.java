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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 22-Aug-2011
 * @since 0.1.0
 */
public class UserAuthMisoRequestManager implements RequestManager {
  protected static final Logger log = LoggerFactory.getLogger(UserAuthMisoRequestManager.class);

  private SecurityContextHolderStrategy securityContextHolderStrategy;
  private SecurityManager securityManager;
  @Autowired
  private RequestManager backingManager;

  public RequestManager getBackingManager() {
    return backingManager;
  }

  public void setBackingManager(RequestManager backingManager) {
    this.backingManager = backingManager;
  }

  public UserAuthMisoRequestManager() {
  }

  public UserAuthMisoRequestManager(SecurityContextHolderStrategy securityContextHolderStrategy, SecurityManager securityManager) {
    this.securityContextHolderStrategy = securityContextHolderStrategy;
    this.securityManager = securityManager;
  }

  private User getCurrentUser() throws IOException {
    Authentication auth = securityContextHolderStrategy.getContext().getAuthentication();
    if (auth == null) {
      return null;
    }
    User user = securityManager.getUserByLoginName(auth.getName());
    if (user == null && auth.isAuthenticated()) {
      user = new UserImpl();
      user.setAdmin(true);
      user.setActive(true);
    }
    return user;
  }

  /**
   * @return the current user's full name, or "Unknown" if the current user cannot be determined
   */
  private String getCurrentUsername() {
    User user = null;
    try {
      user = getCurrentUser();
    } catch (IOException e) {
      user = null;
    }
    if (user == null) {
      return "Unknown";
    } else {
      return user.getFullName();
    }
  }

  private boolean readCheck(SecurableByProfile s) throws IOException {
    if (s != null) {
      try {
        return s.userCanRead(getCurrentUser());
      } catch (IOException e) {
        log.error("Cannot resolve a currently logged in user", e);
      }
    } else {
      return true;
    }
    return false;
  }

  private boolean writeCheck(SecurableByProfile s) throws IOException {
    if (s != null) {
      try {
        return s.userCanWrite(getCurrentUser());
      } catch (IOException e) {
        log.error("cannot resolve a currently logged in user", e);
      }
    } else {
      throw new IOException("Cannot check write permissions for null object. Does this object really exist?");
    }
    return false;
  }

  @Override
  public long saveProject(Project project) throws IOException {
    if (writeCheck(project)) {
      return backingManager.saveProject(project);
    } else {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to this Project");
    }
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (writeCheck(overview.getProject())) {
      return backingManager.saveProjectOverview(overview);
    } else {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to the parent Project");
    }
  }

  @Override
  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    if (writeCheck(overview.getProject())) {
      note.setOwner(getCurrentUser());
      backingManager.saveProjectOverviewNote(overview, note);
    } else {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to the parent Project");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    return backingManager.saveSubmission(submission);
  }

  // gets

  @Override
  public Project getProjectById(long projectId) throws IOException {
    Project o = backingManager.getProjectById(projectId);
    if (readCheck(o)) return o;
    else throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read Project " + projectId);
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    Project o = backingManager.getProjectByAlias(projectAlias);
    if (readCheck(o)) return o;
    else throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read Project " + projectAlias);
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    ProjectOverview o = backingManager.getProjectOverviewById(overviewId);
    if (readCheck(o.getProject())) return o;
    else throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read parent Project " + o.getProject().getId()
        + " for ProjectOverview " + overviewId);
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    return backingManager.getSubmissionById(submissionId);
  }

  /* lists */

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjects()) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjectsWithLimit(limit)) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjectsBySearch(query)) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<ProjectOverview> accessibles = new HashSet<>();
    for (ProjectOverview projectOverview : backingManager.listAllOverviewsByProjectId(projectId)) {
      if (projectOverview.getProject().userCanRead(user)) {
        accessibles.add(projectOverview);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    Collection<Submission> accessibles = new HashSet<>();
    for (Submission submission : backingManager.listAllSubmissions()) {
      accessibles.add(submission);
    }
    return accessibles;
  }

  @Override
  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException {
    if (getCurrentUser().isAdmin()) { // should use authorizationManager.throwIfNonAdminOrMatchingOwner(note.getOwner())
      backingManager.deleteProjectOverviewNote(projectOverview, noteId);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot delete this note");
    }
  }

  @Override
  public Collection<PlatformType> listActivePlatformTypes() throws IOException {
    return backingManager.listActivePlatformTypes();
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return backingManager.getProjectColumnSizes();
  }

  @Override
  public Map<String, Integer> getSubmissionColumnSizes() throws IOException {
    return backingManager.getSubmissionColumnSizes();
  }

  @Override
  public Map<String, Integer> getUserColumnSizes() throws IOException {
    return backingManager.getUserColumnSizes();
  }

  @Override
  public Map<String, Integer> getGroupColumnSizes() throws IOException {
    return backingManager.getGroupColumnSizes();
  }

  @Override
  public void addProjectWatcher(Project project, User watcher) throws IOException {
    if (!readCheck(project)) {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read Project " + project.getId());
    } else if (!project.userCanRead(watcher)) {
      throw new AuthorizationIOException("User " + watcher.getLoginName() + " cannot read Project " + project.getId());
    } else {
      backingManager.addProjectWatcher(project, watcher);
    }
  }

  @Override
  public void removeProjectWatcher(Project project, User watcher) throws IOException {
    if (!writeCheck(project)) {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read write to Project " + project.getId());
    } else {
      backingManager.removeProjectWatcher(project, watcher);
    }
  }
}
