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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import net.sf.json.JSONArray;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
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
@SessionAttributes("pool")
public class EditPoolController {
  protected static final Logger log = LoggerFactory.getLogger(EditPoolController.class);

  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private PlatformService platformService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private PoolOrderService poolOrderService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private BoxService boxService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  private static class Config {
    private static final String BOX = "box";
  }

  @GetMapping(value = "/rest/changes")
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return changeLogService.listAll("Pool");
  }

  @ModelAttribute("platformTypes")
  public Collection<String> populatePlatformTypes() throws IOException {
    return PlatformType.platformTypeNames(platformService.listActivePlatformTypes());
  }

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @ModelAttribute("autoGenerateIdBarcodes")
  public Boolean autoGenerateIdentificationBarcodes() {
    return autoGenerateIdBarcodes;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return poolService.getPoolColumnSizes();
  }

  @GetMapping(value = "/new")
  public ModelAndView newUnassignedPool(ModelMap model) throws IOException {
    return setupForm(PoolImpl.UNSAVED_ID, model);
  }

  @GetMapping(value = "/{poolId}")
  public ModelAndView setupForm(@PathVariable Long poolId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Pool pool = null;
      if (poolId == PoolImpl.UNSAVED_ID) {
        pool = new PoolImpl(user);
        model.put("title", "New Pool");
      } else {
        pool = poolService.get(poolId);
        model.put("title", "Pool " + poolId);
      }

      if (pool == null) throw new NotFoundException("No pool found for ID " + poolId.toString());

      ObjectMapper mapper = new ObjectMapper();
      model.put("formObj", pool);
      model.put("pool", pool);
      model.put("poolDto", poolId == PoolImpl.UNSAVED_ID ? "null" : mapper.writeValueAsString(Dtos.asDto(pool, false, false)));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, pool, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, pool, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, pool, securityManager.listAllGroups()));
      model.put("platforms", getFilteredPlatforms(pool.getPlatformType()));

      model.put("partitions", containerService.listPartitionsByPoolId(poolId).stream().map(Dtos::asDto).collect(Collectors.toList()));
      model.put("runs", poolId == PoolImpl.UNSAVED_ID ? Collections.emptyList() : Dtos.asRunDtos(runService.listByPoolId(poolId)));
      model.put("orders",
          poolId == PoolImpl.UNSAVED_ID ? Collections.emptyList() : Dtos.asPoolOrderDtos(poolOrderService.getByPool(poolId)));

      model.put("duplicateIndicesSequences", mapper.writeValueAsString(pool.getDuplicateIndicesSequences()));
      model.put("nearDuplicateIndicesSequences", mapper.writeValueAsString(pool.getNearDuplicateIndicesSequences()));

      model.put("includedDilutions", Dtos.asDto(pool, true, false).getPooledElements());

      model.put("volumeUnits", VolumeUnit.values());
      model.put("concentrationUnits", ConcentrationUnit.values());

      List<String> warnings = new ArrayList<>();
      addConsentWarning(pool, warnings);
      model.addAttribute("warnings", warnings);

      return new ModelAndView("/pages/editPool.jsp", model);
    } catch (IOException ex) {
      log.debug("Failed to show pool", ex);
      throw ex;
    }
  }

  private void addConsentWarning(Pool pool, List<String> warnings) {
    List<String> consentRevokedNames = pool.getPoolDilutions()
        .stream()
        .map(PoolDilution::getPoolableElementView)
        .filter(ldi -> {
          Sample sam = ldi.getSample();
          return LimsUtils.isDetailedSample(sam) && LimsUtils.getIdentityConsentLevel((DetailedSample) sam) == ConsentLevel.REVOKED;
        })
        .map(PoolableElementView::getDilutionName)
        .collect(Collectors.toList());
    if (!consentRevokedNames.isEmpty()) {
      warnings.add("Donor has revoked consent for "
          + String.join(", ", consentRevokedNames));
    }
  }

  private Collection<Platform> getFilteredPlatforms(PlatformType platformType) throws IOException {
    List<Platform> selected = new ArrayList<>();
    for (Platform p : platformService.list()) {
      if (p.getPlatformType() == platformType && !sequencingParametersService.getForPlatform(p.getId()).isEmpty()) {
        selected.add(p);
      }
    }
    Collections.sort(selected, new Comparator<Platform>() {

      @Override
      public int compare(Platform o1, Platform o2) {
        return o1.getNameAndModel().compareTo(o2.getNameAndModel());
      }

    });
    return selected;
  }

  @PostMapping
  public String processSubmit(@ModelAttribute("pool") Pool pool, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      // The pooled elements may have been modified asynchronously while the form was being edited. Since they can't be edited by form,
      // update them to avoid reverting the state.
      if (pool.getId() != PoolImpl.UNSAVED_ID) {
        Pool original = poolService.get(pool.getId());
        pool.setPoolDilutions(original.getPoolDilutions());
      }

      poolService.save(pool);
      session.setComplete();
      model.clear();
      return "redirect:/miso/pool/" + pool.getId();
    } catch (IOException ex) {
      log.debug("Failed to save pool", ex);
      throw ex;
    }
  }

  @ModelAttribute
  public void addSequencingParameters(ModelMap model) throws IOException {
    Collection<SequencingParametersDto> sequencingParameters = Dtos.asSequencingParametersDtos(sequencingParametersService.getAll());
    JSONArray array = new JSONArray();
    array.addAll(sequencingParameters);
    model.put("sequencingParametersJson", array.toString());
  }

  private final BulkEditTableBackend<Pool, PoolDto> bulkEditBackend = new BulkEditTableBackend<Pool, PoolDto>(
      "pool", PoolDto.class, "Pools") {

    @Override
    protected PoolDto asDto(Pool model) {
      return Dtos.asDto(model, true, true);
    }

    @Override
    protected Stream<Pool> load(List<Long> modelIds) throws IOException {
      return poolService.listByIdList(modelIds).stream().sorted(new AliasComparator<>());
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) {
    }
  };

  @GetMapping(value = "/bulk/edit")
  public ModelAndView editPools(@RequestParam("ids") String poolIds, ModelMap model) throws IOException {
    return bulkEditBackend.edit(poolIds, model);
  }

  private static class BulkMergePoolsBackend extends BulkTableBackend<PoolDto> {

    private final PoolService poolService;
    private final BoxDto newBox;

    public BulkMergePoolsBackend(BoxDto newBox, PoolService poolService) {
      super("pool", PoolDto.class);
      this.poolService = poolService;
      this.newBox = newBox;
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

      Set<DilutionDto> dilutionDtos = new HashSet<>();
      for (Pool parent : parents) {
        for (int i = 0; i < parentIds.size(); i++) {
          if (parentIds.get(i).equals(Long.valueOf(parent.getId()))) {
            for (PoolDilution pd : parent.getPoolDilutions()) {
              DilutionDto existing = dilutionDtos.stream().filter(d -> d.getId().equals(pd.getPoolableElementView().getDilutionId()))
                  .findFirst().orElse(null);
              if (existing == null) {
                DilutionDto ldiDto = Dtos.asDto(pd.getPoolableElementView());
                ldiDto.setProportion(pd.getProportion() * proportions.get(i));
                dilutionDtos.add(ldiDto);
              } else {
                existing.setProportion(existing.getProportion() + pd.getProportion() * proportions.get(i));
              }
            }
            break;
          }
        }
      }
      dto.setPooledElements(dilutionDtos);

      List<VolumeUnit> volumeUnits = parents.stream().map(Pool::getVolumeUnits).filter(Objects::nonNull).distinct()
          .collect(Collectors.toList());
      if (parents.stream().map(Pool::getVolume).allMatch(Objects::nonNull) && volumeUnits.size() == 1) {
        dto.setVolume(Double.toString(parents.stream().mapToDouble(Pool::getVolume).sum()));
      }

      dto.setBox(newBox);

      return dto;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.putPOJO(Config.BOX, newBox);
    }

    public ModelAndView merge(String parentIdsString, String proportionsString, ModelMap model) throws IOException {
      List<Long> parentIds = parseIds(parentIdsString);
      List<Integer> proportions = parseProportions(proportionsString);
      List<PoolDto> dtos = Lists.newArrayList(createDtoFromParents(parentIds, proportions));
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
    return new BulkMergePoolsBackend((boxId != null ? Dtos.asDto(boxService.get(boxId), true) : null), poolService).merge(poolIds, proportions, model);
  }

}
