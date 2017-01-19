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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponentDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
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
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;

public interface RequestManager {

  public boolean isKitComponentAlreadyLogged(String identificationBarcode) throws IOException;

  // SAVES
  public long saveProject(Project project) throws IOException;

  public long saveProjectOverview(ProjectOverview overview) throws IOException;

  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException;

  public long saveRun(Run run) throws IOException;

  public int[] saveRuns(Collection<Run> runs) throws IOException;

  public long saveRunQC(RunQC runQC) throws IOException;

  public long saveRunNote(Run run, Note note) throws IOException;

  public long saveSample(Sample sample) throws IOException;

  public long saveSampleQC(SampleQC sampleQC) throws IOException;

  public long saveSampleNote(Sample sample, Note note) throws IOException;

  public long saveEmPcrDilution(emPCRDilution dilution) throws IOException;

  public long saveLibrary(Library library) throws IOException;

  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException;

  public long saveLibraryNote(Library library, Note note) throws IOException;

  public long saveLibraryQC(LibraryQC libraryQC) throws IOException;

  public long savePool(Pool pool) throws IOException;

  public long savePoolQC(PoolQC poolQC) throws IOException;

  public long savePoolNote(Pool pool, Note note) throws IOException;

  public long saveEmPCR(emPCR pcr) throws IOException;

  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException;

  public long saveExperiment(Experiment experiment) throws IOException;

  public long saveStudy(Study study) throws IOException;

  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException;

  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException;

  public long savePlatform(Platform platform) throws IOException;

  public long saveStatus(Status status) throws IOException;

  public long saveSecurityProfile(SecurityProfile profile) throws IOException;

  public long saveSubmission(Submission submission) throws IOException;

  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException;

  public long saveKitComponent(KitComponent kitComponent) throws IOException;

  public long saveKitComponentDescriptor(KitComponentDescriptor kitComponentDescriptor) throws IOException;

  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException;

  public long saveKitChangeLog(JSONObject changeLog) throws IOException;

  public long saveAlert(Alert alert) throws IOException;

  public long saveEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException;

  public long saveBox(Box box) throws IOException;

  // GETS
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException;

  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException;

  public Experiment getExperimentById(long experimentId) throws IOException;

  public Pool getPoolById(long poolId) throws IOException;

  public Pool getPoolByBarcode(String barcode) throws IOException;

  public Pool getPoolByBarcode(String barcode, PlatformType platformType) throws IOException;

  public Pool getPoolByIdBarcode(String barcode) throws IOException;

  public PoolQC getPoolQCById(long poolQcId) throws IOException;

  public Library getLibraryById(long libraryId) throws IOException;

  public Library getLibraryByBarcode(String barcode) throws IOException;

  public Collection<Library> listLibrariesByAlias(String alias) throws IOException;

  public Library getAdjacentLibraryById(long libraryId, boolean before) throws IOException;

  public Dilution getDilutionByBarcode(String barcode) throws IOException;

  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException;

  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;

  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;

  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException;

  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException;

  public Integer countLibraryDilutionsByPlatform(PlatformType platform) throws IOException;

  public List<LibraryDilution> getLibraryDilutionsForPoolDataTable(int offset, int limit, String search, String sortDir, String sortCol,
      PlatformType platform) throws IOException;

  public LibraryQC getLibraryQCById(long qcId) throws IOException;

  public LibraryType getLibraryTypeById(long libraryId) throws IOException;

  public LibraryType getLibraryTypeByDescription(String description) throws IOException;

  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;

  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException;

  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException;

  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException;

  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException;

  public emPCR getEmPCRById(long pcrId) throws IOException;

  public emPCRDilution getEmPCRDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;

  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException;

  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException;

  public Note getNoteById(long noteId) throws IOException;

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

  public Study getStudyById(long studyId) throws IOException;

  public Submission getSubmissionById(long submissionId) throws IOException;

  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException;

  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException;

  public SequencerReference getSequencerReferenceByRunId(long runId) throws IOException;

  public SequencerServiceRecord getSequencerServiceRecordById(long id) throws IOException;

  public JSONArray getKitChangeLog() throws IOException;

  public JSONArray getKitChangeLogByKitComponentId(long kitComponentId) throws IOException;

  public KitComponent getKitComponentById(long kitId) throws IOException;

  public KitComponent getKitComponentByIdentificationBarcode(String barcode) throws IOException;

  public KitComponentDescriptor getKitComponentDescriptorById(long kitComponentDescriptorId) throws IOException;

  public KitComponentDescriptor getKitComponentDescriptorByReferenceNumber(String referenceNumber) throws IOException;

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

  public Alert getAlertById(long alertId) throws IOException;

  public EntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException;

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

  public Collection<Study> listAllStudies() throws IOException;

  public Collection<Study> listAllStudiesWithLimit(long limit) throws IOException;

  public Collection<Study> listAllStudiesBySearch(String query) throws IOException;

  public Collection<Study> listAllStudiesByLibraryId(long libraryId) throws IOException;

  public Collection<Experiment> listAllExperiments() throws IOException;

  public Collection<Experiment> listAllExperimentsWithLimit(long limit) throws IOException;

  public Collection<Experiment> listAllExperimentsBySearch(String query) throws IOException;

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

  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException;

  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException;

  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException;

  public Collection<ChangeLog> listAllChanges(String type) throws IOException;

  public Collection<Sample> listAllSamples() throws IOException;

  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException;

  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException;

  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException;

  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException;

  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException;

  public Collection<Sample> listSamplesByAlias(String alias) throws IOException;

  /**
   * throws AuthorizationIOException if user cannot read one of the requested samples
   */
  public Collection<Sample> getSamplesByIdList(List<Long> idList) throws IOException;

  public Collection<String> listAllSampleTypes() throws IOException;

  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException;

  @Deprecated // massively detremental to performance.
  public Collection<Library> listAllLibraries() throws IOException;

  public Collection<Library> listAllLibrariesWithLimit(long limit) throws IOException;

  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException;

  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException;

  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException;

  /**
   * throws AuthorizationIOException if user cannot read one of the requested libraries
   */
  public Collection<Library> getLibrariesByIdList(List<Long> idList) throws IOException;

  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException;

  public Collection<LibraryType> listAllLibraryTypes() throws IOException;

  public Collection<LibraryType> listLibraryTypesByPlatform(String platformType) throws IOException;

  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException;

  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException;

  public Collection<Dilution> listAllLibraryDilutionsBySearchAndPlatform(String query, PlatformType platformType) throws IOException;

  public Collection<Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsWithLimit(long limit) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType)
      throws IOException;

  public Collection<TargetedSequencing> listAllTargetedSequencing() throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutions() throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsByEmPcrId(long pcrId) throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsByPlatform(PlatformType platformType) throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectId(long projectId) throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsBySearch(String query, PlatformType platformType) throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  public Collection<emPCRDilution> listAllEmPCRDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException;

  public Collection<emPCR> listAllEmPCRs() throws IOException;

  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException;

  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException;

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

  public Collection<Pool> listPoolsBySampleId(long sampleId) throws IOException;

  public Collection<PoolQC> listAllPoolQCsByPoolId(long poolId) throws IOException;

  /**
   * Obtain a list of all the Experiments the user has access to. Access is defined as either read or write access.
   */
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException;

  /**
   * Obtain a list of all the Studys the user has access to. Access is defined as either read or write access.
   */
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException;

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

  /**
   * Obtain a list of all the StudyTypes
   */
  public Collection<String> listAllStudyTypes() throws IOException;

  public Collection<Submission> listAllSubmissions() throws IOException;

  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException;

  public Collection<Run> listRunsBySequencerId(Long sequencerReferenceId) throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<Boxable> getBoxablesFromBarcodeList(List<String> barcodeList) throws IOException;

  public Collection<SequencerReference> listAllSequencerReferences() throws IOException;

  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException;

  public Collection<SequencerServiceRecord> listAllSequencerServiceRecords() throws IOException;

  public Collection<SequencerServiceRecord> listSequencerServiceRecordsBySequencerId(long referenceId) throws IOException;

  public Collection<KitComponent> listAllKitComponents() throws IOException;

  public Collection<KitComponent> listKitComponentsByExperimentId(long experimentId) throws IOException;

  public Collection<KitComponent> listKitComponentsByManufacturer(String manufacturer) throws IOException;

  public Collection<KitComponent> listKitComponentsByType(KitType kitType) throws IOException;

  public Collection<KitComponent> listKitComponentsByLocationBarcode(String locationBarcode) throws IOException;

  public Collection<KitComponent> listKitComponentsByLotNumber(String lotNumber) throws IOException;

  public Collection<KitComponent> listKitComponentsByReceivedDate(LocalDate receivedDate) throws IOException;

  public Collection<KitComponent> listKitComponentsByExpiryDate(LocalDate expiryDate) throws IOException;

  public Collection<KitComponent> listKitComponentsByExhausted(boolean exhausted) throws IOException;

  public Collection<KitComponent> listKitComponentsByKitComponentDescriptorId(long kitComponentDescriptorId) throws IOException;

  public Collection<KitComponent> listKitComponentsByKitDescriptorId(long kitDescriptorID) throws IOException;

  public Collection<KitComponentDescriptor> listKitComponentDescriptorsByKitDescriptorId(long kitDescriptorId) throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByManufacturer(String manufacturer) throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByPlatform(PlatformType platformType) throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByUnits(String units) throws IOException;

  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;

  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException;

  public Collection<QcType> listAllSampleQcTypes() throws IOException;

  public Collection<QcType> listAllLibraryQcTypes() throws IOException;

  public Collection<QcType> listAllPoolQcTypes() throws IOException;

  public Collection<QcType> listAllRunQcTypes() throws IOException;

  public Collection<Status> listAllStatus() throws IOException;

  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException;

  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException;

  public Collection<Alert> listAlertsByUserId(long userId) throws IOException;

  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException;

  public void discardSingleTube(Box box, String position) throws IOException;

  public void discardAllTubes(Box box) throws IOException;

  // DELETES
  public void deleteProject(Project project) throws IOException;

  public void deleteStudy(Study study) throws IOException;

  public void deleteExperiment(Experiment experiment) throws IOException;

  public void deleteSample(Sample sample) throws IOException;

  public void deleteLibrary(Library library) throws IOException;

  public void deleteEmPCR(emPCR empcr) throws IOException;

  public void deleteRun(Run run) throws IOException;

  public void deleteRunQC(RunQC runQc) throws IOException;

  public void deleteSampleQC(SampleQC sampleQc) throws IOException;

  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException;

  public void deletePoolQC(PoolQC poolQc) throws IOException;

  public void deleteLibraryDilution(LibraryDilution dilution) throws IOException;

  public void deleteEmPCRDilution(emPCRDilution dilution) throws IOException;

  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException;

  public void deleteSequencerServiceRecord(SequencerServiceRecord serviceRecord) throws IOException;

  public void deletePool(Pool pool) throws IOException;

  public void deleteEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException;

  public void deletePartition(SequencerPoolPartition partition) throws IOException;

  public void deleteContainer(SequencerPartitionContainer container) throws IOException;

  public void deleteNote(Note note) throws IOException;

  public void deleteBox(Box box) throws IOException;

  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException;

  public Map<String, Integer> getBoxColumnSizes() throws IOException;

  public Map<String, Integer> getExperimentColumnSizes() throws IOException;

  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException;

  public Map<String, Integer> getLibraryColumnSizes() throws IOException;

  public Map<String, Integer> getProjectColumnSizes() throws IOException;

  public Map<String, Integer> getRunColumnSizes() throws IOException;

  public Map<String, Integer> getSampleColumnSizes() throws IOException;

  public Map<String, Integer> getStudyColumnSizes() throws IOException;

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

  public int countLibraries() throws IOException;

  public List<Library> getLibrariesByCreationDate(Date from, Date to) throws IOException;

  public List<Library> getLibrariesByPageSizeSearch(int offset, int limit, String querystr, String sortDir, String sortCol)
      throws IOException;

  public List<Library> getLibrariesByPageAndSize(int offset, int limit, String sortDir, String sortCol) throws IOException;

  public Long countLibrariesBySearch(String querystr) throws IOException;

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

  public Project lazyGetProjectById(long projectId) throws IOException;

  public Integer countLibraryDilutionsBySearchAndPlatform(String search, PlatformType platform) throws IOException;

  public List<Run> getRunsByPool(Pool pool) throws IOException;

  public Collection<KitComponent> listKitsByType(KitType kitType) throws IOException;

  public KitComponent getKitByLotNumber(String lotNumber) throws IOException;

  public long saveSequencerServiceRecord(SequencerServiceRecord record) throws IOException;

  public Collection<KitComponent> listKitsByManufacturer(String manufacturer) throws IOException;

  public Collection<KitComponent> listKitsByExperimentId(long experimentId) throws IOException;

}
