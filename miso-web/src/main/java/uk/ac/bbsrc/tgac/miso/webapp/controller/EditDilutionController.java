package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.parseIds;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.AlphanumericComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkMergeTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkPropagateTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;

@Controller
@RequestMapping("/dilutions")
public class EditDilutionController {

  protected static final Comparator<LibraryDilution> DILUTION_COMPARATOR = (a, b) -> {
    int nameComparison = AlphanumericComparator.INSTANCE.compare(a.getName(), b.getName());
    return nameComparison == 0 ? new AliasComparator<>().compare(a.getLibrary(), b.getLibrary()) : nameComparison;
  };

  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private BoxService boxService;

  @GetMapping("/{dilutionId}")
  public ModelAndView editDilution(ModelMap model, @PathVariable long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    if (dilution == null) {
      throw new NotFoundException("Dilution not found");
    }

    ObjectMapper mapper = new ObjectMapper();
    model.put("dilution", dilution);
    model.put("dilutionDto", mapper.writeValueAsString(Dtos.asDto(dilution, false, false)));
    List<Pool> pools = poolService.listByDilutionId(dilutionId);
    model.put("dilutionPools",
        pools.stream().map(p -> Dtos.asDto(p, false, false)).collect(Collectors.toList()));
    model.put("dilutionRuns", pools.stream().flatMap(WhineyFunction.flatRethrow(p -> runService.listByPoolId(p.getId()))).map(Dtos::asDto)
        .collect(Collectors.toList()));

    return new ModelAndView("/WEB-INF/pages/editDilution.jsp", model);
  }

  private final class BulkPropagateLibraryBackend extends BulkPropagateTableBackend<Library, DilutionDto> {

    private final BoxDto newBox;

    private BulkPropagateLibraryBackend(BoxDto newBox) {
      super("dilution", DilutionDto.class, "Dilutions", "Libraries");
      this.newBox = newBox;
    }

    @Override
    protected DilutionDto createDtoFromParent(Library item) {
      DilutionDto dto = new DilutionDto();
      dto.setLibrary(Dtos.asDto(item, false));
      if (item.getSample().getProject().getDefaultTargetedSequencing() != null) {
        dto.setTargetedSequencingId(item.getSample().getProject().getDefaultTargetedSequencing().getId());
      }
      dto.setBox(newBox);
      if (item.getConcentration() != null) {
        dto.setConcentration(item.getConcentration().toString());
        dto.setConcentrationUnits(item.getConcentrationUnits());
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
      config.put("pageMode", "propagate");
    }
  }

  @GetMapping(value = "/bulk/propagate")
  public ModelAndView propagateDilutions(@RequestParam("ids") String libraryIds,
      @RequestParam(value = "boxId", required = false) Long boxId, ModelMap model) throws IOException {
    BulkPropagateLibraryBackend bulkPropagateDilutionBackend = new BulkPropagateLibraryBackend(
        boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null);
    return bulkPropagateDilutionBackend.propagate(libraryIds, model);
  }

  private final BulkEditTableBackend<LibraryDilution, DilutionDto> dilutionBulkEditBackend = new BulkEditTableBackend<LibraryDilution, DilutionDto>(
      "dilution", DilutionDto.class, "Dilutions") {

    @Override
    protected DilutionDto asDto(LibraryDilution model) {
      return Dtos.asDto(model, true, true);
    }

    @Override
    protected Stream<LibraryDilution> load(List<Long> modelIds) throws IOException {
      return dilutionService.listByIdList(modelIds).stream().sorted(DILUTION_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put("pageMode", "edit");
    }
  };

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editDilutions(@RequestParam("ids") String dilutionIds, ModelMap model) throws IOException {
    return dilutionBulkEditBackend.edit(dilutionIds, model);
  }

  private final class BulkMergeDilutionBackend extends BulkMergeTableBackend<PoolDto> {

    private final BoxDto newBox;

    private BulkMergeDilutionBackend(BoxDto newBox) {
      super("pool", PoolDto.class, "Pools", "Dilutions");
      this.newBox = newBox;
    }

    @Override
    protected PoolDto createDtoFromParents(List<Long> ids) throws IOException {
      List<LibraryDilution> parents = dilutionService.listByIdList(ids);
      if (parents.isEmpty()) {
        throw new IllegalStateException("Cannot have no dilutions for pool propagation.");
      }
      List<PlatformType> platformTypes = parents.stream().map(dilution -> dilution.getLibrary().getPlatformType()).distinct()
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
            .findCommonPrefix(parents.stream().map(dilution -> dilution.getLibrary().getAlias()).toArray(String[]::new));
        if (commonPrefix != null) {
          dto.setAlias(commonPrefix + "_POOL");
        }
      }
      dto.setPooledElements(parents.stream().map(ldi -> Dtos.asDto(ldi, false, false)).collect(Collectors.toSet()));
      if (dto.getPooledElements().stream().allMatch(element -> element.getVolumeUsed() != null)) {
        dto.setVolume(
            Double.toString(dto.getPooledElements().stream().mapToDouble(element -> Double.parseDouble(element.getVolumeUsed())).sum()));
      }

      dto.setBox(newBox);

      return dto;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
      config.put("pageMode", "create");
    }
  }

  @GetMapping(value = "/bulk/merge")
  public ModelAndView propagatePoolsMerged(@RequestParam("ids") String dilutionIds,
      @RequestParam(value = "boxId", required = false) Long boxId, ModelMap model) throws IOException {
    BulkMergeDilutionBackend bulkMergeDilutionBackend = new BulkMergeDilutionBackend(
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null));
    return bulkMergeDilutionBackend.propagate(dilutionIds, model);
  }

  private static class BulkCustomPoolTableBackend extends BulkTableBackend<PoolDto> {

    private final int poolQuantity;
    private final List<DilutionDto> dilutions;
    private final PlatformType platformType;
    private final BoxDto newBox;

    public BulkCustomPoolTableBackend(int poolQuantity, String idString, LibraryDilutionService dilutionService,
        BoxDto newBox) throws IOException {
      super("pool", PoolDto.class);
      this.poolQuantity = poolQuantity;
      List<LibraryDilution> ldis = dilutionService.listByIdList(parseIds(idString));
      List<PlatformType> platformTypes = ldis.stream().map(dilution -> dilution.getLibrary().getPlatformType()).distinct()
          .collect(Collectors.toList());
      if (platformTypes.size() > 1) {
        throw new IllegalArgumentException("Cannot create a pool for multiple platforms: "
            + String.join(", ", platformTypes.stream().map(Enum::name).toArray(CharSequence[]::new)));
      }
      this.dilutions = ldis.stream().map(ldi -> Dtos.asDto(ldi, false, false)).collect(Collectors.toList());
      this.platformType = platformTypes.get(0);
      this.newBox = newBox;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.putPOJO("dilutionsToPool", dilutions);
      config.putPOJO("box", newBox);
      config.put("pageMode", "create");
    }

    public ModelAndView create(ModelMap model) throws IOException {
      PoolDto dto = new PoolDto();
      dto.setPlatformType(this.platformType.name());
      dto.setBox(newBox);
      return prepare(model, true, "Create Pools from Dilutions", Collections.nCopies(poolQuantity, dto));
    }

  }

  @GetMapping(value = "/bulk/pool")
  public ModelAndView propagatePoolsCustom(@RequestParam("ids") String dilutionIds, @RequestParam("quantity") int poolQuantity,
      @RequestParam(value = "boxId", required = false) Long boxId, ModelMap model)
      throws IOException {
    BulkCustomPoolTableBackend bulkCustomPoolTableBackend = new BulkCustomPoolTableBackend(poolQuantity, dilutionIds, dilutionService,
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null));
    return bulkCustomPoolTableBackend.create(model);
  }

  private final class BulkPropagateDilutionBackend extends BulkPropagateTableBackend<LibraryDilution, PoolDto> {
    private final BoxDto newBox;

    private BulkPropagateDilutionBackend(BoxDto newBox) {
      super("pool", PoolDto.class, "Pools", "Dilutions");
      this.newBox = newBox;
    }

    @Override
    protected PoolDto createDtoFromParent(LibraryDilution item) {
      PoolDto dto = new PoolDto();
      dto.setAlias(item.getLibrary().getAlias() + "_POOL");
      dto.setPooledElements(Collections.singleton(Dtos.asDto(item, false, false)));
      dto.setPlatformType(item.getLibrary().getPlatformType().name());
      if (item.getVolumeUsed() != null) {
        dto.setVolume(item.getVolumeUsed().toString());
      }
      dto.setBox(newBox);
      if (item.getConcentration() != null) {
        dto.setConcentration(item.getConcentration().toString());
        dto.setConcentrationUnits(item.getConcentrationUnits());
      }
      return dto;
    }

    @Override
    protected Stream<LibraryDilution> loadParents(List<Long> ids) throws IOException {
      return dilutionService.listByIdList(ids).stream().sorted(DILUTION_COMPARATOR);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.putPOJO("box", newBox);
      config.put("pageMode", "propagate");
    }
  }

  @GetMapping(value = "/bulk/pool-separate")
  public ModelAndView propagatePoolsIndividual(@RequestParam("ids") String dilutionIds,
      @RequestParam(value = "boxId", required = false) Long boxId, ModelMap model) throws IOException {
    BulkPropagateDilutionBackend bulkPropagateDilutionBackend = new BulkPropagateDilutionBackend(
        (boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null));
    return bulkPropagateDilutionBackend.propagate(dilutionIds, model);
  }

}
