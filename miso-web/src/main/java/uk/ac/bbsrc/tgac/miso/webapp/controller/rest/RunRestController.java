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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyConsumer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunDto;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.impl.PartitionQCService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RestExceptionHandler.RestError;

/**
 * A controller to handle all REST requests for Runs
 * 
 * @author Rob Davey
 * @date 01-Sep-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/run")
@SessionAttributes("run")
public class RunRestController extends RestController {
  public static class RunPartitionQCRequest {
    private List<Long> partitionIds;
    private Long qcTypeId;
    private String notes;

    public List<Long> getPartitionIds() {
      return partitionIds;
    }

    public void setPartitionIds(List<Long> partitionIds) {
      this.partitionIds = partitionIds;
    }

    public Long getQcTypeId() {
      return qcTypeId;
    }

    public void setQcTypeId(Long qcTypeId) {
      this.qcTypeId = qcTypeId;
    }

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }
  }

  protected static final Logger log = LoggerFactory.getLogger(RunRestController.class);
  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  @Autowired
  private RunService runService;
  @Autowired
  private PartitionQCService partitionQCService;

  private final JQueryDataTableBackend<Run, RunDto> jQueryBackend = new JQueryDataTableBackend<Run, RunDto>() {

    @Override
    protected RunDto asDto(Run model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Run> getSource() throws IOException {
      return runService;
    }
  };
  @Autowired
  private ContainerService containerService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @RequestMapping(value = "{runId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String getRunById(@PathVariable Long runId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Run r = runService.get(runId);
    if (r == null) {
      throw new RestException("No run found with ID: " + runId, Status.NOT_FOUND);
    }
    return mapper.writeValueAsString(r);
  }

  @RequestMapping(value = "/alias/{runAlias}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String getRunByAlias(@PathVariable String runAlias) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Run r = runService.getRunByAlias(runAlias);
    if (r == null) {
      throw new RestException("No run found with alias: " + runAlias, Status.NOT_FOUND);
    }
    return mapper.writeValueAsString(r);
  }

  @RequestMapping(value = "/{runId}/samplesheet", method = RequestMethod.GET)
  public HttpEntity<String> getSampleSheetForRun(@PathVariable Long runId, HttpServletResponse response) throws IOException {
    Run run = runService.get(runId);
    return getSampleSheetForRun(run, SampleSheet.CASAVA_1_8, response);
  }

  @RequestMapping(value = "/{runId}/oldsamplesheet", method = RequestMethod.GET)
  public HttpEntity<String> getOldSampleSheetForRun(@PathVariable Long runId, HttpServletResponse response) throws IOException {
    Run run = runService.get(runId);
    return getSampleSheetForRun(run, SampleSheet.CASAVA_1_7, response);
  }

  @RequestMapping(value = "/alias/{runAlias}/samplesheet", method = RequestMethod.GET)
  public HttpEntity<String> getSampleSheetForRunByAlias(@PathVariable String runAlias, HttpServletResponse response) throws IOException {
    Run run = runService.getRunByAlias(runAlias);
    return getSampleSheetForRun(run, SampleSheet.CASAVA_1_8, response);
  }

  @RequestMapping(value = "/alias/{runAlias}/oldsamplesheet", method = RequestMethod.GET)
  public HttpEntity<String> getOldSampleSheetForRunByAlias(@PathVariable String runAlias, HttpServletResponse response)
      throws IOException {
    Run run = runService.getRunByAlias(runAlias);
    return getSampleSheetForRun(run, SampleSheet.CASAVA_1_7, response);
  }

  private HttpEntity<String> getSampleSheetForRun(Run run, SampleSheet casavaVersion, HttpServletResponse response) throws IOException {
    if (run == null) {
      throw new RestException("Run does not exist.", Status.NOT_FOUND);
    }
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    if (run.getSequencerPartitionContainers().size() != 1) {
      throw new RestException(
          "Expected 1 sequencing container for run " + run.getAlias() + ", but found " + run.getSequencerPartitionContainers().size());
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("text", "csv"));
    response.setHeader("Content-Disposition",
        "attachment; filename=" + String.format("RUN%d-%s-SampleSheet.csv", run.getId(), casavaVersion.name()));

    return new HttpEntity<>(casavaVersion.createSampleSheet(run, user), headers);
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String listAllRuns() throws IOException {
    Collection<Run> lr = runService.list();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(lr);
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<RunDto> dataTable(HttpServletRequest request, HttpServletResponse response, UriComponentsBuilder uriBuilder)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<RunDto> dataTableByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "/dt/platform/{platform}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<RunDto> dataTableByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder)
      throws IOException {
    PlatformType platformType = PlatformType.valueOf(platform);
    if (platformType == null) {
      throw new RestException("Invalid platform type.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(platformType));
  }

  @RequestMapping(value = "/dt/sequencer/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<RunDto> dataTableBySequencer(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.sequencer(id));
  }

  @ExceptionHandler(Exception.class)
  public @ResponseBody RestError handleError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
    return RestExceptionHandler.handleException(request, response, exception);
  }

  @RequestMapping(value = "{runId}/add", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(code = HttpStatus.OK)
  public void addContainerByBarcode(@PathVariable Long runId, @RequestParam("barcode") String barcode) throws IOException {
    Run run = runService.get(runId);
    Collection<SequencerPartitionContainer> containers = containerService
        .listByBarcode(barcode);
    if (containers.isEmpty()) {
      throw new RestException("No containers with this barcode.", Status.BAD_REQUEST);
    }
    if (containers.size() > 1) {
      throw new RestException("Multiple containers with this barcode.", Status.BAD_REQUEST);
    }
    SequencerPartitionContainer container = containers.iterator().next();
    if (container.getPlatform().getId() != run.getSequencerReference().getPlatform().getId()) {
      throw new RestException(String.format("This container is meant for %s but this run came from %s.",
          container.getPlatform().getNameAndModel(), run.getSequencerReference().getPlatform().getNameAndModel()), Status.BAD_REQUEST);
    }
    run.addSequencerPartitionContainer(container);
    runService.update(run);
  }

  @RequestMapping(value = "{runId}/remove", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(code = HttpStatus.OK)
  public void removeContainer(@PathVariable Long runId, @RequestBody List<Long> containerIds) throws IOException {
    Run run = runService.get(runId);
    run.setSequencerPartitionContainers(run.getSequencerPartitionContainers().stream()
        .filter(container -> !containerIds.contains(container.getId())).collect(Collectors.toList()));
    runService.update(run);
  }

  @RequestMapping(value = "{runId}/qc", method = RequestMethod.POST, produces = "application/json")
  @ResponseStatus(code = HttpStatus.OK)
  public void setQc(@PathVariable Long runId, @RequestBody RunPartitionQCRequest request) throws IOException {
    Run run = runService.get(runId);
    PartitionQCType qcType = partitionQCService.listTypes().stream().filter(qt -> qt.getId() == request.getQcTypeId()).findAny()
        .orElseThrow(() -> new RestException(Status.BAD_REQUEST));
    run.getSequencerPartitionContainers().stream()//
        .flatMap(container -> container.getPartitions().stream())//
        .filter(partition -> request.partitionIds.contains(partition.getId()))//
        .map(WhineyFunction.rethrow(partition -> {
          PartitionQC qc = partitionQCService.get(run, partition);
          if (qc == null) {
            qc = new PartitionQC();
            qc.setRun(run);
            qc.setPartition(partition);
          }
          qc.setType(qcType);
          qc.setNotes(request.notes);
          return qc;
        })).forEach(WhineyConsumer.rethrow(partitionQCService::save));
  }

}
