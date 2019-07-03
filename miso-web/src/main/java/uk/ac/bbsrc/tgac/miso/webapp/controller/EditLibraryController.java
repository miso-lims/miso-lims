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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.AlphanumericComparator;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotSingleCellDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
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
@SessionAttributes("library")
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
    private static final String SORTABLE_LOCATION = "sortableLocation";
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
  }

  @Autowired
  private IndexService indexService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryTypeService libraryTypeService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private NamingScheme namingScheme;
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

  public NamingScheme getNamingScheme() {
    return namingScheme;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setIndexService(IndexService indexService) {
    this.indexService = indexService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.display.library.bulk.libraryalias}")
  private Boolean showLibraryAlias;
  @Value("${miso.display.library.bulk.description}")
  private Boolean showDescription;
  @Value("${miso.display.library.bulk.volume}")
  private Boolean showVolume;
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  public void addAdjacentLibraries(Library l, ModelMap model) throws IOException {
    if (l.getId() == AbstractLibrary.UNSAVED_ID) {
      return;
    }
    model.put("previousLibrary", libraryService.getAdjacentLibrary(l.getId(), true));
    model.put("nextLibrary", libraryService.getAdjacentLibrary(l.getId(), false));

  }

  /* HOT */
  @GetMapping(value = "indicesJson")
  public @ResponseBody JSONObject indicesJson(@RequestParam("indexFamily") String indexFamily, @RequestParam("position") String position)
      throws IOException {
    final JSONObject rtn = new JSONObject();
    List<JSONObject> rtnList = new ArrayList<>();
    try {
      if (!isStringEmptyOrNull(indexFamily)) {
        final IndexFamily ifam = indexService.getIndexFamilyByName(indexFamily);
        if (ifam != null) {
          rtnList = indicesForPosition(ifam, Integer.parseInt(position));
        }
      }
    } catch (Exception e) {
      log.error("Failed to get indices", e);
    }
    rtn.put("indices", rtnList);
    return rtn;
  }

  public List<JSONObject> indicesForPosition(IndexFamily ifam, int position) {
    final List<JSONObject> rtnList = new ArrayList<>();
    for (final Index index : ifam.getIndicesForPosition(position)) {
      final JSONObject obj = new JSONObject();
      obj.put("id", index.getId());
      obj.put("name", index.getName());
      obj.put("sequence", index.getSequence());
      obj.put("label", index.getLabel());
      rtnList.add(obj);
    }
    return rtnList;
  }

  /* HOT */
  @GetMapping(value = "libraryTypesJson")
  public @ResponseBody JSONObject libraryTypesJson(@RequestParam("platform") String platform) throws IOException {
    final JSONObject rtn = new JSONObject();
    final List<String> rtnLibTypes = new ArrayList<>();
    if (!isStringEmptyOrNull(platform)) {
      final Collection<LibraryType> libTypes = libraryTypeService.listByPlatform(PlatformType.get(platform));
      for (final LibraryType type : libTypes) {
        rtnLibTypes.add(type.getDescription());
      }
    }
    rtn.put("libraryTypes", rtnLibTypes);
    return rtn;
  }

  @GetMapping(value = "/{libraryId}")
  public ModelAndView setupForm(@PathVariable Long libraryId, ModelMap model) throws IOException {
    Library library = libraryService.get(libraryId);
    if (library == null) throw new NotFoundException("No library found for ID " + libraryId.toString());
    model.put("title", "Library " + library.getId());

    model.put("library", library);
    addAdjacentLibraries(library, model);

    Collection<Pool> pools = poolService.listByLibraryId(library.getId());
    model.put("libraryPools", pools.stream().map(p -> Dtos.asDto(p, false, false)).collect(Collectors.toList()));
    model.put("libraryRuns", pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("libraryAliquots", library.getLibraryAliquots().stream()
        .map(ldi -> Dtos.asDto(ldi, false, false)).collect(Collectors.toList()));
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode config = mapper.createObjectNode();
    config.putPOJO("library", Dtos.asDto(library, false));
    model.put("libraryAliquotsConfig", mapper.writeValueAsString(config));
    model.put("experiments", experimentService.listAllByLibraryId(library.getId()).stream().map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("libraryDto", mapper.writeValueAsString(Dtos.asDto(library, false)));

    if (LimsUtils.isDetailedLibrary(library)) {
      DetailedLibrary detailed = (DetailedLibrary) library;
      SampleIdentity identity = getParent(SampleIdentity.class, (DetailedSample) detailed.getSample());
      model.put("effectiveExternalNames", identity.getExternalName());
    }
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
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put(Config.SORTABLE_LOCATION, true);
      config.put(Config.PAGE_MODE, Config.EDIT);
      writeLibraryConfiguration(config);
    }
  };

  private static class LibraryBulkPropagateBackend extends BulkPropagateTableBackend<Sample, LibraryDto> {

    private final SampleService sampleService;
    private final LibraryTemplateService libraryTemplateService;
    private final Consumer<ObjectNode> additionalConfigFunction;
    private final BoxDto newBox;

    public LibraryBulkPropagateBackend(SampleService sampleService, LibraryTemplateService libraryTemplateService,
        Consumer<ObjectNode> additionalConfigFunction, BoxDto newBox) {
      super("library", LibraryDto.class, "Libraries", "Samples");
      this.sampleService = sampleService;
      this.libraryTemplateService = libraryTemplateService;
      this.additionalConfigFunction = additionalConfigFunction;
      this.newBox = newBox;
    }

    private Map<Long, List<LibraryTemplateDto>> templatesByProjectId;
    private String sort = null;

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
        Optional<DetailedSample> effective = sample.getEffectiveGroupIdSample();
        if (effective.isPresent()) {
          detailedDto.setEffectiveGroupId(effective.get().getGroupId());
          detailedDto.setEffectiveGroupIdSample(effective.get().getAlias());
        }
        dto = detailedDto;
      } else {
        dto = new LibraryDto();
      }
      dto.setParentSampleId(item.getId());
      dto.setParentSampleAlias(item.getAlias());
      dto.setParentSampleProjectId(item.getProject().getId());
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
            throw new IOException("Can only create libraries when samples all have the same class.");
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
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      additionalConfigFunction.accept(config);
      if (templatesByProjectId != null && !templatesByProjectId.isEmpty()) {
        config.putPOJO(Config.TEMPLATES, templatesByProjectId);
      }
      if (sort != null) {
        config.put(Config.SORT, sort);
      }
      config.putPOJO(Config.BOX, newBox);
      config.put(Config.PAGE_MODE, Config.PROPAGATE);
    }

    public ModelAndView propagate(String idString, String replicates, String sort, ModelMap model) throws IOException {
      this.sort = sort;
      return propagate(idString, replicates, model);
    }

  }

  private void writeLibraryConfiguration(ObjectNode config) {
    config.put(Config.SHOW_DESCRIPTION, showDescription);
    config.put(Config.SHOW_VOLUME, showVolume);
    config.put(Config.SHOW_LIBRARY_ALIAS, showLibraryAlias);
    config.put(Config.SORTABLE_LOCATION, true);
  }

  @GetMapping(value = "/bulk/propagate")
  public ModelAndView propagateFromSamples(@RequestParam("ids") String sampleIds, @RequestParam("replicates") String replicates,
      @RequestParam(name = "sort", required = false) String sort, @RequestParam(name = "boxId", required = false) Long boxId,
      ModelMap model) throws IOException {
    BoxDto newBox = boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null;
    return new LibraryBulkPropagateBackend(sampleService, libraryTemplateService, this::writeLibraryConfiguration, newBox)
        .propagate(sampleIds, replicates, sort, model);
  }

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editBulkLibraries(@RequestParam("ids") String libraryIds, ModelMap model) throws IOException {
    return libraryBulkEditBackend.edit(libraryIds, model);
  }

  @GetMapping(value = "/bulk/receive")
  public ModelAndView receiveBulkLibraries(@RequestParam("quantity") Integer quantity,
      @RequestParam(value = "sampleClassId", required = false) Long aliquotClassId,
      @RequestParam(value = "projectId", required = false) Long projectId,
      @RequestParam(value = "boxId", required = false) Long boxId,
      ModelMap model) throws IOException {

    LibraryDto libDto = null;
    Project project = null;
    if (projectId != null) {
      project = projectService.get(projectId);
      if (project == null) throw new InvalidParameterException("No project found for ID " + projectId.toString());
    }

    SampleClass aliquotClass = null;
    if (isDetailedSampleEnabled()) {
      if (aliquotClassId == null) throw new InvalidParameterException("Sample Class ID is required");
      aliquotClass = sampleClassService.get(aliquotClassId);
      if (aliquotClass == null) throw new InvalidParameterException("Requested sample class not found");
      DetailedLibraryDto detailedDto = new DetailedLibraryDto();
      libDto = detailedDto;
      SampleAliquotDto samDto = SampleAliquotSingleCell.SAMPLE_CLASS_NAME.equals(aliquotClass.getAlias()) ? new SampleAliquotSingleCellDto()
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

    return new BulkReceiveLibraryBackend(libDto, quantity, project, aliquotClass, defaultSciName, libraryTemplateService).create(model);
  }

  private final class BulkReceiveLibraryBackend extends BulkCreateTableBackend<LibraryDto> {

    private final Project project;
    private final SampleClass aliquotClass;
    private final String defaultSciName;
    private final BoxDto newBox;
    private final LibraryTemplateService libraryTemplateService;

    public BulkReceiveLibraryBackend(LibraryDto dto, Integer quantity, Project project, SampleClass aliquotClass, String defaultSciName,
        LibraryTemplateService libraryTemplateService) {
      super("libraryReceipt", LibraryDto.class, "Libraries", dto, quantity);
      if (isDetailedSampleEnabled() && aliquotClass == null) throw new InvalidParameterException("Aliquot class cannot be null");
      this.project = project;
      this.aliquotClass = aliquotClass;
      this.defaultSciName = defaultSciName;
      this.libraryTemplateService = libraryTemplateService;
      newBox = dto.getBox();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      if (aliquotClass != null) {
        config.putPOJO("targetSampleClass", Dtos.asDto(aliquotClass));
        config.put("dnaseTreatable", aliquotClass.hasPathToDnaseTreatable(sampleValidRelationshipService.getAll()));
      }
      config.put(Config.PAGE_MODE, Config.CREATE);
      config.put("hasProject", project != null);
      Map<Long, List<LibraryTemplateDto>> templatesByProjectId = new HashMap<>();
      if (project == null) {
        projectService.list().stream().map(Dtos::asDto).forEach(config.putArray("projects")::addPOJO);
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
      config.put(Config.SORTABLE_LOCATION, false);
      config.put(Config.PAGE_MODE, Config.CREATE);
      config.put(Config.IS_LIBRARY_RECEIPT, true);
      config.putPOJO(Config.BOX, newBox);
      config.putPOJO(Config.TEMPLATES, templatesByProjectId);
    }

  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("library") Library library, ModelMap model, SessionStatus session)
      throws IOException {
    if (library.getId() == AbstractLibrary.UNSAVED_ID) {
      libraryService.create(library);
    } else {
      libraryService.update(library);
    }

    session.setComplete();
    model.clear();
    return new ModelAndView("redirect:/miso/library/" + library.getId(), model);
  }

  @PostMapping(value = "/bulk/create")
  public String processBulkSubmit(@RequestBody JSONArray librariesDtos) throws IOException {
    if (librariesDtos != null && librariesDtos.size() > 0) {
      List<Long> savedLibraryIds = new ArrayList<>();

      for (Object lDto : librariesDtos) {
        ObjectMapper mapper = new ObjectMapper();
        LibraryDto libDto = mapper.readValue(lDto.toString(), LibraryDto.class);
        Library library = Dtos.to(libDto);

        Long savedId = libraryService.create(library);
        savedLibraryIds.add(savedId);
      }
      return "redirect:/miso/library/bulk/edit/" + savedLibraryIds.toString();
    } else {
      throw new IOException("There are no libraries to save!");
    }
  }
}
