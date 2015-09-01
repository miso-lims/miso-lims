package uk.ac.bbsrc.tgac.miso.core.test;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.SequencingKit;
import uk.ac.bbsrc.tgac.miso.core.data.type.*;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessDefinitionImpl;
import uk.ac.bbsrc.tgac.miso.core.workflow.impl.WorkflowProcessImpl;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.*;

/**
 * Dummy request manager that just returns empty collections, 0 for counts, and stateless objects
 *
 * @author Rob Davey
 * @date 27/11/14
 * @since 0.2.1
 */
public class MockRequestManager implements RequestManager {
  @Override
  public long saveProject(Project project) throws IOException {
    return 0;
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    return 0;
  }

  @Override
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    return 0;
  }

  @Override
  public long saveRun(Run run) throws IOException {
    return 0;
  }

  @Override
  public int[] saveRuns(Collection<Run> runs) throws IOException {
    return new int[0];
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    return 0;
  }

  @Override
  public long saveRunNote(Run run, Note note) throws IOException {
    return 0;
  }

  @Override
  public long saveSample(Sample sample) throws IOException {
    return 0;
  }

  @Override
  public long saveSampleQC(SampleQC sampleQC) throws IOException {
    return 0;
  }

  @Override
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    return 0;
  }

  @Override
  public long saveEmPcrDilution(emPCRDilution dilution) throws IOException {
    return 0;
  }

  @Override
  public long saveLibrary(Library library) throws IOException {
    return 0;
  }

  @Override
  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException {
    return 0;
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    return 0;
  }

  @Override
  public long saveLibraryQC(LibraryQC libraryQC) throws IOException {
    return 0;
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    return 0;
  }

  @Override
  public long savePoolQC(PoolQC poolQC) throws IOException {
    return 0;
  }

  @Override
  public long saveEmPCR(emPCR pcr) throws IOException {
    return 0;
  }

  @Override
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    return 0;
  }

  @Override
  public long saveExperiment(Experiment experiment) throws IOException {
    return 0;
  }

  @Override
  public long saveStudy(Study study) throws IOException {
    return 0;
  }

  @Override
  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException {
    return 0;
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    return 0;
  }

  @Override
  public long savePlatform(Platform platform) throws IOException {
    return 0;
  }

  @Override
  public long saveStatus(Status status) throws IOException {
    return 0;
  }

  @Override
  public long saveSecurityProfile(SecurityProfile profile) throws IOException {
    return 0;
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    return 0;
  }

  @Override
  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException {
    return 0;
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    return 0;
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    return 0;
  }

  @Override
  public <T extends List<S>, S extends Plateable> long savePlate(Plate<T, S> plate) throws IOException {
    return 0;
  }

  @Override
  public long saveAlert(Alert alert) throws IOException {
    return 0;
  }

  @Override
  public long saveEntityGroup(HierarchicalEntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    return 0;
  }

  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    return new PartitionImpl();
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException {
    return new SequencerPartitionContainerImpl();
  }

  @Override
  public Experiment getExperimentById(long experimentId) throws IOException {
    return new ExperimentImpl();
  }

  @Override
  public Pool<? extends Poolable> getPoolById(long poolId) throws IOException {
    return new PoolImpl<>();
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode) throws IOException {
    return new PoolImpl<>();
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    return new PoolImpl<>();
  }

  @Override
  public PoolQC getPoolQCById(long poolQcId) throws IOException {
    return new PoolQCImpl();
  }

  @Override
  public Library getLibraryById(long libraryId) throws IOException {
    return new LibraryImpl();
  }

  @Override
  public Library getLibraryByBarcode(String barcode) throws IOException {
    return new LibraryImpl();
  }

  @Override
  public Library getLibraryByAlias(String alias) throws IOException {
    return new LibraryImpl();
  }

  @Override
  public Dilution getDilutionByBarcode(String barcode) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    return new LibraryDilution();
  }

  @Override
  public LibraryQC getLibraryQCById(long qcId) throws IOException {
    return new LibraryQCImpl();
  }

  @Override
  public LibraryType getLibraryTypeById(long libraryId) throws IOException {
    return new LibraryType();
  }

  @Override
  public LibraryType getLibraryTypeByDescription(String description) throws IOException {
    return new LibraryType();
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    return new LibraryType();
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException {
    return new LibrarySelectionType();
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    return new LibrarySelectionType();
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException {
    return new LibraryStrategyType();
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    return new LibraryStrategyType();
  }

  @Override
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException {
    return new TagBarcodeImpl();
  }

  @Override
  public emPCR getEmPCRById(long pcrId) throws IOException {
    return new emPCR();
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    return new emPCRDilution();
  }

  @Override
  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException {
    return new emPCRDilution();
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException {
    return new emPCRDilution();
  }

  @Override
  public Note getNoteById(long noteId) throws IOException {
    return new Note();
  }

  @Override
  public Platform getPlatformById(long platformId) throws IOException {
    return new PlatformImpl();
  }

  @Override
  public Project getProjectById(long projectId) throws IOException {
    return new ProjectImpl();
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    return new ProjectImpl();
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    return new ProjectOverview();
  }

  @Override
  public Run getRunById(long runId) throws IOException {
    return new RunImpl();
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    return new RunImpl();
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    return new RunQCImpl();
  }

  @Override
  public Sample getSampleById(long sampleId) throws IOException {
    return new SampleImpl();
  }

  @Override
  public Sample getSampleByBarcode(String barcode) throws IOException {
    return new SampleImpl();
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    return new SampleQCImpl();
  }

  @Override
  public Status getStatusById(long statusId) throws IOException {
    return new StatusImpl();
  }

  @Override
  public Status getStatusByRunName(String runName) throws IOException {
    return new StatusImpl();
  }

  @Override
  public Study getStudyById(long studyId) throws IOException {
    return new StudyImpl();
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    return new SubmissionImpl();
  }

  @Override
  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException {
    return new SequencerReferenceImpl("foo", Inet4Address.getLocalHost(), new PlatformImpl());
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    return new SequencerReferenceImpl(referenceName, Inet4Address.getLocalHost(), new PlatformImpl());
  }

  @Override
  public SequencerReference getSequencerReferenceByRunId(long runId) throws IOException {
    return new SequencerReferenceImpl("foo", Inet4Address.getLocalHost(), new PlatformImpl());
  }

  @Override
  public Kit getKitById(long kitId) throws IOException {
    return new SequencingKit();
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    return new SequencingKit();
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    return new SequencingKit();
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    return new KitDescriptor();
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    return new KitDescriptor();
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    return new QcType();
  }

  @Override
  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    return new QcType();
  }

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateById(long plateId) throws IOException {
    return new PlateImpl<>();
  }

  @Override
  public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException {
    return (Plate)new PlateImpl<Plateable>();
  }

  @Override
  public Alert getAlertById(long alertId) throws IOException {
    return new MockAlert(new UserImpl());
  }

  @Override
  public HierarchicalEntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException {
    return new HierarchicalEntityGroupImpl<>();
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Study> listAllStudies() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Study> listAllStudiesWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Study> listAllStudiesBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Study> listAllStudiesByLibraryId(long libraryId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Experiment> listAllExperiments() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Experiment> listAllExperimentsWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Experiment> listAllExperimentsBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllRuns() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllRunsWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllRunsBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllRunsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listRunsByPoolId(long poolId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listRunsBySequencerPartitionContainerId(long containerId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllLS454Runs() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllIlluminaRuns() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listAllSolidRuns() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamples() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sample> listSamplesByAlias(String alias) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Library> listAllLibraries() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Library> listAllLibrariesWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryType> listAllLibraryTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByPlatform(String platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByStrategyName(String platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Dilution> listDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsWithLimit(long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByEmPcrId(long pcrId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPlatform(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCR> listAllEmPCRs() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPools() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByLibraryId(long libraryId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsBySampleId(long sampleId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<PoolQC> listAllPoolQCsByPoolId(long poolId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Platform> listAllPlatforms() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<String> listAllStudyTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerReference> listAllSequencerReferences() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Kit> listAllKits() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Kit> listKitsByExperimentId(long experimentId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Kit> listKitsByManufacturer(String manufacturer) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Kit> listKitsByType(KitType kitType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Status> listAllStatus() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlates() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesByProjectId(long projectId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesBySearch(String str) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public <T extends Nameable, S extends Nameable> Collection<HierarchicalEntityGroup<T, S>> listAllEntityGroupsByEntityType(Class<T> parentType, Class<S> entityType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Workflow getWorkflowById(long workflowId) throws IOException {
    return new WorkflowImpl(new WorkflowDefinitionImpl(new TreeMap<Integer, WorkflowProcessDefinition>()));
  }

  @Override
  public WorkflowProcess getWorkflowProcessById(long workflowProcessId) throws IOException {
    return new WorkflowProcessImpl(new WorkflowProcessDefinitionImpl());
  }

  @Override
  public WorkflowDefinition getWorkflowDefinitionById(long workflowDefinitionId) throws IOException {
    return new WorkflowDefinitionImpl(new TreeMap<Integer, WorkflowProcessDefinition>());
  }

  @Override
  public WorkflowProcessDefinition getWorkflowProcessDefinitionById(long workflowProcessDefinitionId) throws IOException {
    return new WorkflowProcessDefinitionImpl();
  }

  @Override
  public Collection<Workflow> listAllWorkflows() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Workflow> listWorkflowsByAssignee(long userId) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Workflow> listIncompleteWorkflows() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<Workflow> listWorkflowsByStatus(HealthType healthType) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<WorkflowDefinition> listAllWorkflowDefinitions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<WorkflowDefinition> listWorkflowDefinitionsBySearch(String searchStr) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<WorkflowProcessDefinition> listAllWorkflowProcessDefinitions() throws IOException {
    return Collections.emptyList();
  }

  @Override
  public Collection<WorkflowProcessDefinition> listWorkflowProcessDefinitionsBySearch(String searchStr) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public long saveWorkflow(Workflow workflow) throws IOException {
    return 0;
  }

  @Override
  public long saveWorkflowProcess(WorkflowProcess workflowProcess) throws IOException {
    return 0;
  }

  @Override
  public long saveWorkflowDefinition(WorkflowDefinition workflowDefinition) throws IOException {
    return 0;
  }

  @Override
  public long saveWorkflowProcessDefinition(WorkflowProcessDefinition workflowProcessDefinition) throws IOException {
    return 0;
  }

  @Override
  public Set<String> listAllStateKeys() throws IOException {
    return Collections.emptySet();
  }

  @Override
  public Map<Long, String> listStateKeysBySearch(String str) throws IOException {
    return Collections.emptyMap();
  }

  @Override
  public boolean validateStateKeys(Set<String> keys) throws IOException {
    return true;
  }

  @Override
  public long getIdForStateKey(String key) throws IOException {
    return 0;
  }

  @Override
  public String getStateKey(long keyId) throws IOException {
    return "";
  }

  @Override
  public String getStateValue(long valueId) throws IOException {
    return "";
  }

  @Override
  public long saveStateKey(String key) throws IOException {
    return 0;
  }

  @Override
  public long saveStateValue(String value) throws IOException {
    return 0;
  }

  @Override
  public List<Map<Long, Long>> saveState(JSONObject jsonObject) throws IOException {
    return Collections.emptyList();
  }

  @Override
  public void deleteProject(Project project) throws IOException {
  }

  @Override
  public void deleteStudy(Study study) throws IOException {
  }

  @Override
  public void deleteExperiment(Experiment experiment) throws IOException {
  }

  @Override
  public void deleteSample(Sample sample) throws IOException {
  }

  @Override
  public void deleteLibrary(Library library) throws IOException {
  }

  @Override
  public void deleteEmPCR(emPCR empcr) throws IOException {
  }

  @Override
  public void deleteRun(Run run) throws IOException {
  }

  @Override
  public void deleteRunQC(RunQC runQc) throws IOException {
  }

  @Override
  public void deleteSampleQC(SampleQC sampleQc) throws IOException {
  }

  @Override
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
  }

  @Override
  public void deletePoolQC(PoolQC poolQc) throws IOException {
  }

  @Override
  public void deleteLibraryDilution(LibraryDilution dilution) throws IOException {
  }

  @Override
  public void deleteEmPCRDilution(emPCRDilution dilution) throws IOException {
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
  }

  @Override
  public void deletePool(Pool pool) throws IOException {
  }

  @Override
  public void deletePlate(Plate plate) throws IOException {
  }

  @Override
  public void deleteEntityGroup(HierarchicalEntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
  }

  @Override
  public void deletePartition(SequencerPoolPartition partition) throws IOException {
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
  }

  @Override
  public void deleteNote(Note note) throws IOException {
  }
}
