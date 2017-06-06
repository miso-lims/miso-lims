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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

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
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface RequestManager {

  // SAVES
  public long saveProject(Project project) throws IOException;

  public long saveProjectOverview(ProjectOverview overview) throws IOException;

  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.saveRuns() instead */
  public long saveRun(Run run) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.saveRuns() instead */
  public void saveRuns(Collection<Run> runs) throws IOException;

  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException;

  public long saveSubmission(Submission submission) throws IOException;

  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException;

  public long saveSequencerServiceRecord(SequencerServiceRecord record) throws IOException;

  public long saveBox(Box box) throws IOException;

  // GETS
  public Partition getPartitionById(long partitionId) throws IOException;

  public SequencerPartitionContainer getSequencerPartitionContainerById(long containerId) throws IOException;

  public LibraryQC getLibraryQCById(long qcId) throws IOException;

  public Platform getPlatformById(long platformId) throws IOException;

  public Project getProjectById(long projectId) throws IOException;

  public Project getProjectByAlias(String projectAlias) throws IOException;

  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.getByAlias() instead */
  public Run getRunByAlias(String alias) throws IOException;

  public RunQC getRunQCById(long runQcId) throws IOException;

  public SampleQC getSampleQCById(long sampleQcId) throws IOException;

  public Submission getSubmissionById(long submissionId) throws IOException;

  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException;

  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException;

  public SequencerReference getSequencerReferenceByUpgradedReferenceId(long upgradedReferenceId) throws IOException;

  public SequencerServiceRecord getSequencerServiceRecordById(long id) throws IOException;

  public QcType getSampleQcTypeById(long qcTypeId) throws IOException;

  public QcType getSampleQcTypeByName(String qcName) throws IOException;

  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException;

  public QcType getLibraryQcTypeByName(String qcName) throws IOException;

  public QcType getRunQcTypeById(long qcTypeId) throws IOException;

  public QcType getRunQcTypeByName(String qcName) throws IOException;

  public QcType getPoolQcTypeById(long qcTypeId) throws IOException;

  public QcType getPoolQcTypeByName(String qcName) throws IOException;

  public Box getBoxById(long boxId) throws IOException;

  public Box getBoxByBarcode(String barcode) throws IOException;

  public Box getBoxByAlias(String alias) throws IOException;

  public TargetedSequencing getTargetedSequencingById(long targetedSequencingId) throws IOException;

  // LISTS
  /**
   * Obtain a list of all the projects the user has access to. Access is defined as either read or write access.
   */
  public Collection<Project> listAllProjects() throws IOException;

  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException;

  public Collection<Project> listAllProjectsBySearch(String query) throws IOException;

  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException;

  public Collection<Box> listAllBoxes() throws IOException;

  public Collection<Box> listAllBoxesWithLimit(long limit) throws IOException;

  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException;

  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByRunId(long runId)
      throws IOException;

  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException;

  public Collection<SequencerPartitionContainer> listAllSequencerPartitionContainers() throws IOException;

  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException;

  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException;

  public Collection<TargetedSequencing> listAllTargetedSequencing() throws IOException;

  /**
   * Obtain a list of all the Platforms
   */
  public Collection<Platform> listAllPlatforms() throws IOException;

  /**
   * PlatformTypes with existing sequencers.
   *
   * @throws IOException
   */
  public Collection<PlatformType> listActivePlatformTypes() throws IOException;

  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException;

  public Collection<String> listDistinctPlatformNames() throws IOException;

  /**
   * Obtain a list of all of the Box attributes (uses, sizes)
   */
  public Collection<BoxUse> listAllBoxUses() throws IOException;

  public Collection<BoxSize> listAllBoxSizes() throws IOException;


  public Collection<Submission> listAllSubmissions() throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<BoxableView> getBoxableViewsFromBarcodeList(Collection<String> barcodeList) throws IOException;

  public BoxableView getBoxableViewByBarcode(String barcode) throws IOException;

  public Collection<SequencerReference> listAllSequencerReferences() throws IOException;

  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException;

  public Collection<SequencerServiceRecord> listAllSequencerServiceRecords() throws IOException;

  public Collection<SequencerServiceRecord> listSequencerServiceRecordsBySequencerId(long referenceId) throws IOException;

  public Collection<QcType> listAllLibraryQcTypes() throws IOException;

  public Collection<QcType> listAllPoolQcTypes() throws IOException;

  public Collection<QcType> listAllRunQcTypes() throws IOException;

  public void discardSingleTube(Box box, String position) throws IOException;

  public void discardAllTubes(Box box) throws IOException;

  // DELETES

  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException;

  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException;

  public void deleteSequencerServiceRecord(SequencerServiceRecord serviceRecord) throws IOException;

  public void deleteContainer(SequencerPartitionContainer container) throws IOException;

  public void deleteRunNote(Run run, Long noteId) throws IOException;

  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException;

  public void deleteBox(Box box) throws IOException;

  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException;

  public Map<String, Integer> getBoxColumnSizes() throws IOException;

  public Map<String, Integer> getProjectColumnSizes() throws IOException;

  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException;

  public Map<String, Integer> getSubmissionColumnSizes() throws IOException;

  public Map<String, Integer> getUserColumnSizes() throws IOException;

  public Map<String, Integer> getGroupColumnSizes() throws IOException;

  public Collection<LibraryDesign> listLibraryDesigns() throws IOException;

  public Collection<LibraryDesign> listLibraryDesignByClass(SampleClass sampleClass) throws IOException;

  public Collection<LibraryDesignCode> listLibraryDesignCodes() throws IOException;

  public Long countContainers() throws IOException;

  void addProjectWatcher(Project project, User watcher) throws IOException;

  void removeProjectWatcher(Project project, User watcher) throws IOException;

  void updateContainer(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException;

}
