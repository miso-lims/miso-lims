package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.GroupIdentifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.QcNodeService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.AlphanumericComparator;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedLibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkMergeTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/libraryaliquot")
public class EditLibraryAliquotController {

  protected static final Comparator<LibraryAliquot> LIBRARY_ALIQUOT_COMPARATOR = (a, b) -> {
    int nameComparison = AlphanumericComparator.INSTANCE.compare(a.getName(), b.getName());
    return nameComparison == 0 ? new AliasComparator<>().compare(a.getLibrary(), b.getLibrary()) : nameComparison;
  };

  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private QcNodeService qcNodeService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/{aliquotId}")
  public ModelAndView edit(ModelMap model, @PathVariable long aliquotId) throws IOException {
    LibraryAliquot aliquot = libraryAliquotService.get(aliquotId);
    if (aliquot == null) {
      throw new NotFoundException("Library aliquot not found");
    }
    model.put("title", "Library Aliquot " + aliquot.getId());
    model.put("aliquot", aliquot);
    model.put("aliquotDto", mapper.writeValueAsString(Dtos.asDto(aliquot, false)));
    List<Pool> pools = poolService.listByLibraryAliquotId(aliquotId);
    model.put("aliquotPools",
        pools.stream().map(p -> Dtos.asDto(p, false, false, indexChecker)).collect(Collectors.toList()));
    model.put("aliquotRuns",
        pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
            .collect(Collectors.toList()));

    model.put("aliquotTransfers", aliquot.getTransferViews().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    return new ModelAndView("/WEB-INF/pages/editLibraryAliquot.jsp", model);
  }

  private final class BulkPropagateLibraryBackend extends BulkPropagateTableBackend<Library, LibraryAliquotDto> {

    private final BoxDto newBox;
    private final Map<Long, Long> defaultTargetedSequencingByProject = new HashMap<>();

    private BulkPropagateLibraryBackend(BoxDto newBox) {
      super("libraryaliquot", LibraryAliquotDto.class, "Library Aliquots", "Libraries", mapper);
      this.newBox = newBox;
    }

    @Override
    protected LibraryAliquotDto createDtoFromParent(Library item) {
      LibraryAliquotDto dto = null;
      if (LimsUtils.isDetailedLibrary(item)) {
        DetailedLibraryAliquotDto detailed = new DetailedLibraryAliquotDto();
        DetailedLibrary detailedLibrary = (DetailedLibrary) item;
        detailed.setNonStandardAlias(detailedLibrary.hasNonStandardAlias());
        detailed.setLibraryDesignCodeId(detailedLibrary.getLibraryDesignCode().getId());
        ParentTissueAttributes tissue = ((DetailedSample) detailedLibrary.getSample()).getTissueAttributes();
        setEffectiveAttributes(detailed, detailedLibrary, tissue);

        dto = detailed;
      } else {
        dto = new LibraryAliquotDto();
      }
      dto.setDnaSize(item.getDnaSize());
      dto.setLibraryId(item.getId());
      dto.setLibraryName(item.getName());
      dto.setLibraryAlias(item.getAlias());
      dto.setParentName(item.getName());
      dto.setParentVolume(item.getVolume() == null ? null : item.getVolume().toString());
      if (item.getBox() != null) {
        dto.setParentBoxPosition(item.getBoxPosition());
        dto.setParentBoxPositionLabel(BoxUtils.makeBoxPositionLabel(item.getBox().getAlias(), item.getBoxPosition()));
      }
      dto.setBox(newBox);
      if (item.getConcentration() != null) {
        dto.setConcentration(LimsUtils.toNiceString(item.getConcentration()));
        dto.setConcentrationUnits(item.getConcentrationUnits());
      }
      dto.setLibraryPlatformType(item.getPlatformType().getKey());
      Sample sample = item.getSample();
      Project project = sample.getProject();
      dto.setProjectId(project.getId());
      dto.setProjectName(project.getName());
      dto.setProjectCode(project.getCode());
      if (project.getDefaultTargetedSequencing() != null) {
        defaultTargetedSequencingByProject.put(project.getId(), project.getDefaultTargetedSequencing().getId());
      }

      Requisition requisition = sample.getRequisition() == null ? getParentRequisition(sample)
          : sample.getRequisition();
      if (requisition != null && requisition.getAssay() != null) {
        dto.setRequisitionAssayId(requisition.getAssay().getId());
      }
      return dto;
    }

    @Override
    protected Stream<Library> loadParents(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
      config.set("defaultTargetedSequencingByProject", mapper.valueToTree(defaultTargetedSequencingByProject));
    }
  }

  protected static void setEffectiveAttributes(DetailedLibraryAliquotDto aliquot, GroupIdentifiable parent,
      ParentTissueAttributes tissue) {
    aliquot.setEffectiveTissueOriginAlias(tissue.getTissueOrigin().getAlias());
    aliquot.setEffectiveTissueOriginDescription(tissue.getTissueOrigin().getDescription());
    aliquot.setEffectiveTissueTypeAlias(tissue.getTissueType().getAlias());
    aliquot.setEffectiveTissueTypeDescription(tissue.getTissueType().getDescription());

    GroupIdentifiable effective = parent.getEffectiveGroupIdEntity();
    if (effective != null) {
      aliquot.setEffectiveGroupId(effective.getGroupId());
      aliquot.setEffectiveGroupIdSample(effective.getAlias());
    }
  }

  private final class BulkPropagateAliquotBackend extends BulkPropagateTableBackend<LibraryAliquot, LibraryAliquotDto> {

    private final BoxDto newBox;
    private final Map<Long, Long> defaultTargetedSequencingByProject = new HashMap<>();

    private BulkPropagateAliquotBackend(BoxDto newBox) {
      super("libraryaliquot", LibraryAliquotDto.class, "Library Aliquots", "Library Aliquots", mapper);
      this.newBox = newBox;
    }

    @Override
    protected LibraryAliquotDto createDtoFromParent(LibraryAliquot item) {
      LibraryAliquotDto dto = null;
      if (LimsUtils.isDetailedLibraryAliquot(item)) {
        DetailedLibraryAliquotDto detailed = new DetailedLibraryAliquotDto();
        DetailedLibraryAliquot detailedParent = (DetailedLibraryAliquot) item;
        detailed.setNonStandardAlias(detailedParent.isNonStandardAlias());
        detailed.setLibraryDesignCodeId(detailedParent.getLibraryDesignCode().getId());
        ParentTissueAttributes tissue =
            ((DetailedSample) detailedParent.getLibrary().getSample()).getTissueAttributes();
        setEffectiveAttributes(detailed, detailedParent, tissue);

        dto = detailed;
      } else {
        dto = new LibraryAliquotDto();
      }
      dto.setDnaSize(item.getDnaSize());
      dto.setParentAliquotId(item.getId());
      dto.setParentAliquotAlias(item.getAlias());
      dto.setLibraryId(item.getLibrary().getId());
      dto.setLibraryName(item.getLibrary().getName());
      dto.setLibraryAlias(item.getLibrary().getAlias());
      dto.setParentName(item.getName());
      dto.setParentVolume(item.getVolume() == null ? null : item.getVolume().toString());
      if (item.getBox() != null) {
        dto.setParentBoxPosition(item.getBoxPosition());
        dto.setParentBoxPositionLabel(BoxUtils.makeBoxPositionLabel(item.getBox().getAlias(), item.getBoxPosition()));
      }
      dto.setBox(newBox);
      if (item.getConcentration() != null) {
        dto.setConcentration(LimsUtils.toNiceString(item.getConcentration()));
        dto.setConcentrationUnits(item.getConcentrationUnits());
      }
      dto.setLibraryPlatformType(item.getLibrary().getPlatformType().getKey());
      Sample sample = item.getLibrary().getSample();
      Project project = sample.getProject();
      dto.setProjectId(project.getId());
      dto.setProjectName(project.getName());
      dto.setProjectCode(project.getCode());
      if (project.getDefaultTargetedSequencing() != null) {
        defaultTargetedSequencingByProject.put(project.getId(), project.getDefaultTargetedSequencing().getId());
      }

      Requisition requisition = sample.getRequisition() == null ? getParentRequisition(sample)
          : sample.getRequisition();
      if (requisition != null && requisition.getAssay() != null) {
        dto.setRequisitionAssayId(requisition.getAssay().getId());
      }
      return dto;
    }

    @Override
    protected Stream<LibraryAliquot> loadParents(List<Long> ids) throws IOException {
      return libraryAliquotService.listByIdList(ids).stream();
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
      config.set("defaultTargetedSequencingByProject", mapper.valueToTree(defaultTargetedSequencingByProject));
    }
  }

  @PostMapping(value = "/bulk/propagate")
  public ModelAndView propagate(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String libraryIds = getStringInput("ids", form, true);
    Long boxId = getLongInput("boxId", form, false);

    BulkPropagateLibraryBackend bulkPropagateBackend = new BulkPropagateLibraryBackend(
        boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null);
    return bulkPropagateBackend.propagate(libraryIds, model);
  }

  @PostMapping(value = "/bulk/repropagate")
  public ModelAndView repropagate(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String aliquotIds = getStringInput("ids", form, true);
    Long boxId = getLongInput("boxId", form, false);

    BulkPropagateAliquotBackend bulkPropagateBackend = new BulkPropagateAliquotBackend(
        boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null);
    return bulkPropagateBackend.propagate(aliquotIds, model);
  }

  private class BulkEditBackend extends BulkEditTableBackend<LibraryAliquot, LibraryAliquotDto> {

    public BulkEditBackend() {
      super("libraryaliquot", LibraryAliquotDto.class, "Library Aliquots", mapper);
    }

    @Override
    protected LibraryAliquotDto asDto(LibraryAliquot model) {
      return Dtos.asDto(model, true);
    }

    @Override
    protected Stream<LibraryAliquot> load(List<Long> modelIds) throws IOException {
      return libraryAliquotService.listByIdList(modelIds).stream().sorted(LIBRARY_ALIQUOT_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      // no config required
    }
  };

  @PostMapping(value = "/bulk/edit")
  public ModelAndView bulkEdit(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String ids = getStringInput("ids", form, true);
    return new BulkEditBackend().edit(ids, model);
  }

  private final class BulkMergeBackend extends BulkMergeTableBackend<PoolDto> {

    private final BoxDto newBox;

    private BulkMergeBackend(BoxDto newBox) {
      super("pool", PoolDto.class, "Pools", "Library Aliquots", mapper);
      this.newBox = newBox;
    }

    @Override
    protected PoolDto createDtoFromParents(List<Long> ids) throws IOException {
      List<LibraryAliquot> parents = libraryAliquotService.listByIdList(ids);
      if (parents.isEmpty()) {
        throw new IllegalStateException("Cannot have no library aliquots for pool propagation.");
      }
      List<PlatformType> platformTypes = parents.stream()
          .map(aliquot -> aliquot.getLibrary().getPlatformType())
          .distinct()
          .collect(Collectors.toList());
      if (platformTypes.size() > 1) {
        throw new IllegalArgumentException("Cannot create a pool for multiple platforms: "
            + String.join(", ", platformTypes.stream().map(Enum::name).toArray(CharSequence[]::new)));
      }
      PoolDto dto = new PoolDto();
      dto.setPlatformType(platformTypes.get(0).name());

      if (parents.size() == 1) {
        dto.setAlias(parents.get(0).getLibrary().getAlias() + "_POOL");
      } else {
        String commonPrefix = LimsUtils
            .findCommonPrefix(parents.stream().map(LibraryAliquot::getAlias).toArray(String[]::new));
        if (commonPrefix != null) {
          dto.setAlias(commonPrefix + "_POOL");
        }
      }
      dto.setPooledElements(parents.stream().map(ldi -> Dtos.asDto(ldi, false)).collect(Collectors.toSet()));
      if (dto.getPooledElements().stream().allMatch(element -> element.getVolumeUsed() != null)) {
        dto.setVolume(
            Double.toString(dto.getPooledElements().stream()
                .mapToDouble(element -> Double.parseDouble(element.getVolumeUsed())).sum()));
      }

      dto.setBox(newBox);

      return dto;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
    }
  }

  @PostMapping(value = "/bulk/merge")
  public ModelAndView propagatePoolsMerged(@RequestParam Map<String, String> form, ModelMap model) throws IOException {
    String aliquotIds = getStringInput("ids", form, true);
    Long boxId = getLongInput("boxId", form, false);
    BulkMergeBackend bulkMergeBackend = new BulkMergeBackend(
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null));
    return bulkMergeBackend.propagate(aliquotIds, model);
  }

  private static class BulkCustomPoolTableBackend extends BulkTableBackend<PoolDto> {

    private final int poolQuantity;
    private final List<LibraryAliquotDto> aliquots;
    private final PlatformType platformType;
    private final BoxDto newBox;

    public BulkCustomPoolTableBackend(int poolQuantity, String idString, LibraryAliquotService libraryAliquotService,
        BoxDto newBox, ObjectMapper mapper) throws IOException {
      super("pool", PoolDto.class, mapper);
      this.poolQuantity = poolQuantity;
      List<LibraryAliquot> ldis = libraryAliquotService.listByIdList(parseIds(idString));
      List<PlatformType> platformTypes = ldis.stream()
          .map(aliquot -> aliquot.getLibrary().getPlatformType())
          .distinct()
          .collect(Collectors.toList());
      if (platformTypes.size() > 1) {
        throw new IllegalArgumentException("Cannot create a pool for multiple platforms: "
            + String.join(", ", platformTypes.stream().map(Enum::name).toArray(CharSequence[]::new)));
      }
      this.aliquots = ldis.stream().map(ldi -> Dtos.asDto(ldi, false)).collect(Collectors.toList());
      this.platformType = platformTypes.get(0);
      this.newBox = newBox;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      aliquots.forEach(config.putArray("aliquotsToPool")::addPOJO);
      config.putPOJO("box", newBox);
    }

    public ModelAndView create(ModelMap model) throws IOException {
      PoolDto dto = new PoolDto();
      dto.setPlatformType(this.platformType.name());
      dto.setBox(newBox);
      return prepare(model, PageMode.CREATE, "Create Pools from Library Aliquots",
          Collections.nCopies(poolQuantity, dto));
    }

  }

  @PostMapping(value = "/bulk/pool")
  public ModelAndView propagatePoolsCustom(@RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    String aliquotIds = getStringInput("ids", form, true);
    int poolQuantity = getIntegerInput("quantity", form, true);
    Long boxId = getLongInput("boxId", form, false);
    BulkCustomPoolTableBackend bulkCustomPoolTableBackend = new BulkCustomPoolTableBackend(poolQuantity, aliquotIds,
        libraryAliquotService, (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null), mapper);
    return bulkCustomPoolTableBackend.create(model);
  }

  private final class BulkPropagateBackend extends BulkPropagateTableBackend<LibraryAliquot, PoolDto> {
    private final BoxDto newBox;

    private BulkPropagateBackend(BoxDto newBox) {
      super("pool", PoolDto.class, "Pools", "Library Aliquots", mapper);
      this.newBox = newBox;
    }

    @Override
    protected PoolDto createDtoFromParent(LibraryAliquot item) {
      PoolDto dto = new PoolDto();
      dto.setAlias(item.getLibrary().getAlias() + "_POOL");
      dto.setPooledElements(Collections.singleton(Dtos.asDto(item, false)));
      dto.setPlatformType(item.getLibrary().getPlatformType().name());
      if (item.getVolumeUsed() != null) {
        dto.setVolume(LimsUtils.toNiceString(item.getVolumeUsed()));
        dto.setVolumeUnits(item.getVolumeUnits());
      }
      dto.setBox(newBox);
      if (item.getConcentration() != null) {
        dto.setConcentration(LimsUtils.toNiceString(item.getConcentration()));
        dto.setConcentrationUnits(item.getConcentrationUnits());
      }
      return dto;
    }

    @Override
    protected Stream<LibraryAliquot> loadParents(List<Long> ids) throws IOException {
      return libraryAliquotService.listByIdList(ids).stream().sorted(LIBRARY_ALIQUOT_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
    }
  }

  @PostMapping(value = "/bulk/pool-separate")
  public ModelAndView propagatePoolsIndividual(@RequestParam Map<String, String> form, ModelMap model)
      throws IOException {
    String aliquotIds = getStringInput("ids", form, true);
    Long boxId = getLongInput("boxId", form, false);
    BulkPropagateBackend bulkPropagateBackend = new BulkPropagateBackend(
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null));
    return bulkPropagateBackend.propagate(aliquotIds, model);
  }

  @GetMapping("/{id}/qc-hierarchy")
  public ModelAndView getQcHierarchy(@PathVariable long id, ModelMap model) throws IOException {
    return MisoWebUtils.getQcHierarchy("Library Aliquot", id, qcNodeService::getForLibraryAliquot, model, mapper);
  }

}
