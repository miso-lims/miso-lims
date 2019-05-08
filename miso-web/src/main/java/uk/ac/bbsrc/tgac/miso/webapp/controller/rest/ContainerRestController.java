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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PartitionSpreadsheets;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.service.ContainerModelService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/containers")
public class ContainerRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(ContainerRestController.class);

  @Autowired
  private ContainerService containerService;
  @Autowired
  private ContainerModelService containerModelService;

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

  @GetMapping(value = "/{containerBarcode}", produces = "application/json")
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

  @PostMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    Stream<Partition> input = request.getIds().stream()
        .map(WhineyFunction.rethrow(containerService::get))
        .flatMap(container -> container.getPartitions().stream());
    return MisoWebUtils.generateSpreadsheet(request, input, PartitionSpreadsheets::valueOf, response);
  }

  private static class SerialNumberValidationDto {
    private final String serialNumber;
    private final String containerId;

    @SuppressWarnings("unused")
    public SerialNumberValidationDto(@JsonProperty("serialNumber") String serialNumber, @JsonProperty("containerId") String containerId) {
      this.serialNumber = serialNumber;
      this.containerId = containerId;
    }

    public String getSerialNumber() {
      return serialNumber;
    }

    public String getContainerId() {
      return containerId;
    }
  }

  @PostMapping(value = "/validate-serial-number")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void validateSerialNumber(@RequestBody SerialNumberValidationDto params, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    String serialNumber = params.getSerialNumber();
    String maybeContainerId = params.getContainerId();

    if (isStringEmptyOrNull(serialNumber) || isStringEmptyOrNull(maybeContainerId)) {
      throw new RestException("Serial number and containerID must be provided", Status.BAD_REQUEST);
    }

    final Long containerId = (maybeContainerId.contains("Unsaved") ? SequencerPartitionContainerImpl.UNSAVED_ID
        : Long.valueOf(Integer.valueOf(maybeContainerId)));

    List<SequencerPartitionContainer> matchingContainers = (List<SequencerPartitionContainer>) containerService
        .listByBarcode(serialNumber);
    if (!matchingContainers.isEmpty() && matchingContainers.stream().noneMatch(spc -> spc.getId() == containerId)) {
      throw new RestException("Serial number is already associated with another container", Status.BAD_REQUEST);
    }
  }

  @PostMapping
  public @ResponseBody ContainerDto create(@RequestBody ContainerDto dto) throws IOException {
    if (dto.getModel() == null || dto.getModel().getId() == null) {
      throw new RestException("Container model not specified", Status.BAD_REQUEST);
    }
    SequencingContainerModel model = containerModelService.get(dto.getModel().getId());
    if (model == null) {
      throw new RestException("Invalid container model", Status.BAD_REQUEST);
    }
    return RestUtils.createObject("Container", dto, d -> {
      SequencerPartitionContainer container = Dtos.to(d);
      container.setModel(model);
      container.setPartitionLimit(model.getPartitionCount());
      return container;
    }, containerService, Dtos::asDto);

  }

  @PutMapping("/{containerId}")
  public @ResponseBody ContainerDto update(@PathVariable long containerId, @RequestBody ContainerDto dto) throws IOException {
    SequencerPartitionContainer original = containerService.get(containerId);
    return RestUtils.updateObject("Container", containerId, dto, d -> {
      SequencerPartitionContainer container = Dtos.to(d);
      // reset partitions since they're not intended to be modified by this method
      container.setPartitions(original.getPartitions());
      return container;
    }, containerService, Dtos::asDto);
  }

}
