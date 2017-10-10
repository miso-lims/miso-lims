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

package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.generateTemporaryName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultProjectService implements ProjectService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultProjectService.class);

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;

  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private SecurityManager securityManager;

  @Override
  public void addProjectWatcher(Project project, User watcher) throws IOException {
    authorizationManager.throwIfNotWritable(project);
    projectStore.addWatcher(project, watcher);
  }

  @Override
  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    ProjectOverview managed = projectStore.getProjectOverviewById(projectOverview.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for ProjectOverview " + projectOverview.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    projectStore.saveOverview(managed);
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    Project project = projectStore.getByAlias(projectAlias);
    authorizationManager.throwIfNotReadable(project);
    return project;
  }

  // GETS
  @Override
  public Project getProjectById(long projectId) throws IOException {
    Project project = projectStore.get(projectId);
    authorizationManager.throwIfNotReadable(project);
    return project;
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return projectStore.getProjectColumnSizes();
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    ProjectOverview projectOverview = projectStore.getProjectOverviewById(overviewId);
    authorizationManager.throwIfNotReadable(projectOverview.getProject());
    return projectOverview;
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    return authorizationManager.filterUnreadable(projectStore.listOverviewsByProjectId(projectId), ProjectOverview::getProject);
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    return authorizationManager.filterUnreadable(projectStore.listAll());
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    return authorizationManager.filterUnreadable(projectStore.listBySearch(query));
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    return authorizationManager.filterUnreadable(projectStore.listAllWithLimit(limit));
  }

  private Collection<Group> loadManagedGroups(Collection<Group> original) throws IOException {
    if (original == null)
      return null;
    List<Group> managed = new ArrayList<>();
    for (Group item : original) {
      managed.add(securityManager.getGroupById(item.getGroupId()));
    }
    return managed;
  }

  // SAVES

  private Collection<User> loadManagedUsers(Collection<User> original) throws IOException {
    if (original == null)
      return null;
    List<User> managed = new ArrayList<>();
    for (User item : original) {
      managed.add(securityManager.getUserById(item.getUserId()));
    }
    return managed;
  }

  @Override
  public void removeProjectWatcher(Project project, User watcher) throws IOException {
    authorizationManager.throwIfNotWritable(project);
    projectStore.removeWatcher(project, watcher);
  }

  private Collection<Group> resolveGroups(Collection<Group> groups) throws IOException {
    List<Group> resolved = Lists.newArrayList();
    if (groups != null) {
      for (Group group : groups) {
        Group g = securityManager.getGroupById(group.getGroupId());
        if (g == null) throw new IllegalArgumentException("Group " + group.getGroupId() + " does not exist");
        resolved.add(g);
      }
    }
    return resolved;
  }

  private long resolveMembers(SecurityProfile sp) throws IOException {
    if (sp == null) throw new NullPointerException("null SecurityProfile");
    sp.setOwner(securityManager.getUserById(sp.getOwner().getUserId()));
    sp.setReadUsers(resolveUsers(sp.getReadUsers()));
    sp.setReadGroups(resolveGroups(sp.getReadGroups()));
    sp.setWriteUsers(resolveUsers(sp.getWriteUsers()));
    sp.setWriteGroups(resolveGroups(sp.getWriteGroups()));
    return securityProfileStore.save(sp);
  }

  private Collection<User> resolveUsers(Collection<User> users) throws IOException {
    List<User> resolved = Lists.newArrayList();
    if (users != null) {
      for (User user : users) {
        User u = securityManager.getUserById(user.getUserId());
        if (u == null) throw new IllegalArgumentException("User " + user.getUserId() + " does not exist");
        resolved.add(u);
      }
    }
    return resolved;
  }

  @Override
  public long saveProject(Project project) throws IOException {
    ValidationResult shortNameValidation = namingScheme.validateProjectShortName(project.getShortName());
    if (!shortNameValidation.isValid()) {
      throw new IOException("Cannot save project - invalid shortName: " + shortNameValidation.getMessage());
    }
    if (project.getId() == ProjectImpl.UNSAVED_ID) {
      resolveMembers(project.getSecurityProfile());
      project.setName(generateTemporaryName());
      projectStore.save(project);
      try {
        project.setName(namingScheme.generateNameFor(project));
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Project - issue with naming scheme", e);
      }
      LimsUtils.validateNameOrThrow(project, namingScheme);
    } else {
      Project original = projectStore.get(project.getId());
      authorizationManager.throwIfNotWritable(original);
      original.setAlias(project.getAlias());
      original.setDescription(project.getDescription());
      original.setIssueKeys(project.getIssueKeys());
      original.setLastUpdated(new Date());
      original.setProgress(project.getProgress());
      original.setReferenceGenome(referenceGenomeDao.getReferenceGenome(project.getReferenceGenome().getId()));
      original.setShortName(project.getShortName());
      for (ProjectOverview po : project.getOverviews()) {
        if (po.getId() == ProjectOverview.UNSAVED_ID) {
          original.getOverviews().add(po);
        }
      }
      updateSecurityProfile(original.getSecurityProfile(), project.getSecurityProfile());
      project = original;
    }
    project.getSecurityProfile().setProfileId(saveSecurityProfile(project.getSecurityProfile()));
    long id = projectStore.save(project);
    return id;
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (overview.getId() != ProjectOverview.UNSAVED_ID) {
      ProjectOverview original = projectStore.getProjectOverviewById(overview.getId());
      authorizationManager.throwIfNotWritable(original.getProject());
      original.setAllLibrariesQcPassed(overview.getAllLibrariesQcPassed());
      original.setAllPoolsConstructed(overview.getAllPoolsConstructed());
      original.setAllRunsCompleted(overview.getAllRunsCompleted());
      original.setLocked(overview.getLocked());
      original.setPrimaryAnalysisCompleted(overview.getPrimaryAnalysisCompleted());
      original.setPrincipalInvestigator(overview.getPrincipalInvestigator());
      original.setStartDate(overview.getStartDate());
      original.setEndDate(overview.getEndDate());
      original.setNumProposedSamples(overview.getNumProposedSamples());
      original.setAllSampleQcPassed(overview.getAllSampleQcPassed());
      overview = original;
    } else {
      authorizationManager.throwIfNotWritable(overview.getProject());
    }
    overview.setLastUpdated(new Date());
    return projectStore.saveOverview(overview);
  }

  @Override
  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    ProjectOverview managed = projectStore.getProjectOverviewById(overview.getId());
    authorizationManager.throwIfNotWritable(managed.getProject());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.getNotes().add(note);
    projectStore.saveOverview(managed);
  }

  private long saveSecurityProfile(SecurityProfile sp) throws IOException {
    return securityProfileStore.save(sp);
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

  public void setReferenceGenomeStore(ReferenceGenomeDao referenceGenomeStore) {
    this.referenceGenomeDao = referenceGenomeStore;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  private void updateSecurityProfile(SecurityProfile target, SecurityProfile source) throws IOException {
    target.setAllowAllInternal(source.isAllowAllInternal());
    target.setOwner(source.getOwner() == null ? null : securityManager.getUserById(source.getOwner().getUserId()));
    target.setReadGroups(loadManagedGroups(source.getReadGroups()));
    target.setWriteGroups(loadManagedGroups(source.getWriteGroups()));
    target.setReadUsers(loadManagedUsers(source.getReadUsers()));
    target.setWriteUsers(loadManagedUsers(source.getWriteUsers()));
  }

}
