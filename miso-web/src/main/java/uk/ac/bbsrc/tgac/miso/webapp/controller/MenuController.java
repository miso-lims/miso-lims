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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity.DonorSex;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryAliquotSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibrarySpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PartitionSpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PoolSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.IlluminaWorkflowType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
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
import uk.ac.bbsrc.tgac.miso.core.service.OrderPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.core.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.core.service.TissuePieceTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Layout;
import uk.ac.bbsrc.tgac.miso.core.util.IlluminaExperiment;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheet;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentModelDto;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;

import io.prometheus.client.Gauge;

@Controller
public class MenuController implements ServletContextAware {
  protected static final Logger log = LoggerFactory.getLogger(MenuController.class);

  private static final Gauge constantsTimestamp = Gauge
      .build("miso_constants_timestamp", "The epoch time of the last build of the constants.js file.").register();

  private String constantsJs;

  ServletContext servletContext;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private KitDescriptorService kitService;
  @Autowired
  private IndexService indexService;
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
  private SampleGroupService sampleGroupService;
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
  private OrderPurposeService orderPurposeService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private NamingScheme namingScheme;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.genomeFolder:}")
  private String genomeFolder;

  @Resource
  private Boolean boxScannerEnabled;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @RequestMapping("/login")
  public ModelAndView loginPage(ModelMap model, @RequestParam(name = "login_error", required = false) Integer loginError) {
    return new ModelAndView("/WEB-INF/login.jsp", model);
  }

  @RequestMapping("/myAccount")
  public ModelAndView myAccountMenu(ModelMap model) {
    try {
      User user = authorizationManager.getCurrentUser();
      String realName = user.getFullName();
      StringBuilder groups = new StringBuilder();
      for (String role : user.getRoles()) {
        groups.append(role.replaceAll("ROLE_", "") + "&nbsp;");
      }
      model.put("title", "My Account");
      model.put("userRealName", realName);
      model.put("userId", user.getId());
      model.put("apiKey", SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")));
      model.put("userGroups", groups.toString());
      return new ModelAndView("/WEB-INF/pages/myAccount.jsp", model);
    } catch (IOException e) {
      log.error("my account menu", e);
      return new ModelAndView("/WEB-INF/login.jsp", model);
    } catch (NoSuchAlgorithmException e) {
      log.error("my account menu", e);
      return new ModelAndView("/WEB-INF/login.jsp", model);
    }
  }

  @GetMapping("/")
  public ModelAndView redirectMisoRoot(ModelMap model) {
    model.clear();
    return new ModelAndView("redirect:mainMenu", model);
  }

  @RequestMapping("/mainMenu")
  public ModelAndView mainMenu(ModelMap model) throws IOException {
    User user = authorizationManager.getCurrentUser();
    model.put("title", "Home");
    ObjectMapper mapper = new ObjectMapper();
    model.put("favouriteWorkflows",
        user.getFavouriteWorkflows().stream().map(Dtos::asDto).map(dto -> mapper.valueToTree(dto)).collect(Collectors.toList()));
    return new ModelAndView("/WEB-INF/pages/mainMenu.jsp", model);
  }

  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  private static <Model, Dto> void createArray(ObjectMapper mapper, ObjectNode node, String name,
      Iterable<Model> items,
      Function<Model, Dto> asDto) {
    ArrayNode array = node.putArray(name);
    for (Model item : items) {
      Dto dto = asDto.apply(item);
      JsonNode itemNode = mapper.valueToTree(dto);
      array.add(itemNode);
    }
  }

  @GetMapping(path = "/constants.js")
  @ResponseBody
  public synchronized ResponseEntity<String> constantsScript(HttpServletResponse response, final UriComponentsBuilder uriBuilder)
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
      ObjectMapper mapper = new ObjectMapper();
      ObjectNode node = mapper.createObjectNode();
      node.put("isDetailedSample", detailedSample);
      node.put("automaticBarcodes", autoGenerateIdentificationBarcodes());
      node.put("automaticSampleAlias", namingScheme.hasSampleAliasGenerator());
      node.put("automaticLibraryAlias", namingScheme.hasLibraryAliasGenerator());
      node.put("boxScannerEnabled", boxScannerEnabled);

      final Collection<SampleValidRelationship> relationships = sampleValidRelationshipService.getAll();

      createArray(mapper, node, "libraryDesigns", libraryDesignService.list(), Dtos::asDto);
      createArray(mapper, node, "libraryTypes", libraryTypeService.list(), Dtos::asDto);
      createArray(mapper, node, "librarySelections", librarySelectionService.list(), Dtos::asDto);
      createArray(mapper, node, "libraryStrategies", libraryStrategyService.list(), Dtos::asDto);
      createArray(mapper, node, "libraryDesignCodes", libraryDesignCodeService.list(), Dtos::asDto);
      Set<Long> activePlatforms = sequencerService.list().stream().filter(Instrument::isActive)
          .map(sequencer -> sequencer.getInstrumentModel().getId())
          .collect(Collectors.toSet());
      createArray(mapper, node, "instrumentModels", instrumentModelService.list(), platform -> {
        InstrumentModelDto dto = Dtos.asDto(platform);
        dto.setActive(activePlatforms.contains(platform.getId()));
        return dto;
      });
      createArray(mapper, node, "kitDescriptors", kitService.list(), Dtos::asDto);
      createArray(mapper, node, "sampleClasses", sampleClassService.getAll(), Dtos::asDto);
      createArray(mapper, node, "sampleValidRelationships", relationships, Dtos::asDto);
      createArray(mapper, node, "detailedQcStatuses", detailedQcStatusService.getAll(), Dtos::asDto);
      createArray(mapper, node, "sampleGroups", sampleGroupService.getAll(), Dtos::asDto);
      createArray(mapper, node, "subprojects", subprojectService.list(), Dtos::asDto);
      createArray(mapper, node, "labs", labService.list(), Dtos::asDto);
      createArray(mapper, node, "tissueOrigins", tissueOriginService.list(), Dtos::asDto);
      createArray(mapper, node, "tissueTypes", tissueTypeService.list(), Dtos::asDto);
      createArray(mapper, node, "tissueMaterials", tissueMaterialService.list(), Dtos::asDto);
      createArray(mapper, node, "tissuePieceTypes", tissuePieceTypeService.list(), Dtos::asDto);
      createArray(mapper, node, "stains", stainService.list(), Dtos::asDto);
      createArray(mapper, node, "targetedSequencings", targetedSequencingService.list(), Dtos::asDto);
      createArray(mapper, node, "samplePurposes", samplePurposeService.list(), Dtos::asDto);
      createArray(mapper, node, "sequencingParameters", sequencingParametersService.list(), Dtos::asDto);
      createArray(mapper, node, "printerBackends", Arrays.asList(Backend.values()), Dtos::asDto);
      createArray(mapper, node, "printerDrivers", Arrays.asList(Driver.values()), Dtos::asDto);
      createArray(mapper, node, "printerLayouts", Arrays.asList(Layout.values()), Dtos::asDto);
      createArray(mapper, node, "boxSizes", boxSizeService.list(), Dtos::asDto);
      createArray(mapper, node, "boxUses", boxUseService.list(), Dtos::asDto);
      createArray(mapper, node, "studyTypes", studyTypeService.list(), Dtos::asDto);
      createArray(mapper, node, "sampleCategories", SampleClass.CATEGORIES, Function.identity());
      createArray(mapper, node, "submissionAction", Arrays.asList(SubmissionActionType.values()), SubmissionActionType::name);
      createArray(mapper, node, "containerModels", containerModelService.list(), Dtos::asDto);
      createArray(mapper, node, "poreVersions", containerService.listPoreVersions(), Dtos::asDto);
      createArray(mapper, node, "spikeIns", librarySpikeInService.list(), Dtos::asDto);
      createArray(mapper, node, "attachmentCategories", attachmentCategoryService.list(), Dtos::asDto);
      createArray(mapper, node, "orderPurposes", orderPurposeService.list(), Dtos::asDto);
      createArray(mapper, node, "sampleSheetFormats", Arrays.asList(SampleSheet.values()), SampleSheet::name);

      Collection<IndexFamily> indexFamilies = indexService.getIndexFamilies();
      indexFamilies.add(IndexFamily.NULL);
      createArray(mapper, node, "indexFamilies", indexFamilies, Dtos::asDto);
      createArray(mapper, node, "qcTypes", qcService.listQcTypes(), Dtos::asDto);
      createArray(mapper, node, "qcTargets", Arrays.asList(QcTarget.values()), Dtos::asDto);
      createArray(mapper, node, "concentrationUnits", Arrays.asList(ConcentrationUnit.values()), Dtos::asDto);
      createArray(mapper, node, "volumeUnits", Arrays.asList(VolumeUnit.values()), Dtos::asDto);
      createArray(mapper, node, "partitionQcTypes", partitionQcTypeService.list(), Dtos::asDto);
      createArray(mapper, node, "referenceGenomes", referenceGenomeService.list(), Dtos::asDto);
      createArray(mapper, node, "spreadsheetFormats", Arrays.asList(SpreadSheetFormat.values()), Dtos::asDto);
      createArray(mapper, node, "sampleSpreadsheets", Arrays.asList(SampleSpreadSheets.values()), Dtos::asDto);
      createArray(mapper, node, "librarySpreadsheets", Arrays.asList(LibrarySpreadSheets.values()), Dtos::asDto);
      createArray(mapper, node, "libraryAliquotSpreadsheets", Arrays.asList(LibraryAliquotSpreadSheets.values()), Dtos::asDto);
      createArray(mapper, node, "poolSpreadsheets", Arrays.asList(PoolSpreadSheets.values()), Dtos::asDto);
      createArray(mapper, node, "partitionSpreadsheets", Arrays.asList(PartitionSpreadsheets.values()), Dtos::asDto);
      createArray(mapper, node, "workflows", Arrays.asList(WorkflowName.values()), Dtos::asDto);

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
      ArrayNode illuminaExperimentTypes = node.putArray("illuminaExperimentTypes");
      for (IlluminaExperiment experiment : IlluminaExperiment.values()) {
        ObjectNode dto = illuminaExperimentTypes.addObject();
        dto.put("name", experiment.name());
        dto.put("description", experiment.getDescription());
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
      ArrayNode healthTypes = node.putArray("healthTypes");
      for (HealthType status : HealthType.values()) {
        ObjectNode dto = healthTypes.addObject();
        dto.put("label", status.getKey());
        dto.put("allowedFromSequencer", status.isAllowedFromSequencer());
        dto.put("isDone", status.isDone());
      }
      ArrayNode illuminaWorkflowTypes = node.putArray("illuminaWorkflowTypes");
      for (IlluminaWorkflowType wf : IlluminaWorkflowType.values()) {
        ObjectNode dto = illuminaWorkflowTypes.addObject();
        dto.put("label", wf.getLabel());
        dto.put("value", wf.getRawValue());
      }
      ArrayNode illuminaChemistry = node.putArray("illuminaChemistry");
      for (IlluminaChemistry chemistry : IlluminaChemistry.values()) {
        illuminaChemistry.add(chemistry.name());
      }
      ArrayNode instrumentTypes = node.putArray("instrumentTypes");
      for (InstrumentType type : InstrumentType.values()) {
        ObjectNode dto = instrumentTypes.addObject();
        dto.put("label", type.getLabel());
        dto.put("value", type.name());
      }
      ArrayNode dataManglingPolicies = node.putArray("dataManglingPolicies");
      for (InstrumentDataManglingPolicy policy : InstrumentDataManglingPolicy.values()) {
        ObjectNode dto = dataManglingPolicies.addObject();
        dto.put("label", policy.getLabel());
        dto.put("value", policy.name());
      }

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

  @Scheduled(fixedDelay = 900_000)
  public synchronized void refreshConstants() {
    final ScheduledFuture<?> current = future;
    if (current != null && !current.isDone()) {
      current.cancel(false);
    }
    // This will wait a few seconds after a save of an institute default; if another save comes along, it will cancel and wait again. If
    // nothing saves again, it will rebuild the constants string.
    future = executor.schedule(this::rebuildConstants, 15, TimeUnit.SECONDS);
  }

  @GetMapping("/accessDenied")
  public ModelAndView showAccessDenied(ModelMap model) {
    return new ModelAndView("/WEB-INF/accessDenied.jsp", model);
  }

  @GetMapping("/error")
  public ModelAndView showError(ModelMap model) {
    return new ModelAndView("/WEB-INF/error.jsp", model);
  }
}
