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
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity.DonorSex;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryDilutionSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibrarySpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PoolSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SpreadSheetFormat;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.DilutionFactor;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PlatformDto;
import uk.ac.bbsrc.tgac.miso.dto.WritableUrls;
import uk.ac.bbsrc.tgac.miso.integration.util.SignatureHelper;
import uk.ac.bbsrc.tgac.miso.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PartitionQCService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.StainService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;

import io.prometheus.client.Gauge;

@Controller
public class MenuController implements ServletContextAware {
  protected static final Logger log = LoggerFactory.getLogger(MenuController.class);

  private static final Gauge constantsTimestamp = Gauge
      .build("miso_constants_timestamp", "The epoch time of the last build of the constants.js file.").register();

  private String constantsJs;
  private long constantsJsTime;

  ServletContext servletContext;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private KitService kitService;
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
  private LibraryService libraryService;
  @Autowired
  private LibraryDesignService libraryDesignService;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;
  @Autowired
  private PlatformService platformService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private SampleGroupService sampleGroupService;
  @Autowired
  private SamplePurposeService samplePurposeService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private QualityControlService qcService;
  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private InstrumentService sequencerService;
  @Autowired
  private PartitionQCService partitionQCService;
  @Autowired
  private StudyService studyService;
  @Autowired
  private ContainerModelService containerModelService;
  @Autowired
  private AttachmentCategoryService attachmentCategoryService;

  @Autowired
  private NamingScheme namingScheme;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @Resource
  private Boolean boxScannerEnabled;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @RequestMapping("/myAccount")
  public ModelAndView myAccountMenu(ModelMap model) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      String realName = user.getFullName();
      StringBuilder groups = new StringBuilder();
      for (String role : user.getRoles()) {
        groups.append(role.replaceAll("ROLE_", "") + "&nbsp;");
      }
      model.put("title", "My Account");
      model.put("userRealName", realName);
      model.put("userId", user.getUserId());
      model.put("apiKey", SignatureHelper.generatePrivateUserKey((user.getLoginName() + "::" + user.getPassword()).getBytes("UTF-8")));
      model.put("userGroups", groups.toString());
      return new ModelAndView("/pages/myAccount.jsp", model);
    } catch (IOException e) {
      log.error("my account menu", e);
      return new ModelAndView("/login.jsp", model);
    } catch (NoSuchAlgorithmException e) {
      log.error("my account menu", e);
      return new ModelAndView("/login.jsp", model);
    }
  }

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping("/mainMenu")
  public ModelAndView mainMenu(ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    model.put("title", "Home");
    ObjectMapper mapper = new ObjectMapper();
    model.put("favouriteWorkflows",
        user.getFavouriteWorkflows().stream().map(Dtos::asDto).map(dto -> mapper.valueToTree(dto)).collect(Collectors.toList()));
    return new ModelAndView("/pages/mainMenu.jsp", model);
  }

  @Override
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  private static <Model, Dto> void createArray(ObjectMapper mapper, URI baseUri, ObjectNode node, String name,
      Iterable<Model> items,
      Function<Model, Dto> asDto) {
    ArrayNode array = node.putArray(name);
    for (Model item : items) {
      Dto dto = asDto.apply(item);
      if (dto instanceof WritableUrls) {
        ((WritableUrls) dto).writeUrls(baseUri);
      }
      JsonNode itemNode = mapper.valueToTree(dto);
      array.add(itemNode);
    }
  }

  @RequestMapping(path = "/constants.js")
  @ResponseBody
  public ResponseEntity<String> constantsScript(HttpServletResponse response, final UriComponentsBuilder uriBuilder) throws IOException {
    response.setContentType("application/javascript");
    // Use a cached copy and only update every
    if (constantsJs != null && (System.currentTimeMillis() - constantsJsTime) < 15 * 60 * 1000) {
      return ResponseEntity.ok().cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES)).body(constantsJs);
    }
    URI baseUri = uriBuilder.build().toUri();
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode node = mapper.createObjectNode();
    node.put("isDetailedSample", detailedSample);
    node.put("automaticBarcodes", autoGenerateIdentificationBarcodes());
    node.put("automaticSampleAlias", namingScheme.hasSampleAliasGenerator());
    node.put("automaticLibraryAlias", namingScheme.hasLibraryAliasGenerator());
    node.put("boxScannerEnabled", boxScannerEnabled);

    final Collection<SampleValidRelationship> relationships = sampleValidRelationshipService.getAll();

    createArray(mapper, baseUri, node, "libraryDesigns", libraryDesignService.list(), Dtos::asDto);
    createArray(mapper, baseUri, node, "libraryTypes", libraryService.listLibraryTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "librarySelections", libraryService.listLibrarySelectionTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "libraryStrategies", libraryService.listLibraryStrategyTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "libraryDesignCodes", libraryDesignCodeService.list(), Dtos::asDto);
    Set<Long> activePlatforms = sequencerService.list().stream().filter(Instrument::isActive)
        .map(sequencer -> sequencer.getPlatform().getId())
        .collect(Collectors.toSet());
    createArray(mapper, baseUri, node, "platforms", platformService.list(), platform -> {
      PlatformDto dto = Dtos.asDto(platform);
      dto.setActive(activePlatforms.contains(platform.getId()));
      return dto;
    });
    createArray(mapper, baseUri, node, "kitDescriptors", kitService.listKitDescriptors(), Dtos::asDto);
    createArray(mapper, baseUri, node, "sampleClasses", sampleClassService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "sampleValidRelationships", relationships, Dtos::asDto);
    createArray(mapper, baseUri, node, "detailedQcStatuses", detailedQcStatusService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "sampleGroups", sampleGroupService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "subprojects", subprojectService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "labs", labService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "tissueOrigins", tissueOriginService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "tissueTypes", tissueTypeService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "tissueMaterials", tissueMaterialService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "stains", stainService.list(), Dtos::asDto);
    createArray(mapper, baseUri, node, "targetedSequencings", targetedSequencingService.list(), Dtos::asDto);
    createArray(mapper, baseUri, node, "samplePurposes", samplePurposeService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "sequencingParameters", sequencingParametersService.getAll(), Dtos::asDto);
    createArray(mapper, baseUri, node, "printerBackends", Arrays.asList(Backend.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "printerDrivers", Arrays.asList(Driver.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "boxSizes", boxService.listSizes(), Function.identity());
    createArray(mapper, baseUri, node, "boxUses", boxService.listUses(), Function.identity());
    createArray(mapper, baseUri, node, "studyTypes", studyService.listTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "sampleCategories", SampleClass.CATEGORIES, Function.identity());
    createArray(mapper, baseUri, node, "submissionAction", Arrays.asList(SubmissionActionType.values()), SubmissionActionType::name);
    createArray(mapper, baseUri, node, "containerModels", containerModelService.list(), Dtos::asDto);
    createArray(mapper, baseUri, node, "spikeIns", libraryService.listSpikeIns(), Dtos::asDto);
    createArray(mapper, baseUri, node, "attachmentCategories", attachmentCategoryService.list(), Dtos::asDto);

    Collection<IndexFamily> indexFamilies = indexService.getIndexFamilies();
    indexFamilies.add(IndexFamily.NULL);
    createArray(mapper, baseUri, node, "indexFamilies", indexFamilies, Dtos::asDto);
    createArray(mapper, baseUri, node, "qcTypes", qcService.listQcTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "qcTargets", Arrays.asList(QcTarget.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "concentrationUnits", Arrays.asList(ConcentrationUnit.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "volumeUnits", Arrays.asList(VolumeUnit.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "partitionQcTypes", partitionQCService.listTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "referenceGenomes", referenceGenomeService.listAllReferenceGenomeTypes(), Dtos::asDto);
    createArray(mapper, baseUri, node, "spreadsheetFormats", Arrays.asList(SpreadSheetFormat.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "sampleSpreadsheets", Arrays.asList(SampleSpreadSheets.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "librarySpreadsheets", Arrays.asList(LibrarySpreadSheets.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "libraryDilutionSpreadsheets", Arrays.asList(LibraryDilutionSpreadSheets.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "poolSpreadsheets", Arrays.asList(PoolSpreadSheets.values()), Dtos::asDto);
    createArray(mapper, baseUri, node, "workflows", Arrays.asList(WorkflowName.values()), Dtos::asDto);

    ArrayNode platformTypes = node.putArray("platformTypes");
    Collection<PlatformType> activePlatformTypes = platformService.listActivePlatformTypes();
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
    for (String sampleType : sampleService.listSampleTypes()) {
      sampleTypes.add(sampleType);
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

    // Save the regenerated file in cache. This has a race condition where multiple concurrent requests could results in regenerating this
    // file and updating the cache. Since the cache is two variables (data and time), they can also be torn. Given the nature of the cached
    // data, we don't really care since the results are probably the same and jitter of a few seconds is a small error in cache time.
    constantsJs = "Constants = " + mapper.writeValueAsString(node) + ";";
    constantsJsTime = System.currentTimeMillis();
    constantsTimestamp.set(constantsJsTime / 1000.0);
    return ResponseEntity.ok().cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES)).body(constantsJs);
  }

  public void refreshConstants() {
    constantsJsTime = 0;
  }
}
