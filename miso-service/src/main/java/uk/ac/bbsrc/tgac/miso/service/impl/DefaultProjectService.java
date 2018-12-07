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
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
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
  private TargetedSequencingStore targetedSequencingStore;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private SecurityManager securityManager;

  // GETS
  @Override
  public Project get(long projectId) throws IOException {
    Project project = projectStore.get(projectId);
    authorizationManager.throwIfNotReadable(project);
    return project;
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    Project project = projectStore.getByAlias(projectAlias);
    authorizationManager.throwIfNotReadable(project);
    return project;
  }

  @Override
  public Project getProjectByShortName(String projectShortName) throws IOException {
    Project project = projectStore.getByShortName(projectShortName);
    authorizationManager.throwIfNotReadable(project);
    return project;
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return ValidationUtils.adjustNameLength(
        ValidationUtils.adjustLength(projectStore.getProjectColumnSizes(), "shortName", namingScheme.projectShortNameLengthAdjustment()),
        namingScheme);
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

  @Override
  public Collection<Project> listAllProjectsByShortname() throws IOException {
    List<Project> sortedProjects = (ArrayList<Project>) projectStore.listAll();

    /**
     * Uses String.compareTo to alphabetically sort Projects by shortname
     */
    sortedProjects.sort((Project p1, Project p2) -> p1.getShortName().compareTo(p2.getShortName()));

    return authorizationManager.filterUnreadable(sortedProjects);
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
      original.setLastUpdated(new Date());
      original.setProgress(project.getProgress());
      original.setReferenceGenome(referenceGenomeDao.getReferenceGenome(project.getReferenceGenome().getId()));
      if (project.getDefaultTargetedSequencing() != null) {
        original.setDefaultTargetedSequencing(targetedSequencingStore.get(project.getDefaultTargetedSequencing().getId()));
      } else {
        original.setDefaultTargetedSequencing(null);
      }
      original.setShortName(project.getShortName());
      updateSecurityProfile(original.getSecurityProfile(), project.getSecurityProfile());
      project = original;
    }
    project.getSecurityProfile().setProfileId(saveSecurityProfile(project.getSecurityProfile()));
    long id = projectStore.save(project);
    return id;
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
