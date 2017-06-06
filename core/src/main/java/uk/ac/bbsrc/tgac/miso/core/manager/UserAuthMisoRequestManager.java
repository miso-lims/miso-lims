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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
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
  public long saveRun(Run run) throws IOException {
    if (writeCheck(run)) {
      return backingManager.saveRun(run);
    } else {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to this Run");
    }
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (writeCheck(container)) {
      container.setLastModifier(getCurrentUser());
      return backingManager.saveSequencerPartitionContainer(container);
    } else {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to this SequencerPartitionContainer");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    return backingManager.saveSubmission(submission);
  }

  // gets
  @Override
  public Partition getPartitionById(long partitionId) throws IOException {
    Partition o = backingManager.getPartitionById(partitionId);
    if (readCheck(o.getSequencerPartitionContainer())) return o;
    else throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read Partition " + partitionId);
  }

  @Override
  public LibraryQC getLibraryQCById(long qcId) throws IOException {
    LibraryQC o = backingManager.getLibraryQCById(qcId);
    if (readCheck(o.getLibrary())) return o;
    else throw new AuthorizationIOException(
        "User " + getCurrentUsername() + " cannot read parent Library " + o.getLibrary().getId() + " for LibraryQC " + qcId);
  }

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainerById(long containerId) throws IOException {
    SequencerPartitionContainer o = backingManager.getSequencerPartitionContainerById(containerId);
    if (readCheck(o))
      return o;
    else
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read SequencerPartitionContainer " + containerId);
  }

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
  public Run getRunByAlias(String alias) throws IOException {
    Run o = backingManager.getRunByAlias(alias);
    if (readCheck(o)) return o;
    else throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot read Run " + o.getId());
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    RunQC o = backingManager.getRunQCById(runQcId);
    if (readCheck(o.getRun())) return o;
    else throw new AuthorizationIOException(
        "User " + getCurrentUsername() + " cannot read parent Run " + o.getRun().getId() + " for RunQC " + runQcId);
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    SampleQC o = backingManager.getSampleQCById(sampleQcId);
    if (readCheck(o.getSample())) return o;
    else throw new AuthorizationIOException(
        "User " + getCurrentUsername() + " cannot read parent Sample " + o.getSample().getId() + " for SampleQC " + sampleQcId);
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
  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException {
    User user = getCurrentUser();
    Collection<RunQC> accessibles = new HashSet<>();
    for (RunQC runQC : backingManager.listAllRunQCsByRunId(runId)) {
      if (runQC.userCanRead(user)) {
        accessibles.add(runQC);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer> accessibles = new HashSet<>();
    for (SequencerPartitionContainer container : backingManager.listSequencerPartitionContainersByRunId(runId)) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer> accessibles = new HashSet<>();
    for (SequencerPartitionContainer container : backingManager.listSequencerPartitionContainersByBarcode(barcode)) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException {
    User user = getCurrentUser();
    Collection<SampleQC> accessibles = new HashSet<>();
    for (SampleQC sampleQc : backingManager.listAllSampleQCsBySampleId(sampleId)) {
      if (sampleQc.userCanRead(user)) {
        accessibles.add(sampleQc);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryQC> accessibles = new HashSet<>();
    for (LibraryQC libraryQc : backingManager.listAllLibraryQCsByLibraryId(libraryId)) {
      if (libraryQc.userCanRead(user)) {
        accessibles.add(libraryQc);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer> listAllSequencerPartitionContainers() throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer> accessibles = new HashSet<>();
    for (SequencerPartitionContainer container : backingManager.listAllSequencerPartitionContainers()) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
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
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteLibraryQC(libraryQc);
    }
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteSequencerReference(sequencerReference);
    }
  }

  @Override
  public void deleteSequencerServiceRecord(uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord serviceRecord) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteSequencerServiceRecord(serviceRecord);
    }
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteContainer(container);
    }
  }

  @Override
  public void deleteRunNote(Run run, Long noteId) throws IOException {
    if (getCurrentUser().isAdmin()) { // should use authorizationManager.throwIfNonAdminOrMatchingOwner(note.getOwner())
      backingManager.deleteRunNote(run, noteId);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot delete this note");
    }
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
  public void saveRuns(Collection<Run> runs) throws IOException {
    User user = getCurrentUser();
    for (Run run : runs) {
      if (!writeCheck(run)) {
        throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Run");
      } else {
        run.setLastModifier(user);
        List<SequencerPartitionContainer> containers = run.getSequencerPartitionContainers();
        if (run.getSequencerPartitionContainers() != null) {
          for (SequencerPartitionContainer container : containers) {
            container.setLastModifier(user);
          }
        }
      }
    }
    backingManager.saveRuns(runs);
  }

  @Override
  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (getCurrentUser().isAdmin()) {
      return backingManager.saveSequencerReference(sequencerReference);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this SequencerReference");
    }
  }

  @Override
  public long saveBox(Box box) throws IOException {
    if (writeCheck(box)) {
      return backingManager.saveBox(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Box");
    }
  }

  @Override
  public Platform getPlatformById(long platformId) throws IOException {
    return backingManager.getPlatformById(platformId);
  }

  @Override
  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException {
    return backingManager.getSequencerReferenceById(referenceId);
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    return backingManager.getSequencerReferenceByName(referenceName);
  }

  @Override
  public SequencerReference getSequencerReferenceByUpgradedReferenceId(long upgradedReferenceId) throws IOException {
    return backingManager.getSequencerReferenceByUpgradedReferenceId(upgradedReferenceId);
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getSampleQcTypeById(qcTypeId);
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    return backingManager.getSampleQcTypeByName(qcName);
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getLibraryQcTypeById(qcTypeId);
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    return backingManager.getLibraryQcTypeByName(qcName);
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getRunQcTypeById(qcTypeId);
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    return backingManager.getRunQcTypeByName(qcName);
  }

  @Override
  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getPoolQcTypeById(qcTypeId);
  }

  @Override
  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    return backingManager.getPoolQcTypeByName(qcName);
  }

  @Override
  public Box getBoxById(long boxId) throws IOException {
    Box o = backingManager.getBoxById(boxId);
    if (readCheck(o)) return o;
    else throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + boxId);
  }

  @Override
  public Box getBoxByBarcode(String barcode) throws IOException {
    Box o = backingManager.getBoxByBarcode(barcode);
    if (readCheck(o)) return o;
    else throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + barcode);
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    Box o = backingManager.getBoxByAlias(alias);
    if (readCheck(o)) return o;
    else throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + alias);
  }

  @Override
  public Collection<Box> listAllBoxes() throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxes()) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Box> listAllBoxesWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxesWithLimit(limit)) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Platform> listAllPlatforms() throws IOException {
    return backingManager.listAllPlatforms();
  }

  @Override
  public Collection<PlatformType> listActivePlatformTypes() throws IOException {
    return backingManager.listActivePlatformTypes();
  }

  @Override
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException {
    return backingManager.listPlatformsOfType(platformType);
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    return backingManager.listDistinctPlatformNames();
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    return backingManager.listAllBoxUses();
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    return backingManager.listAllBoxSizes();
  }

  @Override
  public Collection<BoxableView> getBoxableViewsFromBarcodeList(Collection<String> barcodeList) throws IOException {
    return backingManager.getBoxableViewsFromBarcodeList(barcodeList);
  }

  @Override
  public BoxableView getBoxableViewByBarcode(String barcode) throws IOException {
    return backingManager.getBoxableViewByBarcode(barcode);
  }

  @Override
  public Collection<SequencerReference> listAllSequencerReferences() throws IOException {
    return backingManager.listAllSequencerReferences();
  }

  @Override
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException {
    return backingManager.listSequencerReferencesByPlatformType(platformType);
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    return backingManager.listAllLibraryQcTypes();
  }

  @Override
  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    return backingManager.listAllPoolQcTypes();
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    return backingManager.listAllRunQcTypes();
  }

  @Override
  public void discardSingleTube(Box box, String position) throws IOException {
    if (writeCheck(box)) {
      backingManager.discardSingleTube(box, position);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }

  }

  @Override
  public void discardAllTubes(Box box) throws IOException {
    if (writeCheck(box)) {
      backingManager.discardAllTubes(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }
  }

  @Override
  public void deleteBox(Box box) throws IOException {
    if (writeCheck(box)) {
      backingManager.deleteBox(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }
  }

  @Override
  public long saveSequencerServiceRecord(SequencerServiceRecord record) throws IOException {
    return backingManager.saveSequencerServiceRecord(record);
  }

  @Override
  public SequencerServiceRecord getSequencerServiceRecordById(long id) throws IOException {
    return backingManager.getSequencerServiceRecordById(id);
  }

  @Override
  public Collection<SequencerServiceRecord> listAllSequencerServiceRecords() throws IOException {
    return backingManager.listAllSequencerServiceRecords();
  }

  @Override
  public Collection<SequencerServiceRecord> listSequencerServiceRecordsBySequencerId(long referenceId) throws IOException {
    return backingManager.listSequencerServiceRecordsBySequencerId(referenceId);
  }

  @Override
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException {
    return backingManager.getServiceRecordColumnSizes();
  }

  @Override
  public Map<String, Integer> getBoxColumnSizes() throws IOException {
    return backingManager.getBoxColumnSizes();
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return backingManager.getProjectColumnSizes();
  }

  @Override
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException {
    return backingManager.getSequencerReferenceColumnSizes();
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
  public Collection<LibraryDesign> listLibraryDesignByClass(SampleClass sampleClass) throws IOException {
    if (sampleClass == null) return Collections.emptyList();
    return backingManager.listLibraryDesignByClass(sampleClass);
  }

  @Override
  public Collection<LibraryDesignCode> listLibraryDesignCodes() throws IOException {
    return backingManager.listLibraryDesignCodes();
  }

  @Override
  public Collection<TargetedSequencing> listAllTargetedSequencing() throws IOException {
    return backingManager.listAllTargetedSequencing();
  }

  @Override
  public TargetedSequencing getTargetedSequencingById(long targetedSequencingId) throws IOException {
    return backingManager.getTargetedSequencingById(targetedSequencingId);
  }

  @Override
  public Long countContainers() throws IOException {
    return backingManager.countContainers();
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

  @Override
  public Collection<LibraryDesign> listLibraryDesigns() throws IOException {
    return backingManager.listLibraryDesigns();
  }

  @Override
  public void updateContainer(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException {
    if (!writeCheck(managed)) {
      throw new AuthorizationIOException("User " + getCurrentUsername() + " cannot write to Container " + managed.getId());
    } else {
      backingManager.updateContainer(source, managed);
    }
  }
}
