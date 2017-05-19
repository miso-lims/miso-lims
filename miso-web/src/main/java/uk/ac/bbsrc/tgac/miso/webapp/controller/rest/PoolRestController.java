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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;

/**
 * A controller to handle all REST requests for Pools
 * 
 * Created by IntelliJ IDEA. User: bianx Date: 17-Aug-2011 Time: 11:34:04
 */
@Controller
@RequestMapping("/rest/pool")
@SessionAttributes("pool")
public class PoolRestController extends RestController {
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
  private RequestManager requestManager;

  @Autowired
  private LibraryDilutionService dilutionService;

  @Autowired
  private PoolService poolService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  @RequestMapping(value = "{poolId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String getPoolById(@PathVariable Long poolId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Pool p = poolService.getPoolById(poolId);
    if (p == null) {
      throw new RestException("No pool found with ID: " + poolId, Status.NOT_FOUND);
    }
    return mapper.writeValueAsString(p);
  }

  @RequestMapping(value = "platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<PoolDto> getPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    if (PlatformType.getKeys().contains(platform)) {
      Collection<Pool> pools = new ArrayList<>();
      PlatformType platformType = PlatformType.get(platform);
      pools = poolService.listAllPoolsByPlatform(platformType);
      return serializePools(pools, uriBuilder);
    } else {
      throw new RestException("Request must specify a platform");
    }
  }

  @RequestMapping(value = "dt/platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PoolDto> getDTPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    if (!PlatformType.getKeys().contains(platform)) {
      throw new RestException("Invalid platform type.");
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(PlatformType.get(platform)));
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

    List<String> types = new ArrayList<>(requestManager.listDistinctPlatformNames());
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
    List<String> types = new ArrayList<>(requestManager.listDistinctPlatformNames());
    for (String name : types) {
      names.add("\"" + name + "\"");
    }
    return "[" + LimsUtils.join(names, ",") + "]";
  }

}
