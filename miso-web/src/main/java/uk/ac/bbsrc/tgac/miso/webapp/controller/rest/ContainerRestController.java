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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PartitionSpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

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

  @GetMapping(value = "{containerBarcode}", produces = "application/json")
  public @ResponseBody List<ContainerDto> jsonRest(@PathVariable String containerBarcode) throws IOException {
    return containerService.listByBarcode(containerBarcode).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ContainerDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/platform/{platform}", produces = "application/json")
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

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<SequencerPartitionContainer> containers = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null container", Status.BAD_REQUEST);
      }
      SequencerPartitionContainer container = containerService.get(id);
      if (container == null) {
        throw new RestException("Container " + id + " not found", Status.BAD_REQUEST);
      }
      containers.add(container);
    }
    containerService.bulkDelete(containers);
  }

  private static final Pattern COMMA = Pattern.compile(",");

  @GetMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(HttpServletRequest request, HttpServletResponse response, UriComponentsBuilder uriBuilder) {
    Stream<Partition> input = COMMA.splitAsStream(request.getParameter("ids"))
        .map(Long::parseLong)
        .map(WhineyFunction.rethrow(containerService::get))
        .flatMap(container -> container.getPartitions().stream());
    return MisoWebUtils.generateSpreadsheet(input, PartitionSpreadsheets::valueOf, request, response);
  }

  @PostMapping(value = "/validate-serial-number")
  public ResponseEntity<?> validateSerialNumber(@RequestBody String params, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    JsonNode reqBody = null;
    try {
      reqBody = new ObjectMapper().readTree(params);
    } catch (JsonProcessingException e) {
      String error = String.format("Error looking up containers by serial number");
      log.error(error, e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(error);
    }

    if (!reqBody.has("serialNumber") || LimsUtils.isStringEmptyOrNull(reqBody.get("serialNumber").asText()) || !reqBody.has("containerId")
        || reqBody.get("containerId").asLong() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return ResponseEntity
          .status(HttpStatus.PRECONDITION_FAILED)
          .body("Container ID and serial number must be provided");
    }
    String serialNumber = reqBody.get("serialNumber").asText();
    Long containerId = reqBody.get("containerId").asLong();
    try {
      List<SequencerPartitionContainer> matchingContainers = (List<SequencerPartitionContainer>) containerService
          .listByBarcode(serialNumber);
      if (matchingContainers.isEmpty()
          || (matchingContainers.size() == 1 && matchingContainers.get(0).getId() == Long.valueOf(containerId))) {
        return ResponseEntity.status(HttpStatus.OK).build();
      } else {
        return ResponseEntity
            .status(HttpStatus.PRECONDITION_FAILED)
            .body("Serial number is already associated with another container");
      }
    } catch (IOException e) {
      String error = String.format("Error looking up containers by serial number '%s'", serialNumber);
      log.error(error, e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(error);
    }
  }

}
