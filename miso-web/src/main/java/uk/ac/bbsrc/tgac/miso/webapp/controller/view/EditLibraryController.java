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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.getParent;
import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryBatch;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.AlphanumericComparator;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryBatchDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotSingleCellDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping("/library")
public class EditLibraryController {
  protected static final Logger log = LoggerFactory.getLogger(EditLibraryController.class);

  private static final IndexFamily INDEX_FAMILY_NEEDS_PLATFORM = new IndexFamily();

  protected static final Comparator<LibraryAliquot> LIBRARY_ALIQUOT_COMPARATOR = (a, b) -> {
    int nameComparison = AlphanumericComparator.INSTANCE.compare(a.getName(), b.getName());
    return nameComparison == 0 ? new AliasComparator<>().compare(a.getLibrary(), b.getLibrary()) : nameComparison;
  };

  static {
    INDEX_FAMILY_NEEDS_PLATFORM.setName("Please select a platform...");
  }

  private static class Config {
    private static final String IS_LIBRARY_RECEIPT = "isLibraryReceipt";
    private static final String DEFAULT_SCI_NAME = "defaultSciName";
    private static final String PAGE_MODE = "pageMode";
    private static final String CREATE = "create";
    private static final String PROPAGATE = "propagate";
    private static final String EDIT = "edit";
    private static final String SHOW_LIBRARY_ALIAS = "showLibraryAlias";
    private static final String SHOW_DESCRIPTION = "showDescription";
    private static final String SHOW_VOLUME = "showVolume";
    private static final String TEMPLATES = "templatesByProjectId";
    private static final String SORT = "sort";
    private static final String BOX = "box";
    private static final String SAMPLE_ALIAS_MAYBE_REQUIRED = "sampleAliasMaybeRequired";
    private static final String LIBRARY_ALIAS_MAYBE_REQUIRED = "libraryAliasMaybeRequired";
  }

  @Autowired
  private LibraryService libraryService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private RunService runService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private LibraryTemplateService libraryTemplateService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private WorkstationService workstationService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private SopService sopService;
  @Autowired
  private KitDescriptorService kitDescriptorService;
  @Autowired
  private UserService userService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.display.library.bulk.libraryalias:true}")
  private Boolean showLibraryAlias;
  @Value("${miso.display.library.bulk.description:true}")
  private Boolean showDescription;
  @Value("${miso.display.library.bulk.volume:true}")
  private Boolean showVolume;
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;

  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  public void addAdjacentLibraries(Library library, ModelMap model) throws IOException {
    if (library.getId() == LibraryImpl.UNSAVED_ID) {
      return;
    }
    model.put("previousLibrary", libraryService.getAdjacentLibrary(library, true));
    model.put("nextLibrary", libraryService.getAdjacentLibrary(library, false));

  }

  @GetMapping(value = "/{libraryId}")
  public ModelAndView setupForm(@PathVariable Long libraryId, ModelMap model) throws IOException {
    Library library = libraryService.get(libraryId);
    if (library == null) throw new NotFoundException("No library found for ID " + libraryId.toString());
    model.put("title", "Library " + library.getId());

    model.put("library", library);
    addAdjacentLibraries(library, model);

    Collection<Pool> pools = poolService.listByLibraryId(library.getId());
    model.put("libraryPools",
        pools.stream().map(p -> Dtos.asDto(p, false, false, indexChecker)).collect(Collectors.toList()));
    model.put("libraryRuns", pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("libraryAliquots", library.getLibraryAliquots().stream()
        .map(ldi -> Dtos.asDto(ldi, false)).collect(Collectors.toList()));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    config.putPOJO("library", Dtos.asDto(library, false));
    model.put("libraryAliquotsConfig", mapper.writeValueAsString(config));
    model.put("experiments",
        experimentService.listAllByLibraryId(library.getId()).stream().map(exp -> Dtos.asDto(exp))
        .collect(Collectors.toList()));
    model.put("libraryDto", mapper.writeValueAsString(Dtos.asDto(library, false)));

    if (LimsUtils.isDetailedLibrary(library)) {
      DetailedLibrary detailed = (DetailedLibrary) library;
      SampleIdentity identity = getParent(SampleIdentity.class, (DetailedSample) detailed.getSample());
      model.put("effectiveExternalNames", identity.getExternalName());
    }
    model.put("libraryTransfers", library.getTransferViews().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    ObjectNode formConfig = mapper.createObjectNode();
    formConfig.put("detailedSample", isDetailedSampleEnabled());
    addJsonArray(mapper, formConfig, "workstations", workstationService.list(), Dtos::asDto);
    addJsonArray(mapper, formConfig, "thermalCyclers", instrumentService.listByType(InstrumentType.THERMAL_CYCLER),
        Dtos::asDto);
    addJsonArray(mapper, formConfig, "sops", sopService.listByCategory(SopCategory.LIBRARY), Dtos::asDto);
    model.put("formConfig", mapper.writeValueAsString(formConfig));

    return new ModelAndView("/WEB-INF/pages/editLibrary.jsp", model);
  }

  private final BulkEditTableBackend<Library, LibraryDto> libraryBulkEditBackend = new BulkEditTableBackend<Library, LibraryDto>(
      "library", LibraryDto.class, "Libraries") {

    @Override
    protected Stream<Library> load(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected LibraryDto asDto(Library model) {
      return Dtos.asDto(model, true);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put(Config.PAGE_MODE, Config.EDIT);
      config.put(Config.SAMPLE_ALIAS_MAYBE_REQUIRED, !alwaysGenerateSampleAliases());
      config.put(Config.LIBRARY_ALIAS_MAYBE_REQUIRED, !alwaysGenerateLibraryAliases());
      addJsonArray(mapper, config, "workstations", workstationService.list(), Dtos::asDto);
      addJsonArray(mapper, config, "thermalCyclers", instrumentService.listByType(InstrumentType.THERMAL_CYCLER),
          Dtos::asDto);
      addJsonArray(mapper, config, "sops", sopService.listByCategory(SopCategory.LIBRARY), Dtos::asDto);

      config.put(Config.SHOW_DESCRIPTION, showDescription);
      config.put(Config.SHOW_VOLUME, showVolume);
      config.put(Config.SHOW_LIBRARY_ALIAS, showLibraryAlias);
    }

    @Override
    protected boolean isNewInterface() {
      return true;
    }
  };

  private boolean alwaysGenerateSampleAliases() {
    return namingSchemeHolder.getPrimary().hasSampleAliasGenerator()
        && (namingSchemeHolder.getSecondary() == null || namingSchemeHolder.getSecondary().hasSampleAliasGenerator());
  }

  private boolean alwaysGenerateLibraryAliases() {
    return namingSchemeHolder.getPrimary().hasLibraryAliasGenerator()
        && (namingSchemeHolder.getSecondary() == null || namingSchemeHolder.getSecondary().hasLibraryAliasGenerator());
  }

  private final class LibraryBulkPropagateBackend extends BulkPropagateTableBackend<Sample, LibraryDto> {

    private final BoxDto newBox;
    private final String sort;

    public LibraryBulkPropagateBackend(BoxDto newBox, String sort) {
      super("library", LibraryDto.class, "Libraries", "Samples");
      this.newBox = newBox;
      this.sort = sort;
    }

    private Map<Long, List<LibraryTemplateDto>> templatesByProjectId;

    @Override
    protected LibraryDto createDtoFromParent(Sample item) {
      LibraryDto dto;
      if (LimsUtils.isDetailedSample(item)) {
        DetailedSample sample = (DetailedSample) item;
        DetailedLibraryDto detailedDto = new DetailedLibraryDto();
        detailedDto.setParentSampleClassId(sample.getSampleClass().getId());
        detailedDto.setNonStandardAlias(sample.hasNonStandardAlias());
        if (sample.getBox() != null) {
          detailedDto.setSampleBoxPositionLabel(BoxUtils.makeBoxPositionLabel(sample.getBox().getAlias(), sample.getBoxPosition()));
        }
        GroupIdentifiable effective = sample.getEffectiveGroupIdEntity();
        if (effective != null) {
          detailedDto.setEffectiveGroupId(effective.getGroupId());
          detailedDto.setEffectiveGroupIdSample(effective.getAlias());
        }
        dto = detailedDto;
      } else {
        dto = new LibraryDto();
      }
      dto.setParentSampleId(item.getId());
      dto.setParentSampleAlias(item.getAlias());
      dto.setProjectId(item.getProject().getId());
      dto.setBox(newBox);
      return dto;
    }

    @Override
    protected Stream<Sample> loadParents(List<Long> ids) throws IOException {
      Collection<Sample> results = sampleService.listByIdList(ids);

      // load templates
      templatesByProjectId = results.stream()
          .map(sam -> sam.getProject().getId())
          .distinct()
          .map(WhineyFunction.rethrow(projectId -> {
            Map<Long, List<LibraryTemplateDto>> map = new HashMap<>();
            map.put(projectId, Dtos.asLibraryTemplateDtos(libraryTemplateService.listLibraryTemplatesForProject(projectId)));
            return map;
          }))
          .filter(map -> !map.values().stream().allMatch(value -> value.isEmpty()))
          .flatMap(map -> map.entrySet().stream())
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      SampleClass sampleClass = null;
      boolean hasPlain = false;
      for (Sample sample : results) {
        if (sample instanceof DetailedSample) {
          DetailedSample detailed = (DetailedSample) sample;
          if (sampleClass == null) {
            sampleClass = detailed.getSampleClass();
          } else if (sampleClass.getId() != detailed.getSampleClass().getId()) {
            throw new ClientErrorException("Can only create libraries when samples all have the same class.");
          }
        } else {
          hasPlain = true;
        }
      }
      if (hasPlain && sampleClass != null) {
        throw new IOException("Cannot mix plain and detailed samples.");
      }
      return results.stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      if (templatesByProjectId != null && !templatesByProjectId.isEmpty()) {
        config.putPOJO(Config.TEMPLATES, templatesByProjectId);
      }
      if (sort != null) {
        config.put(Config.SORT, sort);
      }
      config.putPOJO(Config.BOX, newBox);
      config.put(Config.PAGE_MODE, Config.PROPAGATE);
      config.put(Config.SAMPLE_ALIAS_MAYBE_REQUIRED, !alwaysGenerateSampleAliases());
      config.put(Config.LIBRARY_ALIAS_MAYBE_REQUIRED, !alwaysGenerateLibraryAliases());
      config.put(Config.SHOW_DESCRIPTION, showDescription);
      config.put(Config.SHOW_VOLUME, showVolume);
      config.put(Config.SHOW_LIBRARY_ALIAS, showLibraryAlias);
      addJsonArray(mapper, config, "workstations", workstationService.list(), Dtos::asDto);
      addJsonArray(mapper, config, "thermalCyclers", instrumentService.listByType(InstrumentType.THERMAL_CYCLER),
          Dtos::asDto);
      addJsonArray(mapper, config, "sops", sopService.listByCategory(SopCategory.LIBRARY), Dtos::asDto);
    }

    @Override
    protected boolean isNewInterface() {
      return true;
    }

  }

  @PostMapping(value = "/bulk/propagate")
  public ModelAndView propagateFromSamples(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String sampleIds = getStringInput("ids", form, true);
    String replicates = getStringInput("replicates", form, true);
    String sort = getStringInput("sort", form, false);
    Long boxId = getLongInput("boxId", form, false);

    BoxDto newBox = boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null;
    return new LibraryBulkPropagateBackend(newBox, sort)
        .propagate(sampleIds, replicates, model);
  }

  @PostMapping(value = "/bulk/edit")
  public ModelAndView editBulkLibraries(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String libraryIds = getStringInput("ids", form, true);
    return libraryBulkEditBackend.edit(libraryIds, model);
  }

  @PostMapping(value = "/bulk/receive")
  public ModelAndView receiveBulkLibraries(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    Integer quantity = getIntegerInput("quantity", form, true);
    Long aliquotClassId = getLongInput("sampleClassId", form, isDetailedSampleEnabled());
    Long projectId = getLongInput("projectId", form, false);
    Long boxId = getLongInput("boxId", form, false);

    LibraryDto libDto = null;
    Project project = null;
    if (projectId != null) {
      project = projectService.get(projectId);
      if (project == null) throw new ClientErrorException("No project found for ID " + projectId);
    }

    SampleClass aliquotClass = null;
    if (isDetailedSampleEnabled()) {
      aliquotClass = sampleClassService.get(aliquotClassId);
      if (aliquotClass == null) throw new ClientErrorException("No sample class found for ID " + aliquotClassId);
      DetailedLibraryDto detailedDto = new DetailedLibraryDto();
      libDto = detailedDto;
      SampleAliquotDto samDto = SampleAliquotSingleCell.SUBCATEGORY_NAME.equals(aliquotClass.getAlias()) ? new SampleAliquotSingleCellDto()
          : new SampleAliquotDto();
      detailedDto.setSample(samDto);
      samDto.setSampleClassId(aliquotClassId);
      detailedDto.setParentSampleClassId(aliquotClassId);
    } else {
      libDto = new LibraryDto();
      libDto.setSample(new SampleDto());
    }
    if (boxId != null) {
      libDto.setBox(Dtos.asDto(boxService.get(boxId), true));
    }

    Set<Group> recipientGroups = authorizationManager.getCurrentUser().getGroups();

    return new BulkReceiveLibraryBackend(libDto, quantity, project, aliquotClass, defaultSciName, libraryTemplateService, recipientGroups)
        .create(model);
  }

  private final class BulkReceiveLibraryBackend extends BulkCreateTableBackend<LibraryDto> {

    private final Project project;
    private final SampleClass aliquotClass;
    private final String defaultSciName;
    private final BoxDto newBox;
    private final LibraryTemplateService libraryTemplateService;
    private final Set<Group> recipientGroups;

    public BulkReceiveLibraryBackend(LibraryDto dto, Integer quantity, Project project, SampleClass aliquotClass, String defaultSciName,
        LibraryTemplateService libraryTemplateService, Set<Group> recipientGroups) {
      super("library", LibraryDto.class, "Libraries", dto, quantity);
      if (isDetailedSampleEnabled() && aliquotClass == null) throw new InvalidParameterException("Aliquot class cannot be null");
      this.project = project;
      this.aliquotClass = aliquotClass;
      this.defaultSciName = defaultSciName;
      this.libraryTemplateService = libraryTemplateService;
      this.recipientGroups = recipientGroups;
      newBox = dto.getBox();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      if (aliquotClass != null) {
        config.putPOJO("targetSampleClass", Dtos.asDto(aliquotClass));
        config.put("dnaseTreatable", aliquotClass.hasPathToDnaseTreatable(sampleValidRelationshipService.getAll()));
      }
      config.put(Config.PAGE_MODE, Config.CREATE);
      Map<Long, List<LibraryTemplateDto>> templatesByProjectId = new HashMap<>();
      addJsonArray(mapper, config, "projects", projectService.list(), Dtos::asDto);
      if (project == null) {
        List<LibraryTemplate> templates = libraryTemplateService.list();
        for (LibraryTemplate template : templates) {
          LibraryTemplateDto dto = Dtos.asDto(template);
          for (Project tempProject : template.getProjects()) {
            if (!templatesByProjectId.containsKey(tempProject.getId())) {
              templatesByProjectId.put(tempProject.getId(), new ArrayList<>());
            }
            templatesByProjectId.get(tempProject.getId()).add(dto);
          }
        }
      } else {
        config.putPOJO("project", Dtos.asDto(project));
        templatesByProjectId.put(project.getId(),
            Dtos.asLibraryTemplateDtos(libraryTemplateService.listLibraryTemplatesForProject(project.getId())));
      }
      config.put(Config.DEFAULT_SCI_NAME, defaultSciName);
      config.put(Config.SHOW_DESCRIPTION, showDescription);
      config.put(Config.SHOW_VOLUME, showVolume);
      config.put(Config.SHOW_LIBRARY_ALIAS, showLibraryAlias);
      config.put(Config.PAGE_MODE, Config.CREATE);
      config.put(Config.IS_LIBRARY_RECEIPT, true);
      config.putPOJO(Config.BOX, newBox);
      config.putPOJO(Config.TEMPLATES, templatesByProjectId);
      config.put(Config.SAMPLE_ALIAS_MAYBE_REQUIRED, !alwaysGenerateSampleAliases());
      config.put(Config.LIBRARY_ALIAS_MAYBE_REQUIRED, !alwaysGenerateLibraryAliases());
      addJsonArray(mapper, config, "recipientGroups", recipientGroups, Dtos::asDto);
    }

    @Override
    protected boolean isNewInterface() {
      return true;
    }

  }

  @GetMapping("/batch/{batchId:.+}")
  public ModelAndView getBatchPage(@PathVariable String batchId, ModelMap model) throws IOException {
    LibraryBatch batch = null;
    try {
      batch = new LibraryBatch(batchId);
    } catch (IllegalArgumentException e) {
      throw new ClientErrorException("Invalid batch ID");
    }
    User user = userService.get(batch.getUserId());
    Sop sop = sopService.get(batch.getSopId());
    KitDescriptor kit = kitDescriptorService.get(batch.getKitId());
    if (user == null || sop == null || kit == null || kit.getKitType() != KitType.LIBRARY) {
      throw new ClientErrorException("Invalid batch ID");
    }
    LibraryBatchDto batchDto = Dtos.asDto(batch);
    batchDto.setUsername(user.getLoginName());
    batchDto.setSopLabel(sop.getAlias() + "v." + sop.getVersion());
    batchDto.setSopUrl(sop.getUrl());
    batchDto.setKitName(kit.getName());
    
    ObjectMapper mapper = new ObjectMapper();
    model.put("batchId", StringEscapeUtils.escapeJavaScript(batchId));
    model.put("batchDto", mapper.writeValueAsString(batchDto));

    return new ModelAndView("/WEB-INF/pages/editLibraryBatch.jsp", model);
  }


}
