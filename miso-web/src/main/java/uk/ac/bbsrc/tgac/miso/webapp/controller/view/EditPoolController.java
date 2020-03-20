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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import net.sf.json.JSONArray;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkEditTableBackend;
import uk.ac.bbsrc.tgac.miso.webapp.util.BulkTableBackend;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.9
 */
@Controller
@RequestMapping("/pool")
public class EditPoolController {
  protected static final Logger log = LoggerFactory.getLogger(EditPoolController.class);

  @Value("${miso.pools.strictIndexChecking:false}")
  private Boolean strictPools;

  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private SequencingOrderService sequencingOrderService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private PoolOrderService poolOrderService;

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  private static class Config {
    private static final String BOX = "box";
    private static final String PAGE_MODE = "pageMode";
    private static final String EDIT = "edit";
    private static final String CREATE = "create";
  }

  @GetMapping(value = "/new")
  public ModelAndView newUnassignedPool(ModelMap model) throws IOException {
    return setupForm(PoolImpl.UNSAVED_ID, model);
  }

  @GetMapping(value = "/{poolId}")
  public ModelAndView setupForm(@PathVariable Long poolId, ModelMap model) throws IOException {
    Pool pool = null;
    if (poolId == PoolImpl.UNSAVED_ID) {
      pool = new PoolImpl();
      model.put("title", "New Pool");
    } else {
      pool = poolService.get(poolId);
      model.put("title", "Pool " + poolId);
    }

    if (pool == null) throw new NotFoundException("No pool found for ID " + poolId.toString());
    PoolDto poolDto = Dtos.asDto(pool, true, false, indexChecker);
    
    ObjectMapper mapper = new ObjectMapper();
    model.put("pool", pool);
    model.put("poolDto", poolId == PoolImpl.UNSAVED_ID ? "{}"
        : mapper.writeValueAsString(poolDto));
    Collection<Partition> partitions = containerService.listPartitionsByPoolId(poolId);
    model.put("partitions", partitions.stream()
        .map(partition -> Dtos.asDto(partition, indexChecker)).collect(Collectors.toList()));
    model.put("runs", poolId == PoolImpl.UNSAVED_ID ? Collections.emptyList() : Dtos.asRunDtos(runService.listByPoolId(poolId)));
    if (poolId == PoolImpl.UNSAVED_ID) {
      model.put("orders", Collections.emptyList());
    } else {
      Collection<SequencingOrder> sequencingOrders = sequencingOrderService.getByPool(pool);
      model.put("orders", Dtos.asSequencingOrderDtos(sequencingOrders, indexChecker));
    }

    model.put("poolorders", poolOrderService.getAllByPoolId(pool.getId()).stream().map(order -> Dtos.asDto(order)).collect(Collectors.toList()));

    model.put("duplicateIndicesSequences", mapper.writeValueAsString(poolDto.getDuplicateIndicesSequences()));
    model.put("nearDuplicateIndicesSequences", mapper.writeValueAsString(poolDto.getNearDuplicateIndicesSequences()));

    model.put("poolTransfers", pool.getTransferViews().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    return new ModelAndView("/WEB-INF/pages/editPool.jsp", model);
  }

  @ModelAttribute
  public void addSequencingParameters(ModelMap model) throws IOException {
    Collection<SequencingParametersDto> sequencingParameters = Dtos.asSequencingParametersDtos(sequencingParametersService.list());
    JSONArray array = new JSONArray();
    array.addAll(sequencingParameters);
    model.put("sequencingParametersJson", array.toString());
  }

  private final BulkEditTableBackend<Pool, PoolDto> bulkEditBackend = new BulkEditTableBackend<Pool, PoolDto>(
      "pool", PoolDto.class, "Pools") {

    @Override
    protected PoolDto asDto(Pool model) {
      return Dtos.asDto(model, true, true, indexChecker);
    }

    @Override
    protected Stream<Pool> load(List<Long> modelIds) throws IOException {
      return poolService.listByIdList(modelIds).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
      config.put(Config.PAGE_MODE, Config.EDIT);
    }
  };

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editPools(@RequestParam("ids") String poolIds, ModelMap model) throws IOException {
    return bulkEditBackend.edit(poolIds, model);
  }

  private static class BulkMergePoolsBackend extends BulkTableBackend<PoolDto> {

    private final PoolService poolService;
    private final BoxDto newBox;
    private final IndexChecker indexChecker;
    private final Boolean strictPools;

    public BulkMergePoolsBackend(BoxDto newBox, PoolService poolService, IndexChecker indexChecker, Boolean strictPools) {
      super("pool", PoolDto.class);
      this.poolService = poolService;
      this.newBox = newBox;
      this.indexChecker = indexChecker;
      this.strictPools = strictPools;
    }

    protected PoolDto createDtoFromParents(List<Long> parentIds, List<Integer> proportions) throws IOException {
      PoolDto dto = new PoolDto();
      List<Pool> parents = new ArrayList<>(poolService.listByIdList(parentIds));

      if (parents.size() < 2) {
        throw new IllegalStateException("Not enough pools to merge");
      }

      List<PlatformType> platformTypes = parents.stream().map(Pool::getPlatformType).distinct().collect(Collectors.toList());
      if (platformTypes.size() > 1) {
        throw new IllegalArgumentException("Cannot merge pools from multiple platforms: "
            + String.join(", ", platformTypes.stream().map(Enum::name).toArray(CharSequence[]::new)));
      }
      dto.setPlatformType(platformTypes.get(0).name());

      String parentString = parents.stream().map(Pool::getName).collect(Collectors.joining(", "));
      dto.setDescription("Created from merging pools: " + parentString);

      String commonPrefix = LimsUtils.findCommonPrefix(parents.stream().map(Pool::getAlias).toArray(String[]::new));
      if (commonPrefix != null) {
        dto.setAlias(commonPrefix + "_POOL");
      }

      Set<LibraryAliquotDto> aliquotDtos = new HashSet<>();
      List<List<Index>> masterIndexList = new LinkedList<>();
      for (Pool parent : parents) {
        for (int i = 0; i < parentIds.size(); i++) {
          if (parentIds.get(i).equals(Long.valueOf(parent.getId()))) {
            for (PoolElement element : parent.getPoolContents()) {
              masterIndexList.add(element.getPoolableElementView().getIndices());
              LibraryAliquotDto existing = aliquotDtos.stream().filter(d -> d.getId().equals(element.getPoolableElementView().getAliquotId()))
                  .findFirst().orElse(null);
              if (existing == null) {
                LibraryAliquotDto ldiDto = Dtos.asDto(element.getPoolableElementView());
                ldiDto.setProportion(element.getProportion() * proportions.get(i));
                aliquotDtos.add(ldiDto);
              } else {
                existing.setProportion(existing.getProportion() + element.getProportion() * proportions.get(i));
              }
            }
            break;
          }
        }
      }
      dto.setPooledElements(aliquotDtos);

      dto.setNearDuplicateIndicesSequences(indexChecker.getNearDuplicateIndicesSequencesFromList(masterIndexList));
      dto.setNearDuplicateIndices(!dto.getNearDuplicateIndicesSequences().isEmpty());
      dto.setDuplicateIndicesSequences(indexChecker.getDuplicateIndicesSequencesFromList(masterIndexList));
      dto.setDuplicateIndices(!dto.getDuplicateIndicesSequences().isEmpty());

      List<VolumeUnit> volumeUnits = parents.stream().map(Pool::getVolumeUnits).filter(Objects::nonNull).distinct()
          .collect(Collectors.toList());
      if (parents.stream().map(Pool::getVolume).allMatch(Objects::nonNull) && volumeUnits.size() == 1) {
        dto.setVolume(LimsUtils.toNiceString(parents.stream()
            .map(Pool::getVolume)
            .reduce(BigDecimal.ZERO, (result, item) -> result.add(item))));
      }



      dto.setBox(newBox);

      return dto;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.putPOJO(Config.BOX, newBox);
      config.put(Config.PAGE_MODE, Config.CREATE);
    }

    public ModelAndView merge(String parentIdsString, String proportionsString, ModelMap model) throws IOException {
      List<Long> parentIds = LimsUtils.parseIds(parentIdsString);
      List<Integer> proportions = parseProportions(proportionsString);

      //This is packaged in a List only because prepare() wants a List. There's only ever 1 PoolDto in here.
      List<PoolDto> dtos = Lists.newArrayList(createDtoFromParents(parentIds, proportions));
      PoolDto newDto = dtos.get(0);
      if(strictPools) newDto.setMergeChild(true);

      dtos.set(0, newDto);

      return prepare(model, true, "Merge Pools", dtos);
    }

    private static List<Integer> parseProportions(String proportionsString) {
      String[] split = proportionsString.split(",");
      List<Integer> proportions = new ArrayList<>();
      for (int i = 0; i < split.length; i++) {
        Integer prop = Integer.valueOf(split[i]);
        if (prop < 1) {
          throw new IllegalArgumentException("Invalid proportion: prop");
        }
        proportions.add(prop);
      }
      return proportions;
    }

  }

  @GetMapping(value = "/bulk/merge")
  public ModelAndView propagatePoolsMerged(@RequestParam("ids") String poolIds, @RequestParam(value = "boxId", required = false) Long boxId, @RequestParam String proportions, ModelMap model)
      throws IOException {
    return new BulkMergePoolsBackend((boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null),
            poolService, indexChecker, strictPools).merge(poolIds, proportions, model);
  }

}
