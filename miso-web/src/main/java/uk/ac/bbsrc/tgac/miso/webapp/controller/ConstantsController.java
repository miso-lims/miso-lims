package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.addJsonArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.prometheus.metrics.core.metrics.Gauge;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.Version;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity.DonorSex;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.PermittedSamples;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.BoxSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryAliquotSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibrarySpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PartitionSpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PoolSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.RunLibrarySpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.IlluminaWorkflowType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ThresholdType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.core.service.AssayTestService;
import uk.ac.bbsrc.tgac.miso.core.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.core.service.ContactRoleService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySpikeInService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.MetricService;
import uk.ac.bbsrc.tgac.miso.core.service.MetricSubcategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.PipelineService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunLibraryQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingControlTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.core.service.TissuePieceTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetStageService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.service.printing.PrintableField;
import uk.ac.bbsrc.tgac.miso.core.util.IlluminaExperiment;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheet;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;
import uk.ac.bbsrc.tgac.miso.dto.AssayTestDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentModelDto;
import uk.ac.bbsrc.tgac.miso.dto.MetricDto;
import uk.ac.bbsrc.tgac.miso.dto.MetricSubcategoryDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;

@Controller
public class ConstantsController {

  private static final Gauge constantsTimestamp = Gauge.builder().name("miso_constants_timestamp")
      .help("The epoch time of the last build of the constants.js file.")
      .register();

  private String constantsJs;

  @Autowired
  private AssayService assayService;
  @Autowired
  private AssayTestService assayTestService;
  @Autowired
  private KitDescriptorService kitService;
  @Autowired
  private IndexFamilyService indexFamilyService;
  @Autowired
  private LabService labService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private DetailedQcStatusService detailedQcStatusService;
  @Autowired
  private StainService stainService;
  @Autowired
  private TissueMaterialService tissueMaterialService;
  @Autowired
  private TissueOriginService tissueOriginService;
  @Autowired
  private TissueTypeService tissueTypeService;
  @Autowired
  private TissuePieceTypeService tissuePieceTypeService;
  @Autowired
  private LibraryTypeService libraryTypeService;
  @Autowired
  private LibrarySelectionService librarySelectionService;
  @Autowired
  private LibraryStrategyService libraryStrategyService;
  @Autowired
  private LibraryDesignService libraryDesignService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
  @Autowired
  private LibrarySpikeInService librarySpikeInService;
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private SamplePurposeService samplePurposeService;
  @Autowired
  private SampleTypeService sampleTypeService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private BoxSizeService boxSizeService;
  @Autowired
  private BoxUseService boxUseService;
  @Autowired
  private QualityControlService qcService;
  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private InstrumentService sequencerService;
  @Autowired
  private PartitionQcTypeService partitionQcTypeService;
  @Autowired
  private StudyTypeService studyTypeService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private SequencingContainerModelService containerModelService;
  @Autowired
  private AttachmentCategoryService attachmentCategoryService;
  @Autowired
  private RunPurposeService runPurposeService;
  @Autowired
  private SequencingControlTypeService sequencingControlTypeService;
  @Autowired
  private ScientificNameService scientificNameService;
  @Autowired
  private PipelineService pipelineService;
  @Autowired
  private RunLibraryQcStatusService runLibraryQcStatusService;
  @Autowired
  private WorksetCategoryService worksetCategoryService;
  @Autowired
  private WorksetStageService worksetStageService;
  @Autowired
  private MetricService metricService;
  @Autowired
  private MetricSubcategoryService metricSubcategoryService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private ContactRoleService contactRoleService;

  @Autowired
  private ObjectMapper mapper;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.genomeFolder:}")
  private String genomeFolder;
  @Value("${miso.test.lockConstants:false}")
  private boolean locked;
  @Value("${miso.newOptionSopUrl:#{null}}")
  private String newOptionSopUrl;

  @Resource
  private Boolean boxScannerEnabled;

  private static void createMap(ObjectMapper mapper, ObjectNode node, String name, Map<String, List<String>> map) {
    ObjectNode mapNode = node.putObject(name);
    for (String key : map.keySet()) {
      addJsonArray(mapper, mapNode, key, map.get(key), Function.identity());
    }
  }

  @GetMapping(path = "/constants.js", produces = "application/javascript")
  @ResponseBody
  public synchronized ResponseEntity<String> constantsScript(HttpServletResponse response,
      final UriComponentsBuilder uriBuilder)
      throws IOException {
    response.setContentType("application/javascript");
    // Use a cached copy and only update every
    if (constantsJs == null) {
      final ScheduledFuture<?> current = future;
      if (current != null && !current.isDone()) {
        current.cancel(false);
        future = null;
      }
      rebuildConstants();
    }
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES)).body(constantsJs);
  }

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> future;

  private void rebuildConstants() {
    try {
      ObjectNode node = mapper.createObjectNode();
      node.put("docsVersion", getDocsVersion());
      node.put("isDetailedSample", detailedSample);
      node.put("automaticBarcodes", autoGenerateIdBarcodes);
      node.put("boxScannerEnabled", boxScannerEnabled);
      node.put("newOptionSopUrl", newOptionSopUrl);

      final Collection<SampleValidRelationship> relationships = sampleValidRelationshipService.getAll();

      addJsonArray(mapper, node, "libraryDesigns", libraryDesignService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "libraryTypes", libraryTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "librarySelections", librarySelectionService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "libraryStrategies", libraryStrategyService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "libraryDesignCodes", libraryDesignCodeService.list(), Dtos::asDto);
      Set<Long> activePlatforms = sequencerService.list().stream().filter(Instrument::isActive)
          .map(sequencer -> sequencer.getInstrumentModel().getId())
          .collect(Collectors.toSet());
      addJsonArray(mapper, node, "instrumentModels", instrumentModelService.list(), platform -> {
        InstrumentModelDto dto = Dtos.asDto(platform);
        dto.setActive(activePlatforms.contains(platform.getId()));
        return dto;
      });
      addJsonArray(mapper, node, "kitDescriptors", kitService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "sampleClasses", sampleClassService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "sampleValidRelationships", relationships, Dtos::asDto);
      addJsonArray(mapper, node, "detailedQcStatuses", detailedQcStatusService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "subprojects", subprojectService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "labs", labService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "tissueOrigins", tissueOriginService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "tissueTypes", tissueTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "tissueMaterials", tissueMaterialService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "tissuePieceTypes", tissuePieceTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "stains", stainService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "targetedSequencings", targetedSequencingService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "samplePurposes", samplePurposeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "sequencingParameters", sequencingParametersService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "printerBackends", Arrays.asList(Backend.values()), Dtos::asDto);
      addJsonArray(mapper, node, "printerDrivers", Arrays.asList(Driver.values()), Dtos::asDto);
      addJsonArray(mapper, node, "boxSizes", boxSizeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "boxUses", boxUseService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "studyTypes", studyTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "sampleCategories", SampleClass.CATEGORIES, Function.identity());
      createMap(mapper, node, "sampleSubcategories", SampleClass.SUBCATEGORIES);
      addJsonArray(mapper, node, "submissionAction", Arrays.asList(SubmissionActionType.values()),
          SubmissionActionType::name);
      addJsonArray(mapper, node, "containerModels", containerModelService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "poreVersions", containerService.listPoreVersions(), Dtos::asDto);
      addJsonArray(mapper, node, "spikeIns", librarySpikeInService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "attachmentCategories", attachmentCategoryService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "runPurposes", runPurposeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "sequencingControlTypes", sequencingControlTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "scientificNames", scientificNameService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "pipelines", pipelineService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "runLibraryQcStatuses", runLibraryQcStatusService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "worksetCategories", worksetCategoryService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "worksetStages", worksetStageService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "metrics", metricService.list(), MetricDto::from);
      addJsonArray(mapper, node, "metricSubcategories", metricSubcategoryService.list(), MetricSubcategoryDto::from);
      addJsonArray(mapper, node, "assays", assayService.list(), AssayDto::from);
      addJsonArray(mapper, node, "assayTests", assayTestService.list(), AssayTestDto::from);
      addJsonArray(mapper, node, "sampleSheetFormats", Arrays.asList(SampleSheet.values()), SampleSheet::name);
      addJsonArray(mapper, node, "contactRoles", contactRoleService.list(), Dtos::asDto);

      Collection<IndexFamily> indexFamilies = indexFamilyService.list();
      addJsonArray(mapper, node, "indexFamilies", indexFamilies, Dtos::asDto);
      addJsonArray(mapper, node, "qcTypes", qcService.listQcTypes(), Dtos::asDto);
      addJsonArray(mapper, node, "qcTargets", Arrays.asList(QcTarget.values()), Dtos::asDto);
      addJsonArray(mapper, node, "concentrationUnits", Arrays.asList(ConcentrationUnit.values()), Dtos::asDto);
      addJsonArray(mapper, node, "volumeUnits", Arrays.asList(VolumeUnit.values()), Dtos::asDto);
      addJsonArray(mapper, node, "partitionQcTypes", partitionQcTypeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "referenceGenomes", referenceGenomeService.list(), Dtos::asDto);
      addJsonArray(mapper, node, "spreadsheetFormats", Arrays.asList(SpreadSheetFormat.values()), Dtos::asDto);
      addJsonArray(mapper, node, "sampleSpreadsheets", Arrays.asList(SampleSpreadSheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "librarySpreadsheets", Arrays.asList(LibrarySpreadSheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "libraryAliquotSpreadsheets", Arrays.asList(LibraryAliquotSpreadSheets.values()),
          Dtos::asDto);
      addJsonArray(mapper, node, "poolSpreadsheets", Arrays.asList(PoolSpreadSheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "partitionSpreadsheets", Arrays.asList(PartitionSpreadsheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "runLibrarySpreadsheets", Arrays.asList(RunLibrarySpreadsheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "boxSpreadsheets", Arrays.asList(BoxSpreadSheets.values()), Dtos::asDto);
      addJsonArray(mapper, node, "workflows", Arrays.asList(WorkflowName.values()), Dtos::asDto);
      addJsonArray(mapper, node, "printableFields", Arrays.asList(PrintableField.values()), PrintableField::name);

      ArrayNode platformTypes = node.putArray("platformTypes");
      Collection<PlatformType> activePlatformTypes = instrumentModelService.listActivePlatformTypes();
      for (PlatformType platformType : PlatformType.values()) {
        ObjectNode dto = platformTypes.addObject();
        dto.put("name", platformType.name());
        dto.put("key", platformType.getKey());
        dto.put("containerName", platformType.getContainerName());
        dto.put("active", activePlatformTypes.contains(platformType));
        dto.put("partitionName", platformType.getPartitionName());
        dto.put("pluralPartitionName", platformType.getPluralPartitionName());
      }
      ArrayNode sampleTypes = node.putArray("sampleTypes");
      for (SampleType sampleType : sampleTypeService.list()) {
        if (!sampleType.isArchived()) {
          sampleTypes.add(sampleType.getName());
        }
      }
      ArrayNode donorSexes = node.putArray("donorSexes");
      for (String label : DonorSex.getLabels()) {
        donorSexes.add(label);
      }
      ArrayNode consentLevels = node.putArray("consentLevels");
      for (ConsentLevel level : ConsentLevel.values()) {
        consentLevels.add(level.getLabel());
      }
      ArrayNode strStatuses = node.putArray("strStatuses");
      for (String label : StrStatus.getLabels()) {
        strStatuses.add(label);
      }
      ArrayNode dilutionFactors = node.putArray("dilutionFactors");
      for (String label : DilutionFactor.getLabels()) {
        dilutionFactors.add(label);
      }
      ArrayNode illuminaChemistry = node.putArray("illuminaChemistry");
      for (IlluminaChemistry chemistry : IlluminaChemistry.values()) {
        illuminaChemistry.add(chemistry.name());
      }
      addIlluminaExperimentTypes(node);
      addHealthTypes(node);
      addIlluminaWorkflowTypes(node);
      addInstrumentTypes(node);
      addDataManglingPolicies(node);
      addMetricCategories(node);
      addThresholdTypes(node);
      addPermittedSamples(node);

      ObjectNode warningsNode = mapper.createObjectNode();
      warningsNode.put("consentRevoked", "CONSENT REVOKED");
      warningsNode.put("duplicateIndices", indexChecker.getErrorMismatchesMessage());
      warningsNode.put("nearDuplicateIndices", indexChecker.getWarningMismatchesMessage());
      warningsNode.put("lowQualityLibraries", "Low Quality Libraries");
      warningsNode.put("missingIndex", "MISSING INDEX");
      warningsNode.put("negativeVolume", "Negative Volume");
      node.set("warningMessages", warningsNode);
      node.put("errorEditDistance", indexChecker.getErrorMismatches());
      node.put("warningEditDistance", indexChecker.getWarningMismatches());
      node.put("genomeFolder", genomeFolder);

      // Save the regenerated file in cache.
      constantsJs = "Constants = " + mapper.writeValueAsString(node) + ";";
      constantsTimestamp.set(System.currentTimeMillis() / 1000.0);
    } catch (IOException e) {
      throw new RestException(e);
    }
  }

  private static void addIlluminaExperimentTypes(ObjectNode node) {
    ArrayNode illuminaExperimentTypes = node.putArray("illuminaExperimentTypes");
    for (IlluminaExperiment experiment : IlluminaExperiment.values()) {
      ObjectNode dto = illuminaExperimentTypes.addObject();
      dto.put("name", experiment.name());
      dto.put("description", experiment.getDescription());
    }
  }

  private static void addHealthTypes(ObjectNode node) {
    ArrayNode healthTypes = node.putArray("healthTypes");
    for (HealthType status : HealthType.values()) {
      ObjectNode dto = healthTypes.addObject();
      dto.put("label", status.getKey());
      dto.put("allowedFromSequencer", status.isAllowedFromSequencer());
      dto.put("isDone", status.isDone());
    }
  }

  private static void addIlluminaWorkflowTypes(ObjectNode node) {
    ArrayNode illuminaWorkflowTypes = node.putArray("illuminaWorkflowTypes");
    for (IlluminaWorkflowType wf : IlluminaWorkflowType.values()) {
      ObjectNode dto = illuminaWorkflowTypes.addObject();
      dto.put("label", wf.getLabel());
      dto.put("value", wf.getRawValue());
    }
  }

  private static void addInstrumentTypes(ObjectNode node) {
    ArrayNode instrumentTypes = node.putArray("instrumentTypes");
    for (InstrumentType type : InstrumentType.values()) {
      ObjectNode dto = instrumentTypes.addObject();
      dto.put("label", type.getLabel());
      dto.put("value", type.name());
    }
  }

  private static void addDataManglingPolicies(ObjectNode node) {
    ArrayNode dataManglingPolicies = node.putArray("dataManglingPolicies");
    for (InstrumentDataManglingPolicy policy : InstrumentDataManglingPolicy.values()) {
      ObjectNode dto = dataManglingPolicies.addObject();
      dto.put("label", policy.getLabel());
      dto.put("value", policy.name());
    }
  }

  private static void addMetricCategories(ObjectNode node) {
    ArrayNode metricCategories = node.putArray("metricCategories");
    for (MetricCategory category : MetricCategory.values()) {
      ObjectNode dto = metricCategories.addObject();
      dto.put("label", category.getLabel());
      dto.put("value", category.name());
      dto.put("sortPriority", category.getSortPriority());
    }
  }

  private static void addThresholdTypes(ObjectNode node) {
    ArrayNode thresholdTypes = node.putArray("thresholdTypes");
    for (ThresholdType thresholdType : ThresholdType.values()) {
      ObjectNode dto = thresholdTypes.addObject();
      dto.put("value", thresholdType.name());
      dto.put("sign", thresholdType.getSign());
      dto.put("lowerBound", thresholdType.hasLowerBound());
      dto.put("upperBound", thresholdType.hasUpperBound());
    }
  }

  private static void addPermittedSamples(ObjectNode node) {
    ArrayNode permittedSamplesOutput = node.putArray("permittedSamples");
    for (PermittedSamples permittedSamples : PermittedSamples.values()) {
      ObjectNode dto = permittedSamplesOutput.addObject();
      dto.put("label", permittedSamples.getLabel());
      dto.put("value", permittedSamples.name());
    }
  }

  private static String getDocsVersion() {
    if (Version.VERSION.matches("^\\d+\\.\\d+\\.\\d+$")) {
      return "v" + Version.VERSION;
    } else {
      return "latest";
    }
  }

  public synchronized void refreshConstants(boolean overrideLock) {
    if (constantsJs != null && locked && !overrideLock) {
      return;
    }
    final ScheduledFuture<?> current = future;
    if (current != null && !current.isDone()) {
      current.cancel(false);
    }
    // This will wait a few seconds after a save of an institute default; if another save comes along,
    // it will cancel and wait again. If
    // nothing saves again, it will rebuild the constants string.
    future = executor.schedule(this::rebuildConstants, 15, TimeUnit.SECONDS);
  }

  @Scheduled(fixedDelay = 900_000)
  public synchronized void refreshConstants() {
    refreshConstants(false);
  }

}
