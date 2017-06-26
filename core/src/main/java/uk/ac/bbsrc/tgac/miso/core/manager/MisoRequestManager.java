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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.ReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerPartitionContainerStore;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.core.store.SubmissionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Implementation of a RequestManager to facilitate persistence operations on MISO model objects
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoRequestManager implements RequestManager {
  protected static final Logger log = LoggerFactory.getLogger(MisoRequestManager.class);

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Autowired
  private PlatformStore platformStore;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private ReferenceGenomeDao referenceGenomeDao;
  @Autowired
  private RunStore runStore;
  @Autowired
  private SequencerPartitionContainerStore sequencerPartitionContainerStore;
  @Autowired
  private SequencerReferenceStore sequencerReferenceStore;
  @Autowired
  private SubmissionStore submissionStore;
  @Autowired
  private ChangeLogStore changeLogStore;
  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setPlatformStore(PlatformStore platformStore) {
    this.platformStore = platformStore;
  }

  public void setPoolStore(PoolStore poolStore) {
    this.poolStore = poolStore;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setReferenceGenomeStore(ReferenceGenomeDao referenceGenomeStore) {
    this.referenceGenomeDao = referenceGenomeStore;
  }

  public void setRunStore(RunStore runStore) {
    this.runStore = runStore;
  }

  public void setSequencerPartitionContainerStore(SequencerPartitionContainerStore sequencerPartitionContainerStore) {
    this.sequencerPartitionContainerStore = sequencerPartitionContainerStore;
  }

  public void setSequencerReferenceStore(SequencerReferenceStore sequencerReferenceStore) {
    this.sequencerReferenceStore = sequencerReferenceStore;
  }

  public void setSubmissionStore(SubmissionStore submissionStore) {
    this.submissionStore = submissionStore;
  }

  public void setAutoGenerateIdBarcodes(boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    if (projectStore != null) {
      return projectStore.listAll();
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    if (projectStore != null) {
      return projectStore.listAllWithLimit(limit);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    if (projectStore != null) {
      return projectStore.listBySearch(query);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.listOverviewsByProjectId(projectId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listSequencerPartitionContainersByBarcode(barcode);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    if (submissionStore != null) {
      return submissionStore.listAll();
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
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
    managed.getNotes().remove(deleteNote);
    projectStore.saveOverview(managed);
  }

  // SAVES

  @Override
  public long saveProject(Project project) throws IOException {
    if (projectStore != null) {
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
        original.setAlias(project.getAlias());
        original.setDescription(project.getDescription());
        original.setIssueKeys(project.getIssueKeys());
        original.setLastUpdated(project.getLastUpdated());
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
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  private void updateSecurityProfile(SecurityProfile target, SecurityProfile source) throws IOException {
    target.setAllowAllInternal(source.isAllowAllInternal());
    target.setOwner(source.getOwner() == null ? null : securityStore.getUserById(source.getOwner().getUserId()));
    target.setReadGroups(loadManagedGroups(source.getReadGroups()));
    target.setWriteGroups(loadManagedGroups(source.getWriteGroups()));
    target.setReadUsers(loadManagedUsers(source.getReadUsers()));
    target.setWriteUsers(loadManagedUsers(source.getWriteUsers()));
  }

  private Collection<Group> loadManagedGroups(Collection<Group> original) throws IOException {
    if (original == null)
      return null;
    List<Group> managed = new ArrayList<>();
    for (Group item : original) {
      managed.add(securityStore.getGroupById(item.getGroupId()));
    }
    return managed;
  }

  private Collection<User> loadManagedUsers(Collection<User> original) throws IOException {
    if (original == null)
      return null;
    List<User> managed = new ArrayList<>();
    for (User item : original) {
      managed.add(securityStore.getUserById(item.getUserId()));
    }
    return managed;
  }

  private long saveSecurityProfile(SecurityProfile sp) throws IOException {
    return securityProfileStore.save(sp);
  }

  private long resolveMembers(SecurityProfile sp) throws IOException {
    if (sp == null) throw new NullPointerException("null SecurityProfile");
    sp.setOwner(securityStore.getUserById(sp.getOwner().getUserId()));
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
        User u = securityStore.getUserById(user.getUserId());
        if (u == null) throw new IllegalArgumentException("User " + user.getUserId() + " does not exist");
        resolved.add(u);
      }
    }
    return resolved;
  }

  private Collection<Group> resolveGroups(Collection<Group> groups) throws IOException {
    List<Group> resolved = Lists.newArrayList();
    if (groups != null) {
      for (Group group : groups) {
        Group g = securityStore.getGroupById(group.getGroupId());
        if (g == null) throw new IllegalArgumentException("Group " + group.getGroupId() + " does not exist");
        resolved.add(g);
      }
    }
    return resolved;
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (projectStore != null) {
      if (overview.getId() != ProjectOverview.UNSAVED_ID) {
        ProjectOverview original = projectStore.getProjectOverviewById(overview.getId());
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
      }
      overview.setLastUpdated(new Date());
      return projectStore.saveOverview(overview);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    ProjectOverview managed = projectStore.getProjectOverviewById(overview.getId());
    note.setCreationDate(new Date());
    note.setOwner(getCurrentUser());
    managed.getNotes().add(note);
    projectStore.saveOverview(managed);
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (runStore != null) {
      run.setLastModifier(getCurrentUser());
      if (run.getId() == Run.UNSAVED_ID) {

        run.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(run.getSecurityProfile())));
        run.setLastModifier(getCurrentUser());

        run.setName(generateTemporaryName());

        if (!run.getSequencerPartitionContainers().isEmpty()) {
          List<SequencerPartitionContainer> containersToSave = new ArrayList<>();
          for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
            SequencerPartitionContainer managedContainer = getSequencerPartitionContainerById(container.getId());
            if (managedContainer == null) {
              containersToSave.add(container);
            } else {
              updateContainer(container, managedContainer);
              containersToSave.add(managedContainer);
            }
          }
          run.setSequencerPartitionContainers(containersToSave);
        }
        run.setId(runStore.save(run));
        try {
          String name = namingScheme.generateNameFor(run);
          run.setName(name);

          validateNameOrThrow(run, namingScheme);
          return runStore.save(run);
        } catch (MisoNamingException e) {
          throw new IOException("Cannot save Run - issue with generating name");
        }
      } else {
        Run managed = runStore.get(run.getId());
        log.info("update run: " + managed);
        managed.setLastModifier(getCurrentUser());
        managed.setAlias(run.getAlias());
        managed.setDescription(run.getDescription());
        managed.setFilePath(run.getFilePath());
        managed.setHealth(run.getHealth());
        managed.setStartDate(run.getStartDate());
        managed.setCompletionDate(run.getCompletionDate());
        for (RunQC runQc : run.getRunQCs()) {
          if (!managed.getRunQCs().contains(runQc)) {
            managed.addQc(runQc);
          }
        }
        Set<String> originalContainers = Barcodable.extractLabels(managed.getSequencerPartitionContainers());
        List<SequencerPartitionContainer> saveContainers = new ArrayList<>();
        for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
          SequencerPartitionContainer managedContainer = getSequencerPartitionContainerById(container.getId());
          updateContainer(container, managedContainer);
          saveContainers.add(managedContainer);
        }
        Set<String> updatedContainers = Barcodable.extractLabels(saveContainers);
        managed.setSequencerPartitionContainers(saveContainers);
        managed.setNotes(run.getNotes());
        managed.setSequencingParameters(run.getSequencingParameters());
        if (managed instanceof IlluminaRun) {
          IlluminaRun managedIllumina = (IlluminaRun) managed;
          IlluminaRun runIllumina = (IlluminaRun) run;
          managedIllumina.setCallCycle(runIllumina.getCallCycle());
          managedIllumina.setImgCycle(runIllumina.getImgCycle());
          managedIllumina.setNumCycles(runIllumina.getNumCycles());
          managedIllumina.setScoreCycle(runIllumina.getScoreCycle());
          managedIllumina.setPairedEnd(runIllumina.getPairedEnd());
        } else if (isPacBioRun(managed)) {
          PacBioRun managedPacBio = (PacBioRun) managed;
          PacBioRun runPacBio = (PacBioRun) run;
          managedPacBio.setMovieDuration(runPacBio.getMovieDuration());
        } else if (isLS454Run(managed)) {
          LS454Run managedLS454 = (LS454Run) managed;
          LS454Run runLS454 = (LS454Run) run;
          managedLS454.setCycles(runLS454.getCycles());
          managedLS454.setPairedEnd(runLS454.getPairedEnd());
        } else if (isSolidRun(managed)) {
          SolidRun managedSolid = (SolidRun) managed;
          SolidRun runSolid = (SolidRun) run;
          managedSolid.setPairedEnd(runSolid.getPairedEnd());
        }

        Set<String> added = new TreeSet<>(updatedContainers);
        added.removeAll(originalContainers);
        Set<String> removed = new TreeSet<>(originalContainers);
        removed.removeAll(updatedContainers);
        if (!added.isEmpty() || !removed.isEmpty()) {
          StringBuilder message = new StringBuilder();
          message.append("Containers");
          LimsUtils.appendSet(message, added, "added");
          LimsUtils.appendSet(message, removed, "removed");

          RunChangeLog changeLog = new RunChangeLog();
          changeLog.setRun(managed);
          changeLog.setColumnsChanged("containers");
          changeLog.setSummary(message.toString());
          changeLog.setTime(new Date());
          changeLog.setUser(managed.getLastModifier());
          changeLogStore.create(changeLog);
        }
        runStore.save(managed);
        return run.getId();
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void saveRuns(Collection<Run> runs) throws IOException {
    if (runStore != null) {
      for (Run run : runs) {
        saveRun(run);
      }
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  public User getCurrentUser() throws IOException {
    Authentication auth = SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication();
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

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      if (container.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
        container.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(container.getSecurityProfile())));
        container.setPlatform(platformStore.get(container.getPlatform().getId()));
        container.setLastModifier(securityStore.getUserById(getCurrentUser().getUserId()));
        return sequencerPartitionContainerStore.save(container);
      } else {
        SequencerPartitionContainer managed = getSequencerPartitionContainerById(container.getId());
        updateContainer(container, managed);
        managed.setLastModifier(securityStore.getUserById(getCurrentUser().getUserId()));
        return sequencerPartitionContainerStore.save(managed);
      }
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  private void updateContainer(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException {
    managed.setIdentificationBarcode(source.getIdentificationBarcode());
    managed.setLocationBarcode(source.getLocationBarcode());
    managed.setValidationBarcode(source.getValidationBarcode());

    for (Partition sourcePartition : source.getPartitions()) {
      for (Partition managedPartition : managed.getPartitions()) {
        if (sourcePartition == null || managedPartition == null) {
          throw new IOException("Partition from " + (sourcePartition == null ? "client" : "database") + " is null.");
        }
        if (sourcePartition.getId() == managedPartition.getId()) {
          Pool sourcePool = sourcePartition.getPool();
          Pool managedPool = managedPartition.getPool();
          if (sourcePool == null && managedPool == null) continue;
          if (sourcePool == null && managedPool != null) {
            managedPartition.setPool(null);
          } else if (sourcePool != null && managedPool == null) {
            managedPartition.setPool(poolStore.get(sourcePool.getId()));
          } else if (sourcePool.getId() != managedPool.getId()) {
            managedPartition.setPool(poolStore.get(sourcePool.getId()));
          }
          break;
        }
      }
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    if (submissionStore != null) {
      return submissionStore.save(submission);
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  // GETS
  @Override
  public Project getProjectById(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.get(projectId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    if (projectStore != null) {
      return projectStore.getByAlias(projectAlias);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    if (projectStore != null) {
      return projectStore.getProjectOverviewById(overviewId);
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    if (runStore != null) {
      return runStore.getByAlias(alias);
    } else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainerById(long containerId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.get(containerId);
    } else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    if (submissionStore != null) {
      return submissionStore.get(submissionId);
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByName(referenceName);
    } else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  public ChangeLogStore getChangeLogStore() {
    return changeLogStore;
  }

  public void setChangeLogStore(ChangeLogStore changeLogStore) {
    this.changeLogStore = changeLogStore;
  }

  @Override
  public Collection<PlatformType> listActivePlatformTypes() throws IOException {
    Collection<PlatformType> activePlatformTypes = Lists.newArrayList();
    for (PlatformType platformType : PlatformType.values()) {
      for (SequencerReference sequencer : sequencerReferenceStore.listByPlatformType(platformType)) {
        if (sequencer.isActive()) {
          activePlatformTypes.add(platformType);
          break;
        }
      }
    }
    return activePlatformTypes;
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    if (projectStore != null) {
      return projectStore.getProjectColumnSizes();
    } else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getSubmissionColumnSizes() throws IOException {
    if (submissionStore != null) {
      return submissionStore.getSubmissionColumnSizes();
    } else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getUserColumnSizes() throws IOException {
    if (securityStore != null) {
      return securityStore.getUserColumnSizes();
    } else {
      throw new IOException("No securityStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Map<String, Integer> getGroupColumnSizes() throws IOException {
    if (securityStore != null) {
      return securityStore.getGroupColumnSizes();
    } else {
      throw new IOException("No securityStore available. Check that it has been declared in the Spring config.");
    }
  }

  public static void validateNameOrThrow(Nameable object, NamingScheme namingScheme) throws IOException {
    ValidationResult val = namingScheme.validateName(object.getName());
    if (!val.isValid()) throw new IOException("Save failed - invalid name:" + val.getMessage());
  }

  @Override
  public void addProjectWatcher(Project project, User watcher) throws IOException {
    projectStore.addWatcher(project, watcher);
  }

  @Override
  public void removeProjectWatcher(Project project, User watcher) throws IOException {
    projectStore.removeWatcher(project, watcher);
  }

  public void autoGenerateIdBarcode(Pool pool) {
    String barcode = pool.getName() + "::" + pool.getAlias();
    pool.setIdentificationBarcode(barcode);
  }

}
