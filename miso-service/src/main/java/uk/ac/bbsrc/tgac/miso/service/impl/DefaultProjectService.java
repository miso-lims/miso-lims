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
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateNameOrThrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultProjectService implements ProjectService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultProjectService.class);

  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;
  @Autowired
  private TargetedSequencingStore targetedSequencingStore;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private FileAttachmentService fileAttachmentService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @Override
  public Project get(long projectId) throws IOException {
    return projectStore.get(projectId);
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    return projectStore.getByAlias(projectAlias);
  }

  @Override
  public Project getProjectByShortName(String projectShortName) throws IOException {
    return projectStore.getByShortName(projectShortName);
  }

  @Override
  public List<Project> list() throws IOException {
    return projectStore.listAll();
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    return projectStore.listBySearch(query);
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    return projectStore.listAllWithLimit(limit);
  }

  @Override
  public Collection<Project> listAllProjectsByShortname() throws IOException {
    List<Project> sortedProjects = projectStore.listAll();

    /**
     * Uses String.compareTo to alphabetically sort Projects by shortname
     */
    sortedProjects.sort((Project p1, Project p2) -> p1.getShortName().compareTo(p2.getShortName()));

    return sortedProjects;
  }

  @Override
  public long create(Project project) throws IOException {
    loadChildEntities(project);
    validateChange(project, null);
    project.setChangeDetails(authorizationManager.getCurrentUser());
    project.setName(generateTemporaryName());
    projectStore.save(project);
    NamingScheme namingScheme = namingSchemeHolder.get(project.isSecondaryNaming());
    try {
      project.setName(namingScheme.generateNameFor(project));
    } catch (MisoNamingException e) {
      throw new ValidationException(new ValidationError("name", e.getMessage()));
    }
    validateNameOrThrow(project, namingScheme);
    return projectStore.save(project);
  }

  @Override
  public long update(Project project) throws IOException {
    Project original = projectStore.get(project.getId());
    loadChildEntities(project);
    validateChange(project, original);
    applyChanges(original, project);
    project = original;
    project.setChangeDetails(authorizationManager.getCurrentUser());
    return projectStore.save(project);
  }

  private void validateChange(Project project, Project beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    NamingScheme namingScheme = namingSchemeHolder.get(project.isSecondaryNaming());
    if (ValidationUtils.isSetAndChanged(Project::getShortName, project, beforeChange)) {
      // assume that if project shortname is required, it is used for generating sample aliases
      if (beforeChange != null && !namingScheme.nullProjectShortNameAllowed() && !beforeChange.getSamples().isEmpty()) {
        errors.add(new ValidationError("shortName", "Cannot change because there are already samples in the project"));
      }
    }
    ValidationResult shortNameValidation = namingScheme.validateProjectShortName(project.getShortName());
    if (!shortNameValidation.isValid()) {
      errors.add(new ValidationError("shortName", shortNameValidation.getMessage()));
    }
    if ((beforeChange == null || (project.getShortName() != null && !project.getShortName().equals(beforeChange.getShortName())))
        && (project.getShortName() != null && getProjectByShortName(project.getShortName()) != null)) {
      errors.add(new ValidationError("shortName", "There is already a project with this short name"));
    }
    if ((beforeChange == null || !project.getAlias().equals(beforeChange.getAlias())) && getProjectByAlias(project.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a project with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void loadChildEntities(Project project) throws IOException {
    project.setReferenceGenome(referenceGenomeDao.get(project.getReferenceGenome().getId()));
    if (project.getDefaultTargetedSequencing() != null) {
      project.setDefaultTargetedSequencing(targetedSequencingStore.get(project.getDefaultTargetedSequencing().getId()));
    }
  }

  private void applyChanges(Project original, Project project) {
    original.setAlias(project.getAlias());
    original.setDescription(project.getDescription());
    original.setStatus(project.getStatus());
    original.setReferenceGenome(project.getReferenceGenome());
    original.setDefaultTargetedSequencing(project.getDefaultTargetedSequencing());
    original.setShortName(project.getShortName());
    original.setClinical(project.isClinical());
    original.setRebNumber(project.getRebNumber());
    original.setRebExpiry(project.getRebExpiry());
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setReferenceGenomeDao(ReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public void authorizeDeletion(Project object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult validateDeletion(Project object) throws IOException {
    uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult result = new uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult();

    long samples = sampleService.count(PaginationFilter.project(object.getId()));
    if (samples > 0L) {
      result.addError(new ValidationError(String.format("Project %s contains %d samples",
          object.getShortName() == null ? object.getAlias() : object.getShortName(), samples)));
    }

    return result;
  }

  @Override
  public void beforeDelete(Project object) throws IOException {
    fileAttachmentService.beforeDelete(object);

    List<LibraryTemplate> templates = libraryTemplateService.listLibraryTemplatesForProject(object.getId());
    for (LibraryTemplate template : templates) {
      template.getProjects().removeIf(templateProject -> templateProject.getId() == object.getId());
      libraryTemplateService.update(template);
    }
    SampleNumberPerProject sampleNumberPerProject = sampleNumberPerProjectService.getByProject(object);
    if (sampleNumberPerProject != null) {
      sampleNumberPerProjectService.delete(sampleNumberPerProject);
    }
  }

  @Override
  public void afterDelete(Project object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

}
