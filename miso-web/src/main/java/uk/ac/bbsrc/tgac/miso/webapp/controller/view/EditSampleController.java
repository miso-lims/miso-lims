package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.ws.rs.BadRequestException;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.QcNodeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.NoteDto;
import uk.ac.bbsrc.tgac.miso.dto.ProbeDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleIdentityDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueProcessingDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;


@Controller
@RequestMapping("/sample")
public class EditSampleController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private ArrayService arrayService;
  @Autowired
  private ArrayRunService arrayRunService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private SopService sopService;
  @Autowired
  private QcNodeService qcNodeService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private ObjectMapper mapper;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setSampleClassService(SampleClassService sampleClassService) {
    this.sampleClassService = sampleClassService;
  }

  public RunService getRunService() {
    return runService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  private Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  private static class Config {
    private static final String PROJECTS = "projects";
    private static final String SOPS = "sops";
    private static final String DEFAULT_SCI_NAME = "defaultSciName";
    private static final String SOURCE_CATEGORY = "sourceCategory";
    private static final String TARGET_CATEGORY = "targetCategory";
    private static final String BOX = "box";
    private static final String RECIPIENT_GROUPS = "recipientGroups";
  }

  @GetMapping(value = "/{sampleId}")
  public ModelAndView setupForm(@PathVariable long sampleId, ModelMap model) throws IOException {
    Sample sample = retrieve(sampleId);

    model.put("title", "Sample " + sampleId);

    model.put("previousSample", sampleService.getPreviousInProject(sample));
    model.put("nextSample", sampleService.getNextInProject(sample));

    if (LimsUtils.isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      model.put("sampleCategory", detailed.getSampleClass().getSampleCategory());
      model.put("sampleSubcategory", detailed.getSampleClass().getSampleSubcategory());
    } else {
      model.put("sampleCategory", "plain");
    }
    List<LibraryDto> libraries = sample.isSaved()
        ? libraryService.listBySampleId(sample.getId()).stream().map(lib -> Dtos.asDto(lib, false))
            .collect(Collectors.toList())
        : Collections.emptyList();
    model.put("sampleLibraries", libraries);
    Set<Pool> pools = libraries.stream()
        .flatMap(WhineyFunction.flatRethrow(library -> poolService.listByLibraryId(library.getId())))
        .distinct().collect(Collectors.toSet());
    List<RunDto> runDtos =
        pools.stream().flatMap(WhineyFunction.flatRethrow(pool -> runService.listByPoolId(pool.getId())))
            .map(Dtos::asDto)
            .collect(Collectors.toList());
    model.put("samplePools",
        pools.stream().map(p -> Dtos.asDto(p, false, false, indexChecker)).collect(Collectors.toList()));
    model.put("sampleRuns", runDtos);
    model.put("sampleRelations", getRelations(sample));
    addArrayData(sampleId, model);

    model.put("sampleTransfers", sample.getTransferViews().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    model.put("sample", sample);
    SampleDto sampleDto = Dtos.asDto(sample, false, libraries.size());
    setRelatedSlideDtos(sample, sampleDto);
    model.put("sampleDto", !sample.isSaved() ? "null" : mapper.writeValueAsString(sampleDto));
    model.put("notes", collectNotes(sample));

    ObjectNode formConfig = mapper.createObjectNode();
    formConfig.put("detailedSample", isDetailedSampleEnabled());
    MisoWebUtils.addJsonArray(mapper, formConfig, "projects", projectService.list(),
        Dtos::asDto);
    MisoWebUtils.addJsonArray(mapper, formConfig, Config.SOPS, sopService.listByCategory(SopCategory.SAMPLE),
        Dtos::asDto);
    model.put("formConfig", mapper.writeValueAsString(formConfig));

    return new ModelAndView("/WEB-INF/pages/editSample.jsp", model);
  }

  private static List<NoteDto> collectNotes(Sample sample) {
    List<NoteDto> notes = new ArrayList<>();
    for (Sample current = sample; current != null; current = current.getParent()) {
      final Sample currentSample = current;
      current.getNotes().stream().map(note -> NoteDto.from(note, currentSample)).forEach(notes::add);
    }
    return notes;
  }

  private void setRelatedSlideDtos(Sample sample, SampleDto dto) {
    if (dto instanceof SampleTissueProcessingDto) {
      ((SampleTissueProcessingDto) dto).setRelatedSlides(getRelatedSlideDtos((DetailedSample) sample));
    } else if (dto instanceof SampleStockDto) {
      ((SampleStockDto) dto).setRelatedSlides(getRelatedSlideDtos((DetailedSample) sample));
    }
  }

  private List<SampleDto> getRelatedSlideDtos(DetailedSample sample) {
    DetailedSample tissue = LimsUtils.isTissueSample(sample) ? sample : LimsUtils.getParent(SampleTissue.class, sample);
    if (tissue == null) {
      return Collections.emptyList();
    }
    return getSlideChildren(tissue).stream().map(Dtos::asMinimalDto).collect(Collectors.toList());
  }

  private List<DetailedSample> getSlideChildren(DetailedSample parent) {
    List<DetailedSample> slides = new ArrayList<>();
    for (DetailedSample child : parent.getChildren()) {
      if (LimsUtils.isSampleSlide(child)) {
        slides.add(child);
      } else if (!LimsUtils.isStockSample(child)) {
        slides.addAll(getSlideChildren(child));
      }
    }
    return slides;
  }

  private void addArrayData(long sampleId, ModelMap model) throws IOException {
    model.put("sampleArrays", arrayService.listBySampleId(sampleId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
    model.put("sampleArrayRuns", arrayRunService.listBySampleId(sampleId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));
  }

  private List<SampleDto> getRelations(Sample sample) {
    List<SampleDto> relations = new ArrayList<>();
    if (LimsUtils.isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      for (DetailedSample parent = detailed.getParent(); parent != null; parent = parent.getParent()) {
        relations.add(0, Dtos.asDto(LimsUtils.deproxify(parent), false));
      }
      addChildren(relations, detailed.getChildren());
    }
    return relations;
  }

  private void addChildren(List<SampleDto> relations, Collection<DetailedSample> children) {
    for (DetailedSample child : children) {
      relations.add(Dtos.asDto(LimsUtils.deproxify(child), false));
      addChildren(relations, child.getChildren());
    }
  }

  /**
   * Used to edit samples with ids from given {sampleIds}. Sends Dtos objects which will then be used
   * for editing in grid.
   */
  @PostMapping(value = "/bulk/edit")
  public ModelAndView editBulkSamples(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String sampleIds = getStringInput("ids", form, true);
    return new BulkEditSampleBackend(mapper).edit(sampleIds, model);
  }

  /**
   * Used to create propagate new samples from existing samples (Detailed Sample only).
   * 
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @PostMapping(value = "/bulk/propagate")
  public ModelAndView propagateBulkSamples(@RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    String parentIds = getStringInput("parentIds", form, true);
    String replicates = getStringInput("replicates", form, true);
    String targetCategory = getStringInput("targetCategory", form, isDetailedSampleEnabled());
    Long boxId = getLongInput("boxId", form, false);

    if (isDetailedSampleEnabled()) {
      confirmClassesExist(targetCategory);
    }

    Set<Group> recipientGroups = authorizationManager.getCurrentUser().getGroups();
    BulkPropagateSampleBackend bulkPropagateSampleBackend = new BulkPropagateSampleBackend(targetCategory,
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null), recipientGroups, mapper);
    return bulkPropagateSampleBackend.propagate(parentIds, replicates, model);
  }

  /**
   * Used to create new samples.
   * <ul>
   * <li>Detailed Sample: create new samples of a given sample class. Root identities will be found or
   * created.</li>
   * <li>Plain Sample: create new samples.</li>
   * </ul>
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @PostMapping(value = "/bulk/new")
  public ModelAndView createBulkSamples(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    Integer quantity = getIntegerInput("quantity", form, true);
    String targetCategory = getStringInput("targetCategory", form, isDetailedSampleEnabled());
    Long projectId = getLongInput("projectId", form, false);
    Long boxId = getLongInput("boxId", form, false);

    if (quantity == null || quantity <= 0)
      throw new ClientErrorException("Must specify quantity of samples to create");

    final SampleDto template;

    if (isDetailedSampleEnabled()) {
      // create new detailed samples
      confirmClassesExist(targetCategory);
      template = getCorrectDetailedSampleDto(targetCategory);
    } else {
      template = new SampleDto();
    }
    final Project project;
    if (projectId == null) {
      project = null;
    } else {
      project = projectService.get(projectId);
      template.setProjectId(projectId);
    }

    if (boxId != null) {
      template.setBox(Dtos.asDto(boxService.get(boxId), true));
    }

    Set<Group> recipientGroups = authorizationManager.getCurrentUser().getGroups();

    return new BulkCreateSampleBackend(template.getClass(), template, quantity, project, targetCategory,
        recipientGroups, mapper).create(model);
  }

  private void confirmClassesExist(String targetCategory) throws IOException {
    List<SampleClass> categoryClasses = sampleClassService.listByCategory(targetCategory);
    if (categoryClasses.isEmpty()) {
      throw new ClientErrorException(String.format("No classes available for category '%s'", targetCategory));
    }
  }

  private static DetailedSampleDto getCorrectDetailedSampleDto(String sampleCategory) {
    // need to instantiate the correct DetailedSampleDto class to get the correct fields
    switch (sampleCategory) {
      case SampleIdentity.CATEGORY_NAME:
        return new SampleIdentityDto();
      case SampleTissue.CATEGORY_NAME:
        return new SampleTissueDto();
      case SampleTissueProcessing.CATEGORY_NAME:
        return new SampleTissueProcessingDto();
      case SampleStock.CATEGORY_NAME:
        return new SampleStockDto();
      case SampleAliquot.CATEGORY_NAME:
        return new SampleAliquotDto();
      default:
        throw new ClientErrorException("Unknown sample category : " + sampleCategory);
    }
  }

  private final class BulkEditSampleBackend extends BulkEditTableBackend<Sample, SampleDto> {
    private String targetCategory = null;

    private BulkEditSampleBackend(ObjectMapper mapper) {
      super("sample", SampleDto.class, "Samples", mapper);
    }

    @Override
    protected SampleDto asDto(Sample model) {
      int libraryCount;
      try {
        libraryCount = libraryService.listBySampleId(model.getId()).size();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      SampleDto dto = Dtos.asDto(model, true, libraryCount);
      setRelatedSlideDtos(model, dto);
      return dto;
    }

    @Override
    protected Stream<Sample> load(List<Long> modelIds) throws IOException {
      List<Sample> results = sampleService.listByIdList(modelIds);
      for (Sample sample : results) {
        if (isDetailedSampleEnabled()) {
          if (targetCategory == null) {
            targetCategory = ((DetailedSample) sample).getSampleClass().getSampleCategory();
          } else if (!targetCategory.equals(((DetailedSample) sample).getSampleClass().getSampleCategory())) {
            throw new IOException("Can only bulk edit samples when samples all have the same category.");
          }
        }
      }
      return results.stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put(Config.TARGET_CATEGORY, targetCategory);
      addJsonArray(mapper, config, Config.PROJECTS, projectService.list(), Dtos::asDto);
      addJsonArray(mapper, config, Config.SOPS, sopService.listByCategory(SopCategory.SAMPLE), Dtos::asDto);
    }
  }

  private final class BulkPropagateSampleBackend extends BulkPropagateTableBackend<Sample, SampleDto> {
    private String sourceCategory;
    private final String targetCategory;
    private final BoxDto newBox;
    private final Set<Group> recipientGroups;

    private BulkPropagateSampleBackend(String targetCategory, BoxDto newBox, Set<Group> recipientGroups,
        ObjectMapper mapper) {
      super("sample", SampleDto.class, "Samples", "Samples", mapper);
      this.targetCategory = targetCategory;
      this.newBox = newBox;
      this.recipientGroups = recipientGroups;
    }

    @Override
    protected SampleDto createDtoFromParent(Sample item) {
      if (LimsUtils.isDetailedSample(item)) {
        DetailedSample sample = (DetailedSample) item;
        DetailedSampleDto dto = EditSampleController.getCorrectDetailedSampleDto(targetCategory);
        if (sample.getScientificName() != null) {
          dto.setScientificNameId(sample.getScientificName().getId());
        }
        dto.setParentId(sample.getId());
        dto.setParentName(sample.getName());
        dto.setParentAlias(sample.getAlias());
        if (sample.getBox() != null) {
          dto.setParentBoxPosition(sample.getBoxPosition());
          dto.setParentBoxPositionLabel(
              BoxUtils.makeBoxPositionLabel(sample.getBox().getAlias(), sample.getBoxPosition()));
        }
        dto.setParentSampleClassId(sample.getSampleClass().getId());
        dto.setProjectId(sample.getProject().getId());
        if (sample.getIdentityAttributes() != null && sample.getIdentityAttributes().getConsentLevel() != null) {
          dto.setIdentityConsentLevel(sample.getIdentityAttributes().getConsentLevel().getLabel());
        }
        if (sample.getSubproject() != null) {
          dto.setSubprojectId(sample.getSubproject().getId());
          dto.setSubprojectAlias(sample.getSubproject().getAlias());
          dto.setSubprojectPriority(sample.getSubproject().getPriority());
        }
        dto.setGroupId(sample.getGroupId());
        dto.setGroupDescription(sample.getGroupDescription());
        dto.setBox(newBox);
        if (SampleTissue.CATEGORY_NAME.equals(targetCategory) && LimsUtils.isTissueSample(sample)) {
          ((SampleTissueDto) dto).setTimepoint(((SampleTissue) item).getTimepoint());
        } else if (SampleStock.CATEGORY_NAME.equals(targetCategory)
            || (SampleTissueProcessing.CATEGORY_NAME.equals(targetCategory))) {
          setRelatedSlideDtos(sample, dto);
        }
        Requisition requisition = getEffectiveRequisition(item);
        if (requisition != null) {
          dto.setEffectiveRequisitionId(requisition.getId());
          dto.setEffectiveRequisitionAlias((requisition.getAlias()));
          dto.setRequisitionAssayIds(requisition.getAssays().stream().map(Assay::getId).toList());
        }
        return dto;
      } else {
        throw new IllegalArgumentException("Cannot create plain samples from other plain samples!");
      }
    }

    @Override
    protected Stream<Sample> loadParents(List<Long> parentIds) throws IOException {
      List<Sample> parents = sampleService.listByIdList(parentIds);
      if (isDetailedSampleEnabled()) {
        for (Sample parent : parents) {
          DetailedSample detailed = (DetailedSample) parent;
          if (sourceCategory == null) {
            sourceCategory = detailed.getSampleClass().getSampleCategory();
          } else if (!sourceCategory.equals(detailed.getSampleClass().getSampleCategory())) {
            throw new ClientErrorException("Parents must all be of the same sample category");
          }
        }
        sourceCategory = ((DetailedSample) parents.get(0)).getSampleClass().getSampleCategory();
      }
      return parents.stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put(Config.SOURCE_CATEGORY, sourceCategory);
      config.put(Config.TARGET_CATEGORY, targetCategory);
      config.putPOJO(Config.BOX, newBox);
      addJsonArray(mapper, config, Config.RECIPIENT_GROUPS, recipientGroups, Dtos::asDto);
      addJsonArray(mapper, config, Config.PROJECTS, projectService.list(), Dtos::asDto);
      addJsonArray(mapper, config, Config.SOPS, sopService.listByCategory(SopCategory.SAMPLE), Dtos::asDto);
    }
  }

  private final class BulkCreateSampleBackend extends BulkCreateTableBackend<SampleDto> {
    private final String targetCategory;
    private final Project project;
    private final BoxDto box;
    private final Set<Group> recipientGroups;

    public BulkCreateSampleBackend(Class<? extends SampleDto> dtoClass, SampleDto dto, Integer quantity,
        Project project,
        String targetCategory, Set<Group> recipientGroups, ObjectMapper mapper) {
      super("sample", dtoClass, "Samples", dto, quantity, mapper);
      this.targetCategory = targetCategory;
      this.project = project;
      box = dto.getBox();
      this.recipientGroups = recipientGroups;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put(Config.TARGET_CATEGORY, targetCategory);
      addJsonArray(mapper, config, Config.PROJECTS, projectService.list(), project -> Dtos.asDto(project, true));
      config.put(Config.DEFAULT_SCI_NAME, defaultSciName);
      if (project != null) {
        config.putPOJO("project", Dtos.asDto(project, true));
      }
      config.putPOJO(Config.BOX, box);
      addJsonArray(mapper, config, "recipientGroups", recipientGroups, Dtos::asDto);
      addJsonArray(mapper, config, Config.SOPS, sopService.listByCategory(SopCategory.SAMPLE), Dtos::asDto);
    }
  }

  @GetMapping("/{id}/qc-hierarchy")
  public ModelAndView getQcHierarchy(@PathVariable long id, ModelMap model) throws IOException {
    return MisoWebUtils.getQcHierarchy("Sample", id, qcNodeService::getForSample, model, mapper);
  }

  private static final class BulkEditProbesBackend extends BulkTableBackend<ProbeDto> {

    private final Sample sample;

    public BulkEditProbesBackend(Sample sample, ObjectMapper mapper) {
      super("probe", ProbeDto.class, mapper);
      this.sample = sample;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("sampleId", sample.getId());
    }

    public ModelAndView edit(int additionalProbes, ModelMap model) throws IOException {
      if (!isProcessingSingleCellSample(sample)) {
        throw new BadRequestException("Sample %d is not a tissue processing sample".formatted(sample.getId()));
      }
      SampleSingleCell singleCell = (SampleSingleCell) sample;
      List<ProbeDto> dtos = singleCell.getProbes() == null ? new ArrayList<>()
          : singleCell.getProbes().stream().map(ProbeDto::from).collect(Collectors.toCollection(ArrayList::new));
      for (int i = 0; i < additionalProbes; i++) {
        dtos.add(new ProbeDto());
      }
      return prepare(model, PageMode.EDIT, "Edit Sample Probes", dtos);
    }

  }

  @GetMapping("/{sampleId}/probes")
  public ModelAndView getBulkEditProbesPage(@PathVariable long sampleId,
      @RequestParam(required = false) Integer addProbes, ModelMap model) throws IOException {
    Sample sample = retrieve(sampleId);
    return new BulkEditProbesBackend(sample, mapper).edit(addProbes, model);
  }

  private Sample retrieve(long sampleId) throws IOException {
    Sample sample = sampleService.get(sampleId);
    if (sample == null) {
      throw new NotFoundException("No sample found for ID " + sampleId);
    }
    return sample;
  }

}
