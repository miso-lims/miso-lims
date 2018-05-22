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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyConsumer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.RunDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse.PoolPickerEntry;

/**
 * A controller to handle all REST requests for Pools
 * 
 * Created by IntelliJ IDEA. User: bianx Date: 17-Aug-2011 Time: 11:34:04
 */
@Controller
@RequestMapping("/rest/pool")
@SessionAttributes("pool")
public class PoolRestController extends RestController {
  public static class PoolChangeRequest {
    private List<Long> add;
    private List<Long> remove;

    public List<Long> getAdd() {
      return add;
    }

    public List<Long> getRemove() {
      return remove;
    }

    public void setAdd(List<Long> add) {
      this.add = add;
    }

    public void setRemove(List<Long> remove) {
      this.remove = remove;
    }
  }

  private final JQueryDataTableBackend<Pool, PoolDto> jQueryBackend = new JQueryDataTableBackend<Pool, PoolDto>() {

    @Override
    protected PoolDto asDto(Pool model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Pool> getSource() throws IOException {
      return poolService;
    }

  };

  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  @RequestMapping(value = "{poolId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody PoolDto getPoolById(@PathVariable Long poolId) throws IOException {
    Pool p = poolService.get(poolId);
    if (p == null) {
      throw new RestException("No pool found with ID: " + poolId, Status.NOT_FOUND);
    }
    return Dtos.asDto(p, true);
  }

  @RequestMapping(value = "{poolId}/runs", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody List<RunDto> getRunsByPoolId(@PathVariable Long poolId) throws IOException {
    Collection<Run> rr = runService.listByPoolId(poolId);
    return Dtos.asRunDtos(rr);
  }

  @RequestMapping(method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public PoolDto createPool(@RequestBody PoolDto pool, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    Long id = poolService.save(Dtos.to(pool));
    return getPoolById(id);

  }

  @RequestMapping(value = "{poolId}", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public PoolDto updatePool(@PathVariable Long poolId, @RequestBody PoolDto pool) throws IOException {
    Pool p = Dtos.to(pool);
    p.setId(poolId);
    poolService.save(p);
    return Dtos.asDto(poolService.get(poolId), true);
  }

  @RequestMapping(value = "/{poolId}/contents", method = RequestMethod.PUT, produces = "application/json")
  public @ResponseBody PoolDto changePoolContents(@PathVariable Long poolId, @RequestBody PoolChangeRequest request) throws IOException {
    Pool pool = poolService.get(poolId);
    Stream<PoolableElementView> originalMinusRemoved = pool.getPoolableElementViews().stream()
        .filter(element -> !request.remove.contains(element.getDilutionId()));
    Stream<PoolableElementView> added = poolableElementViewService.list(request.add).stream();
    pool.setPoolableElementViews(Stream.concat(originalMinusRemoved, added).collect(Collectors.toSet()));
    poolService.save(pool);
    return Dtos.asDto(poolService.get(poolId), true);
  }

  @RequestMapping(value = "/{poolId}/assign", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void assignPool(@PathVariable Long poolId, @RequestBody List<Long> partitionIds) throws IOException {
    Pool pool = poolId == 0 ? null : poolService.get(poolId);

    // Determine if this pool transition is allowed for this experiment. If removing a pool, it strictly isn't. If the new pool contains the
    // same library as the experiment, it's fine.
    Predicate<Experiment> isTransitionValid = pool == null ? experiment -> false
        : experiment -> pool.getPoolableElementViews().stream().map(PoolableElementView::getLibraryId)
            .anyMatch(id -> id == experiment.getLibrary().getId());

    partitionIds.stream()//
        .map(WhineyFunction.rethrow(containerService::getPartition))//
        .peek(WhineyConsumer.rethrow(partition -> {
          for (Run run : partition.getSequencerPartitionContainer().getRuns()) {
            // Check that we aren't going to hose any existing experiments through this reassignment
            boolean relatedExperimentsOkay = experimentService.listAllByRunId(run.getId()).stream()//
                .flatMap(experiment -> experiment.getRunPartitions().stream())//
                .filter(rp -> rp.getRun().getId() == run.getId() && rp.getPartition().getId() == partition.getId())//
                .map(RunPartition::getExperiment)//
                .allMatch(isTransitionValid);
            if (!relatedExperimentsOkay) {
              throw new RestException(
                  String.format("%s %d is used in an experiment.",
                      partition.getSequencerPartitionContainer().getModel().getPlatformType().getPartitionName(),
                      partition.getPartitionNumber()),
                  Status.BAD_REQUEST);
            }
          }
          if (pool != null && partition.getSequencerPartitionContainer().getModel().getPlatformType() != pool.getPlatformType()) {
            throw new RestException(
                String.format("%s %d in %s is not compatible with pool %s.",
                    partition.getSequencerPartitionContainer().getModel().getPlatformType().getPartitionName(),
                    partition.getPartitionNumber(), partition.getSequencerPartitionContainer().getIdentificationBarcode(), pool.getName()),
                Status.BAD_REQUEST);
          }
          partition.setPool(pool);
        }))//
        .forEach(WhineyConsumer.rethrow(containerService::update));
    if (pool != null) {
      poolService.save(pool);
    }
  }

  @RequestMapping(value = "platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<PoolDto> getPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    if (PlatformType.getKeys().contains(platform)) {
      Collection<Pool> pools = new ArrayList<>();
      PlatformType platformType = PlatformType.get(platform);
      pools = poolService.listByPlatform(platformType);
      return serializePools(pools, uriBuilder);
    } else {
      throw new RestException("Request must specify a platform");
    }
  }

  @RequestMapping(value = "dt/platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PoolDto> getDTPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    PlatformType platformType = PlatformType.valueOf(platform);
    if (platformType == null) {
      throw new RestException("Invalid platform type.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(platformType));
  }

  @RequestMapping(value = "dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PoolDto> getDTPoolsByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  public List<PoolDto> serializePools(Collection<Pool> pools, UriComponentsBuilder uriBuilder)
      throws IOException {
    List<PoolDto> poolDtos = Dtos.asPoolDtos(pools, false);
    for (PoolDto poolDto : poolDtos) {
      poolDto.writeUrls(uriBuilder);
    }
    return poolDtos;
  }

  @RequestMapping(value = "/picker/search")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform, @RequestParam("query") String query)
      throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.query(query));
  }

  @RequestMapping(value = "/picker/recent")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform)
      throws IOException {
    return getPoolPickerWithFilters(20,
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  private PoolPickerResponse getPoolPickerWithFilters(Integer limit, PaginationFilter... filters) throws IOException {
    PoolPickerResponse ppr = new PoolPickerResponse();
    ppr.populate(poolService, false, "lastModified", limit, this::poolTransform, filters);
    return ppr;
  }

  private PoolPickerEntry poolTransform(Pool pool) throws IOException {
    List<PoolOrderCompletionDto> completions = poolOrderCompletionService.getByPoolId(pool.getId()).stream()
        .map(Dtos::asDto).collect(Collectors.toList());
    return new PoolPickerEntry(Dtos.asDto(pool, true), completions);
  }

  @RequestMapping(value = "query", method = RequestMethod.POST, produces = { "application/json" })
  @ResponseBody
  public List<PoolDto> getPoolsInBulk(@RequestBody List<String> names, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, poolService, p -> Dtos.asDto(p, false),
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @RequestMapping(value = "/bulk-delete", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<Pool> pools = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null pool", Status.BAD_REQUEST);
      }
      Pool pool = poolService.get(id);
      if (pool == null) {
        throw new RestException("Pool " + id + " not found", Status.BAD_REQUEST);
      }
      pools.add(pool);
    }
    poolService.bulkDelete(pools);
  }

}
