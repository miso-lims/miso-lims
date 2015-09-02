/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
import java.util.LinkedList;
import java.util.List;

import com.eaglegenomics.simlims.core.Note;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.*;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;

public interface RequestManager {

  //SAVES
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
  public long saveKit(Kit kit) throws IOException;
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException;
  public <T extends List<S>, S extends Plateable> long savePlate(Plate<T, S> plate) throws IOException;
  public long saveAlert(Alert alert) throws IOException;
  public long saveEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException;

  //GETS
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException;
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException;

  public Experiment getExperimentById(long experimentId) throws IOException;
  public Pool<? extends Poolable> getPoolById(long poolId) throws IOException;
  public Pool<? extends Poolable> getPoolByBarcode(String barcode) throws IOException;
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException;
  public PoolQC getPoolQCById(long poolQcId) throws IOException;
  public Library getLibraryById(long libraryId) throws IOException;
  public Library getLibraryByBarcode(String barcode) throws IOException;
  public Library getLibraryByAlias(String alias) throws IOException;
  public Dilution getDilutionByBarcode(String barcode) throws IOException;
  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException;
  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException;
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException;
  public LibraryQC getLibraryQCById(long qcId) throws IOException;
  public LibraryType getLibraryTypeById(long libraryId) throws IOException;
  public LibraryType getLibraryTypeByDescription(String description) throws IOException;
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;
  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException;
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException;
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException;
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException;
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException;
  public emPCR getEmPcrById(long pcrId) throws IOException;
  public emPCRDilution getEmPcrDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;
  public emPCRDilution getEmPcrDilutionById(long dilutionId) throws IOException;
  public emPCRDilution getEmPcrDilutionByBarcode(String barcode) throws IOException;
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
  //public <T extends LinkedList<S>, S extends Plateable> Plate<T, S> getPlateById(long plateId) throws IOException;
  //public <T extends LinkedList<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException;
  //public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateById(long plateId) throws IOException;
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateById(long plateId) throws IOException;
  public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException;
  public Alert getAlertById(long alertId) throws IOException;
  public EntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException;


//LISTS
  /**
   * Obtain a list of all the projects the user has access to. Access is
   * defined as either read or write access.
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

  /**
   * Obtain a list of all the requests the user has access to within this
   * project. Access is defined as either read or write access.
   */
  //public Collection<Request> listAllRequests(User user, Project project) throws IOException;
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

  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId) throws IOException;
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode) throws IOException;
  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException;
  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException;
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException;

  public Collection<Sample> listAllSamples() throws IOException;
  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException;
  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException;
  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException;
  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException;
  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException;
  public Collection<Sample> listSamplesByAlias(String alias) throws IOException;

  public Collection<String> listAllSampleTypes() throws IOException;
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException;

  public Collection<Library> listAllLibraries() throws IOException;
  public Collection<Library> listAllLibrariesWithLimit(long limit) throws IOException;
  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException;
  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException;
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException;
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException;

  public Collection<LibraryType> listAllLibraryTypes() throws IOException;
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformType) throws IOException;
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException;
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException;
  public Collection<TagBarcode> listAllTagBarcodes() throws IOException;
  public Collection<TagBarcode> listAllTagBarcodesByPlatform(String platformType) throws IOException;
  public Collection<TagBarcode> listAllTagBarcodesByStrategyName(String platformType) throws IOException;

  public Collection<Dilution> listDilutionsBySearch(String query, PlatformType platformType) throws IOException;
  public Collection<Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsWithLimit(long limit) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;
  public Collection<LibraryDilution> listAllLibraryDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException;

  public Collection<emPCRDilution> listAllEmPcrDilutions() throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsByEmPcrId(long pcrId) throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectId(long projectId) throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsBySearch(String query, PlatformType platformType) throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException;

  public Collection<emPCR> listAllEmPCRs() throws IOException;
  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException;
  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException;

  public Collection<Pool<? extends Poolable>> listAllPools() throws IOException;
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatform(PlatformType platformType) throws IOException;
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException;
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatform(PlatformType platformType) throws IOException;
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException;
  public Collection<Pool<? extends Poolable>> listPoolsByProjectId(long projectId) throws IOException;
  public Collection<Pool<? extends Poolable>> listPoolsByLibraryId(long libraryId) throws IOException;
  public Collection<Pool<? extends Poolable>> listPoolsBySampleId(long sampleId) throws IOException;
  public Collection<PoolQC> listAllPoolQCsByPoolId(long poolId) throws IOException;

  /**
   * Obtain a list of all the Experiments the user has access to. Access is
   * defined as either read or write access.
   */
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException;

  /**
   * Obtain a list of all the Studys the user has access to. Access is
   * defined as either read or write access.
   */
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException;

  /**
   * Obtain a list of all the Platforms
   */
  public Collection<Platform> listAllPlatforms() throws IOException;
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException;
  public Collection<String> listDistinctPlatformNames() throws IOException;
  //public Collection<PlatformType> listAllPlatformTypes() throws IOException;

  /**
   * Obtain a list of all the StudyTypes
   */
  public Collection<String> listAllStudyTypes() throws IOException;
  public Collection<Submission> listAllSubmissions() throws IOException;
  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException;

  public Collection<SequencerReference> listAllSequencerReferences() throws IOException;
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException;

  public Collection<Kit> listAllKits() throws IOException;
  //public Collection<KitType> listAllKitTypes() throws IOException;
  public Collection<Kit> listKitsByExperimentId(long experimentId) throws IOException;
  public Collection<Kit> listKitsByManufacturer(String manufacturer) throws IOException;
  public Collection<Kit> listKitsByType(KitType kitType) throws IOException;
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException;
  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException;

  public Collection<QcType> listAllSampleQcTypes() throws IOException;
  public Collection<QcType> listAllLibraryQcTypes() throws IOException;
  public Collection<QcType> listAllPoolQcTypes() throws IOException;
  public Collection<QcType> listAllRunQcTypes() throws IOException;

  public Collection<Status> listAllStatus() throws IOException;
  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException;

  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlates() throws IOException;
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesByProjectId(long projectId) throws IOException;
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesBySearch(String str) throws IOException;

  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException;
  public Collection<Alert> listAlertsByUserId(long userId) throws IOException;
  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException;

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
  public void deleteEmPcrDilution(emPCRDilution dilution) throws IOException;
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException;
  public void deletePool(Pool pool) throws IOException;
  public void deletePlate(Plate plate) throws IOException;
  public void deleteEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException;
  public void deletePartition(SequencerPoolPartition partition) throws IOException;
  public void deleteContainer(SequencerPartitionContainer container) throws IOException;
  public void deleteNote(Note note) throws IOException;
}
