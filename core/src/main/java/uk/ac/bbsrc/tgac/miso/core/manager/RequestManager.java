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
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

public interface RequestManager {

  // SAVES
  public long saveProject(Project project) throws IOException;

  public long saveProjectOverview(ProjectOverview overview) throws IOException;

  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException;

  public long saveRun(Run run) throws IOException;

  public void saveRuns(Collection<Run> runs) throws IOException;

  public long saveRunQC(RunQC runQC) throws IOException;

  public void saveRunNote(Run run, Note note) throws IOException;

  public void saveKitNote(Kit kit, Note note) throws IOException;

  public long saveSampleQC(SampleQC sampleQC) throws IOException;

  public long savePool(Pool pool) throws IOException;

  public long savePoolQC(PoolQC poolQC) throws IOException;

  public void savePoolNote(Pool pool, Note note) throws IOException;

  public long saveSequencerPartitionContainer(SequencerPartitionContainer<SequencerPoolPartition> container) throws IOException;

  public long saveStatus(Status status) throws IOException;

  public long saveSubmission(Submission submission) throws IOException;

  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException;

  public long saveSequencerServiceRecord(SequencerServiceRecord record) throws IOException;

  public long saveKit(Kit kit) throws IOException;

  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException;

  public long saveBox(Box box) throws IOException;

  // GETS
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException;

  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException;

  public Pool getPoolById(long poolId) throws IOException;

  public Pool getPoolByBarcode(String barcode) throws IOException;

  public Pool getPoolByIdBarcode(String barcode) throws IOException;

  public PoolQC getPoolQCById(long poolQcId) throws IOException;

  public LibraryQC getLibraryQCById(long qcId) throws IOException;

  public Platform getPlatformById(long platformId) throws IOException;

  public Project getProjectById(long projectId) throws IOException;

  public Project getProjectByAlias(String projectAlias) throws IOException;

  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException;

  public Run getRunById(long runId) throws IOException;

  public Run getRunByAlias(String alias) throws IOException;

  public RunQC getRunQCById(long runQcId) throws IOException;

  public Sample getSampleById(long sampleId) throws IOException;

  public Sample getSampleByBarcode(String barcode) throws IOException;

  public SampleQC getSampleQCById(long sampleQcId) throws IOException;

  public Status getStatusById(long statusId) throws IOException;

  public Status getStatusByRunName(String runName) throws IOException;

  public Submission getSubmissionById(long submissionId) throws IOException;

  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException;

  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException;

  public SequencerReference getSequencerReferenceByUpgradedReferenceId(long upgradedReferenceId) throws IOException;

  public SequencerServiceRecord getSequencerServiceRecordById(long id) throws IOException;

  public Kit getKitById(long kitId) throws IOException;

  public Kit getKitByIdentificationBarcode(String barcode) throws IOException;

  public Kit getKitByLotNumber(String lotNumber) throws IOException;

  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException;

  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException;

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

  public Collection<Run> listAllRuns() throws IOException;

  public Collection<Run> listAllRunsWithLimit(long limit) throws IOException;

  public Collection<Run> listAllRunsBySearch(String query) throws IOException;

  public Collection<Run> listAllRunsByProjectId(long projectId) throws IOException;

  public Collection<Run> listRunsByPoolId(long poolId) throws IOException;

  public Collection<Run> listRunsBySequencerPartitionContainerId(long containerId) throws IOException;

  public Collection<Run> listAllLS454Runs() throws IOException;

  public Collection<Run> listAllIlluminaRuns() throws IOException;

  public Collection<Run> listAllSolidRuns() throws IOException;

  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException;

  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId)
      throws IOException;

  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException;

  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException;

  public Collection<Sample> listAllSamples() throws IOException;

  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException;

  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException;

  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException;

  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException;

  public Collection<Sample> listSamplesByAlias(String alias) throws IOException;

  /**
   * throws AuthorizationIOException if user cannot read one of the requested samples
   */
  public Collection<Sample> getSamplesByIdList(List<Long> idList) throws IOException;

  public Collection<String> listAllSampleTypes() throws IOException;

  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException;

  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException;

  public Collection<TargetedSequencing> listAllTargetedSequencing() throws IOException;

  public Collection<Pool> listAllPools() throws IOException;

  public Collection<Pool> listAllPoolsBySearch(String query) throws IOException;

  public Collection<Pool> listAllPoolsWithLimit(int limit) throws IOException;

  public Collection<Pool> listAllPoolsByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException;

  public Collection<Pool> listReadyPoolsByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException;

  public Collection<Pool> listPoolsByProjectId(long projectId) throws IOException;

  public Collection<Pool> listPoolsByLibraryId(long libraryId) throws IOException;

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

  public Collection<Run> listRunsBySequencerId(Long sequencerReferenceId) throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<Boxable> getBoxablesFromBarcodeList(List<String> barcodeList) throws IOException;

  public Collection<SequencerReference> listAllSequencerReferences() throws IOException;

  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException;

  public Collection<SequencerServiceRecord> listAllSequencerServiceRecords() throws IOException;

  public Collection<SequencerServiceRecord> listSequencerServiceRecordsBySequencerId(long referenceId) throws IOException;

  public Collection<Kit> listAllKits() throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;

  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException;

  public Collection<QcType> listAllSampleQcTypes() throws IOException;

  public Collection<QcType> listAllLibraryQcTypes() throws IOException;

  public Collection<QcType> listAllPoolQcTypes() throws IOException;

  public Collection<QcType> listAllRunQcTypes() throws IOException;

  public Collection<Status> listAllStatus() throws IOException;

  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException;

  public void discardSingleTube(Box box, String position) throws IOException;

  public void discardAllTubes(Box box) throws IOException;

  // DELETES

  public void deleteSample(Sample sample) throws IOException;


  public void deleteRun(Run run) throws IOException;

  public void deleteRunQC(RunQC runQc) throws IOException;

  public void deleteSampleQC(SampleQC sampleQc) throws IOException;

  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException;

  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException;

  public void deleteSequencerServiceRecord(SequencerServiceRecord serviceRecord) throws IOException;

  public void deletePool(Pool pool) throws IOException;

  public void deleteContainer(SequencerPartitionContainer<SequencerPoolPartition> container) throws IOException;

  public void deleteRunNote(Run run, Long noteId) throws IOException;

  public void deleteKitNote(Kit kit, Long noteId) throws IOException;

  public void deletePoolNote(Pool pool, Long noteId) throws IOException;

  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException;

  public void deleteBox(Box box) throws IOException;

  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException;

  public Map<String, Integer> getBoxColumnSizes() throws IOException;

  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException;

  public Map<String, Integer> getProjectColumnSizes() throws IOException;

  public Map<String, Integer> getRunColumnSizes() throws IOException;

  public Map<String, Integer> getSampleColumnSizes() throws IOException;

  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException;

  public Map<String, Integer> getSubmissionColumnSizes() throws IOException;

  public Map<String, Integer> getUserColumnSizes() throws IOException;

  public Map<String, Integer> getGroupColumnSizes() throws IOException;

  public Collection<LibraryDesign> listLibraryDesignByClass(SampleClass sampleClass) throws IOException;

  public Collection<LibraryDesignCode> listLibraryDesignCodes() throws IOException;

  public Long countPoolsByPlatform(PlatformType platform) throws IOException;

  public List<Pool> getPoolsByPageSizeSearchPlatform(int offset, int limit, String querystr, String sortDir,
      String sortCol, PlatformType platform) throws IOException;

  public List<Pool> getPoolsByPageAndSize(int offset, int limit, String sortDir, String sortCol,
      PlatformType platform) throws IOException;

  public Long getNumPoolsBySearch(PlatformType platform, String querystr) throws IOException;

  public Long countRuns() throws IOException;

  public List<Run> getRunsByPageSizeSearch(int offset, int limit, String querystr, String sortDir, String sortCol) throws IOException;

  public List<Run> getRunsByPageAndSize(int offset, int limit, String sortDir, String sortCol) throws IOException;

  public Long countRunsBySearch(String querystr) throws IOException;

  public Run getLatestRunBySequencerPartitionContainerId(Long containerId) throws IOException;

  public Long countContainers() throws IOException;

  public List<SequencerPartitionContainer<SequencerPoolPartition>> getContainersByPageSizeSearch(int offset, int limit, String querystr,
      String sortDir, String sortCol) throws IOException;

  public List<SequencerPartitionContainer<SequencerPoolPartition>> getContainersByPageAndSize(int offset, int limit, String sortDir,
      String sortCol) throws IOException;

  public Long countContainersBySearch(String querystr) throws IOException;

  public List<Run> getRunsByPool(Pool pool) throws IOException;

  public void addRunWatcher(Run run, User watcher) throws IOException;

  public void removeRunWatcher(Run run, User watcher) throws IOException;

  void addProjectWatcher(Project project, User watcher) throws IOException;

  void removeProjectWatcher(Project project, User watcher) throws IOException;

  public void addPoolWatcher(Pool pool, User watcher) throws IOException;

  public void removePoolWatcher(Pool pool, User watcher) throws IOException;

}
