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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotSingleCellDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleIdentityDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleSingleCellDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleSlideDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockSingleCellDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissuePieceDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueProcessingDto;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkCreateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;


@Controller
@RequestMapping("/sample")
@SessionAttributes("sample")
public class EditSampleController {

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ProjectService projectService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private StainService stainService;
  @Autowired
  private ArrayService arrayService;
  @Autowired
  private ArrayRunService arrayRunService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setSampleValidRelationshipService(SampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
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

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.defaults.sample.bulk.scientificname:}")
  private String defaultSciName;
  @Value("${miso.defaults.sample.lcmtube.groupid:}")
  private String defaultLcmTubeGroupId;
  @Value("${miso.defaults.sample.lcmtube.groupdescription:}")
  private String defaultLcmTubeGroupDesc;
  @Autowired
  private IndexChecker indexChecker;

  private Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  @ModelAttribute("defaultSciName")
  public String getDefaultSciName() {
    return defaultSciName != null ? defaultSciName : "";
  }

  private static class Config {
    private static final String PAGE_MODE = "pageMode";
    private static final String CREATE = "create";
    private static final String PROPAGATE = "propagate";
    private static final String EDIT = "edit";
    private static final String HAS_PROJECT = "hasProject";
    private static final String DNASE_TREATABLE = "dnaseTreatable";
    private static final String DEFAULT_SCI_NAME = "defaultSciName";
    private static final String DEFAULT_LCM_TUBE_GROUP_ID = "defaultLcmTubeGroupId";
    private static final String DEFAULT_LCM_TUBE_GROUP_DESC = "defaultLcmTubeGroupDescription";
    private static final String SOURCE_SAMPLE_CLASS = "sourceSampleClass";
    private static final String TARGET_SAMPLE_CLASS = "targetSampleClass";
    private static final String BOX = "box";
  }

  @ModelAttribute("stains")
  public List<Stain> populateStains() throws IOException {
    return stainService.list();
  }

  public Map<String, Sample> getAdjacentSamplesInProject(Sample s, @RequestParam(value = "projectId", required = false) Long projectId)
      throws IOException {
    Project p = s.getProject();
    Sample prevS = null;
    Sample nextS = null;

    if (p != null && p.getId() == projectId) {
      if (p.getSamples().isEmpty()) {
        // if p was lazy loaded then it doesn't have samples.
        p = projectService.get(p.getId());
      }
      if (!p.getSamples().isEmpty()) {
        Map<String, Sample> ret = new HashMap<>();
        List<Sample> ss = new ArrayList<>(p.getSamples());
        Collections.sort(ss, (a, b) -> Long.compare(a.getId(), b.getId()));
        for (int i = 0; i < ss.size(); i++) {
          if (ss.get(i).getId() == s.getId()) {
            if (i > 0) {
              prevS = ss.get(i - 1);
            }
            if (i < ss.size() - 2) {
              nextS = ss.get(i + 1);
            }
            break;
          }
        }
        ret.put("previousSample", prevS);
        ret.put("nextSample", nextS);
        return ret;
      }
    }
    return Collections.emptyMap();
  }

  @Autowired
  private SampleClassService sampleClassService;

  @GetMapping(value = "/{sampleId}")
  public ModelAndView setupForm(@PathVariable Long sampleId, ModelMap model) throws IOException {
    Sample sample = sampleService.get(sampleId);
    if (sample == null) throw new NotFoundException("No sample found for ID " + sampleId.toString());

    model.put("title", "Sample " + sampleId);

    Map<String, Sample> adjacentSamples = getAdjacentSamplesInProject(sample, sample.getProject().getId());
    if (!adjacentSamples.isEmpty()) {
      model.put("previousSample", adjacentSamples.get("previousSample"));
      model.put("nextSample", adjacentSamples.get("nextSample"));
    }

    model.put("sampleCategory",
        LimsUtils.isDetailedSample(sample) ? ((DetailedSample) sample).getSampleClass().getSampleCategory() : "plain");
    model.put("sampleLibraries", sample.getLibraries().stream().map(lib -> Dtos.asDto(lib, false)).collect(Collectors.toList()));
    Set<Pool> pools = sample.getLibraries().stream()
        .flatMap(WhineyFunction.flatRethrow(library -> poolService.listByLibraryId(library.getId())))
        .distinct().collect(Collectors.toSet());
    List<RunDto> runDtos = pools.stream().flatMap(WhineyFunction.flatRethrow(pool -> runService.listByPoolId(pool.getId())))
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("samplePools",
        pools.stream().map(p -> Dtos.asDto(p, false, false, indexChecker)).collect(Collectors.toList()));
    model.put("sampleRuns", runDtos);
    model.put("sampleRelations", getRelations(sample));
    addArrayData(sampleId, model);

    model.put("sampleTransfers", sample.getTransfers().stream()
        .map(TransferSample::getTransfer)
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    model.put("sample", sample);
    SampleDto sampleDto = Dtos.asDto(sample, false);
    setRelatedSlideDtos(sample, sampleDto);
    model.put("sampleDto", sample.getId() == SampleImpl.UNSAVED_ID ? "null" : mapper.writeValueAsString(sampleDto));

    if (LimsUtils.isTissuePieceSample(sample) || LimsUtils.isStockSample(sample)) {
      model.put("relatedSlides", getRelatedSlideDtos((DetailedSample) sample));
    }

    return new ModelAndView("/WEB-INF/pages/editSample.jsp", model);
  }

  private void setRelatedSlideDtos(Sample sample, SampleDto dto) {
    if (dto instanceof SampleTissuePieceDto) {
      ((SampleTissuePieceDto) dto).setRelatedSlides(getRelatedSlideDtos((DetailedSample) sample));
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
   * Used to edit samples with ids from given {sampleIds}.
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @GetMapping(value = "/bulk/edit")
  public ModelAndView editBulkSamples(@RequestParam("ids") String sampleIds, ModelMap model) throws IOException {
    return new BulkEditSampleBackend().edit(sampleIds, model);
  }

  /**
   * Used to create propagate new samples from existing samples (Detailed Sample only).
   * 
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @GetMapping(value = "/bulk/propagate")
  public ModelAndView propagateBulkSamples(@RequestParam("parentIds") String parentIds, @RequestParam("sampleClassId") Long sampleClassId,
      @RequestParam("replicates") String replicates, @RequestParam(value = "boxId", required = false) Long boxId, ModelMap model)
      throws IOException {
    Set<Group> recipientGroups = authorizationManager.getCurrentUser().getGroups();
    BulkPropagateSampleBackend bulkPropagateSampleBackend = new BulkPropagateSampleBackend(sampleClassService.get(sampleClassId),
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null), recipientGroups);
    return bulkPropagateSampleBackend.propagate(parentIds, replicates, model);
  }

  /**
   * Used to create new samples.
   * <ul>
   * <li>Detailed Sample: create new samples of a given sample class. Root identities will be found or created.</li>
   * <li>Plain Sample: create new samples.</li>
   * </ul>
   * Sends Dtos objects which will then be used for editing in grid.
   */
  @GetMapping(value = "/bulk/new")
  public ModelAndView createBulkSamples(@RequestParam("quantity") Integer quantity,
      @RequestParam(value = "sampleClassId", required = false) Long sampleClassId,
      @RequestParam(value = "projectId", required = false) Long projectId, @RequestParam(value = "boxId", required = false) Long boxId,
      ModelMap model) throws IOException {
    if (quantity == null || quantity <= 0) throw new RestException("Must specify quantity of samples to create", Status.BAD_REQUEST);

    final SampleDto template;
    final SampleClass target;

    if (sampleClassId != null) {
      // create new detailed samples
      target = sampleClassService.get(sampleClassId);
      if (target == null || target.getSampleCategory() == null) {
        throw new RestException("Cannot find sample class with ID " + sampleClassId, Status.NOT_FOUND);
      }
      template = getCorrectDetailedSampleDto(target);
    } else {
      if (detailedSample) throw new RestException("Must specify sample class of samples to create", Status.BAD_REQUEST);
      template = new SampleDto();
      target = null;
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

    return new BulkCreateSampleBackend(template.getClass(), template, quantity, project, target, recipientGroups).create(model);
  }

  private static DetailedSampleDto getCorrectDetailedSampleDto(SampleClass target) {
    // need to instantiate the correct DetailedSampleDto class to get the correct fields
    final DetailedSampleDto detailedTemplate;
    switch (target.getSampleCategory()) {
    case SampleIdentity.CATEGORY_NAME:
      detailedTemplate = new SampleIdentityDto();
      break;
    case SampleTissue.CATEGORY_NAME:
      detailedTemplate = new SampleTissueDto();
      break;
    case SampleTissueProcessing.CATEGORY_NAME:
      if (SampleSlide.SUBCATEGORY_NAME.equals(target.getSampleSubcategory())) {
        detailedTemplate = new SampleSlideDto();
      } else if (SampleTissuePiece.SUBCATEGORY_NAME.equals(target.getSampleSubcategory())) {
        detailedTemplate = new SampleTissuePieceDto();
      } else if (SampleSingleCell.SUBCATEGORY_NAME.equals(target.getSampleSubcategory())) {
        detailedTemplate = new SampleSingleCellDto();
      } else {
        detailedTemplate = new SampleTissueProcessingDto();
      }
      break;
    case SampleStock.CATEGORY_NAME:
      if (SampleStockSingleCell.SUBCATEGORY_NAME.equals(target.getSampleSubcategory())) {
        detailedTemplate = new SampleStockSingleCellDto();
      } else {
        detailedTemplate = new SampleStockDto();
      }
      break;
    case SampleAliquot.CATEGORY_NAME:
      if (SampleAliquotSingleCell.SUBCATEGORY_NAME.equals(target.getSampleSubcategory())) {
        detailedTemplate = new SampleAliquotSingleCellDto();
      } else {
        detailedTemplate = new SampleAliquotDto();
      }
      break;
    default:
      throw new RestException("Unknown category for sample class with ID " + target.getId(), Status.BAD_REQUEST);
    }
    detailedTemplate.setSampleClassId(target.getId());
    return detailedTemplate;
  }

  private final class BulkEditSampleBackend extends BulkEditTableBackend<Sample, SampleDto> {
    private SampleClass sampleClass = null;

    private BulkEditSampleBackend() {
      super("sample", SampleDto.class, "Samples");
    }

    @Override
    protected SampleDto asDto(Sample model) {
      SampleDto dto = Dtos.asDto(model, true);
      setRelatedSlideDtos(model, dto);
      return dto;
    }

    @Override
    protected Stream<Sample> load(List<Long> modelIds) throws IOException {
      List<Sample> results = (List<Sample>) sampleService.listByIdList(modelIds);
      for (Sample sample : results) {
        if (isDetailedSampleEnabled()) {
          if (sampleClass == null) {
            sampleClass = ((DetailedSample) sample).getSampleClass();
          } else if (((DetailedSample) sample).getSampleClass().getId() != sampleClass.getId()) {
            throw new IOException("Can only bulk edit samples when samples all have the same class.");
          }
        }
      }
      return results.stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      if (sampleClass != null) {
        config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(sampleClass));
        config.putPOJO(Config.SOURCE_SAMPLE_CLASS, Dtos.asDto(sampleClass));
        config.put(Config.DNASE_TREATABLE, sampleClass.getDNAseTreatable());
      } else {
        config.put(Config.DNASE_TREATABLE, false);
      }
      config.put(Config.PAGE_MODE, Config.EDIT);
    }
  }

  private final class BulkPropagateSampleBackend extends BulkPropagateTableBackend<Sample, SampleDto> {
    private SampleClass sourceSampleClass;
    private final SampleClass targetSampleClass;
    private final BoxDto newBox;
    private final Set<Group> recipientGroups;

    private BulkPropagateSampleBackend(SampleClass targetSampleClass, BoxDto newBox, Set<Group> recipientGroups) {
      super("sample", SampleDto.class, "Samples", "Samples");
      this.targetSampleClass = targetSampleClass;
      this.newBox = newBox;
      this.recipientGroups = recipientGroups;
    }

    @Override
    protected SampleDto createDtoFromParent(Sample item) {
      if (LimsUtils.isDetailedSample(item)) {
        DetailedSample sample = (DetailedSample) item;
        DetailedSampleDto dto = EditSampleController.getCorrectDetailedSampleDto(targetSampleClass);
        dto.setScientificName(sample.getScientificName());
        dto.setSampleType(sample.getSampleType());
        dto.setParentId(sample.getId());
        dto.setParentAlias(sample.getAlias());
        dto.setParentTissueSampleClassId(sample.getSampleClass().getId());
        dto.setProjectId(sample.getProject().getId());
        if (sample.getSubproject() != null) {
          dto.setSubprojectId(sample.getSubproject().getId());
          dto.setSubprojectAlias(sample.getSubproject().getAlias());
          dto.setSubprojectPriority(sample.getSubproject().getPriority());
        }
        dto.setGroupId(sample.getGroupId());
        dto.setGroupDescription(sample.getGroupDescription());
        sourceSampleClass = sample.getSampleClass();
        dto.setBox(newBox);
        if (targetSampleClass.getSampleCategory().equals(SampleStock.CATEGORY_NAME)
            || (targetSampleClass.getSampleCategory().equals(SampleTissueProcessing.CATEGORY_NAME)
                && targetSampleClass.getSampleSubcategory().equals(SampleTissuePiece.SUBCATEGORY_NAME))) {
          setRelatedSlideDtos(sample, dto);
        }
        return dto;
      } else {
        throw new IllegalArgumentException("Cannot create plain samples from other plain samples!");
      }
    }

    @Override
    protected Stream<Sample> loadParents(List<Long> parentIds) throws IOException {
      return sampleService.listByIdList(parentIds).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put(Config.PAGE_MODE, Config.PROPAGATE);
      config.put(Config.DNASE_TREATABLE, targetSampleClass.getDNAseTreatable());
      config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(targetSampleClass));
      config.putPOJO(Config.SOURCE_SAMPLE_CLASS, Dtos.asDto(sourceSampleClass));
      config.putPOJO(Config.BOX, newBox);
      config.put(Config.DEFAULT_LCM_TUBE_GROUP_ID, defaultLcmTubeGroupId);
      config.put(Config.DEFAULT_LCM_TUBE_GROUP_DESC, defaultLcmTubeGroupDesc);
      MisoWebUtils.addJsonArray(mapper, config, "recipientGroups", recipientGroups, Dtos::asDto);
    }
  }

  private final class BulkCreateSampleBackend extends BulkCreateTableBackend<SampleDto> {
    private final SampleClass targetSampleClass;
    private final Project project;
    private final BoxDto box;
    private final Set<Group> recipientGroups;

    public BulkCreateSampleBackend(Class<? extends SampleDto> dtoClass, SampleDto dto, Integer quantity, Project project,
        SampleClass sampleClass, Set<Group> recipientGroups) {
      super("sample", dtoClass, "Samples", dto, quantity);
      targetSampleClass = sampleClass;
      this.project = project;
      box = dto.getBox();
      this.recipientGroups = recipientGroups;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      if (targetSampleClass != null) {
        config.putPOJO(Config.TARGET_SAMPLE_CLASS, Dtos.asDto(targetSampleClass));
        config.put(Config.DNASE_TREATABLE, targetSampleClass.hasPathToDnaseTreatable(sampleValidRelationshipService.getAll()));
      } else {
        config.put(Config.DNASE_TREATABLE, false);
      }
      config.put(Config.PAGE_MODE, Config.CREATE);
      config.put(Config.HAS_PROJECT, project != null);
      config.put(Config.DEFAULT_LCM_TUBE_GROUP_ID, defaultLcmTubeGroupId);
      config.put(Config.DEFAULT_LCM_TUBE_GROUP_DESC, defaultLcmTubeGroupDesc);
      if (project == null) {
        projectService.list().stream().map(Dtos::asDto).forEach(config.putArray("projects")::addPOJO);
        config.put(Config.DEFAULT_SCI_NAME, defaultSciName);
      } else {
        config.putPOJO("project", Dtos.asDto(project));
        if (project.getReferenceGenome() != null && project.getReferenceGenome().getDefaultSciName() != null) {
          config.put(Config.DEFAULT_SCI_NAME, project.getReferenceGenome().getDefaultSciName());
        } else {
          config.put(Config.DEFAULT_SCI_NAME, defaultSciName);
        }
      }
      config.putPOJO(Config.BOX, box);
      MisoWebUtils.addJsonArray(mapper, config, "recipientGroups", recipientGroups, Dtos::asDto);
    }
  }

}
