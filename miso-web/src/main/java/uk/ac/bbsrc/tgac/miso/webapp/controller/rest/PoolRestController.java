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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
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
  private LibraryDilutionService dilutionService;
  @Autowired
  private PlatformService platformService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  @RequestMapping(value = "{poolId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody PoolDto getPoolById(@PathVariable Long poolId) throws IOException {
    Pool p = poolService.get(poolId);
    if (p == null) {
      throw new RestException("No pool found with ID: " + poolId, Status.NOT_FOUND);
    }
    return Dtos.asDto(p, true);
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

  @RequestMapping(value = "/wizard/librarydilutions", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody JSONObject ldRest() throws IOException {
    Collection<LibraryDilution> lds = dilutionService.list();

    List<String> types = new ArrayList<>(platformService.listDistinctPlatformTypeNames());
    Collections.sort(types);

    JSONArray platformTypeArray = new JSONArray();
    for (String type : types) {
      platformTypeArray.add(type);
    }

    JSONObject ldJSON = new JSONObject();
    JSONArray tableArray = new JSONArray();
    for (LibraryDilution ld : lds) {
      JSONArray rowArray = new JSONArray();
      rowArray.add("");
      rowArray.add(ld.getName());
      rowArray.add("");
      tableArray.add(rowArray);
    }

    ldJSON.put("aaData", tableArray);
    return ldJSON;
  }

  @RequestMapping(value = "/wizard/platformtypes", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String platformTypesRest() throws IOException {
    List<String> names = new ArrayList<>();
    List<String> types = new ArrayList<>(platformService.listDistinctPlatformTypeNames());
    for (String name : types) {
      names.add("\"" + name + "\"");
    }
    return "[" + LimsUtils.join(names, ",") + "]";
  }

  @RequestMapping(value = "/picker/search")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform, @RequestParam("query") String query)
      throws IOException {
    PlatformType platformType = PlatformType.get(platform);
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(platformType),
        PaginationFilter.query(query));
  }

  @RequestMapping(value = "/picker/readytorun")
  @ResponseBody
  public PoolPickerResponse getPickersByReadyToRun(@RequestParam("platform") String platform,
      @RequestParam("readyToRun") boolean readyToRun) throws IOException {
    PlatformType platformType = PlatformType.get(platform);
    return getPoolPickerWithFilters(100, PaginationFilter.platformType(platformType),
        PaginationFilter.readyToRun(readyToRun));
  }

  @RequestMapping(value = "/picker/recent")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform)
      throws IOException {
    PlatformType platformType = PlatformType.get(platform);
    return getPoolPickerWithFilters(20,
        PaginationFilter.platformType(platformType));
  }

  private PoolPickerResponse getPoolPickerWithFilters(Integer limit, PaginationFilter... filters) throws IOException {
    PoolPickerResponse ppr = new PoolPickerResponse();
    ppr.populate(poolService, false, "lastModified", limit, this::poolTransform, filters);
    return ppr;
  }

  private PoolPickerEntry poolTransform(Pool pool) throws IOException {
    List<PoolOrderCompletionDto> completions = poolOrderCompletionService.getByPoolId(pool.getId()).stream()
        .map(completion -> Dtos.asDto(completion)).collect(Collectors.toList());
    return new PoolPickerEntry(Dtos.asDto(pool, true), completions);
  }
}
