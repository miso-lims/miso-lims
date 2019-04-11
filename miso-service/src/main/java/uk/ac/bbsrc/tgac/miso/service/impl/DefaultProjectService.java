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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.core.store.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;

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
  private NamingScheme namingScheme;

  // GETS
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
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return ValidationUtils.adjustNameLength(
        ValidationUtils.adjustLength(projectStore.getProjectColumnSizes(), "shortName", namingScheme.projectShortNameLengthAdjustment()),
        namingScheme);
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
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
  public long saveProject(Project project) throws IOException {
    ValidationResult shortNameValidation = namingScheme.validateProjectShortName(project.getShortName());
    if (!shortNameValidation.isValid()) {
      throw new IOException("Cannot save project - invalid shortName: " + shortNameValidation.getMessage());
    }
    if (project.getId() == ProjectImpl.UNSAVED_ID) {
      project.setName(generateTemporaryName());
      projectStore.save(project);
      try {
        project.setName(namingScheme.generateNameFor(project));
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Project - issue with naming scheme", e);
      }
      validateNameOrThrow(project, namingScheme);
    } else {
      Project original = projectStore.get(project.getId());
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
      project = original;
    }
    long id = projectStore.save(project);
    return id;
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

}
