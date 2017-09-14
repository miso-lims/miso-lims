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
import java.util.List;
import java.util.stream.Collectors;

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

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller.rest
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 */
@Controller
@RequestMapping("/rest/container")
@SessionAttributes("container")
public class ContainerRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(ContainerRestController.class);

  @Autowired
  private ContainerService containerService;

  private final JQueryDataTableBackend<SequencerPartitionContainer, ContainerDto> jQueryBackend = new JQueryDataTableBackend<SequencerPartitionContainer, ContainerDto>() {

    @Override
    protected ContainerDto asDto(SequencerPartitionContainer model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<SequencerPartitionContainer> getSource() throws IOException {
      return containerService;
    }

  };

  @RequestMapping(value = "{containerBarcode}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody List<ContainerDto> jsonRest(@PathVariable String containerBarcode) throws IOException {
    return containerService.listByBarcode(containerBarcode).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ContainerDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/dt/platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ContainerDto> dataTableByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    PlatformType platformType = PlatformType.valueOf(platform);
    if (platformType == null) {
      throw new RestException("Invalid platform.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(platformType));
  }
}
