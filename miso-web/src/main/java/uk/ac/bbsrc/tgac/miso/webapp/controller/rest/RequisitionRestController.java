package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/requisitions")
public class RequisitionRestController extends RestController {

  private static final String TYPE_LABEL = "Requisition";

  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  private final JQueryDataTableBackend<Requisition, RequisitionDto> jQueryBackend = new JQueryDataTableBackend<>() {

    @Override
    protected PaginatedDataSource<Requisition> getSource() throws IOException {
      return requisitionService;
    }

    @Override
    protected RequisitionDto asDto(Requisition model) {
      return RequisitionDto.from(model);
    }

  };

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<RequisitionDto> list(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody RequisitionDto create(@RequestBody RequisitionDto dto) throws IOException {
    return RestUtils.createObject(TYPE_LABEL, dto, RequisitionDto::to, requisitionService, RequisitionDto::from);
  }

  @PutMapping("/{id}")
  public @ResponseBody RequisitionDto update(@RequestBody RequisitionDto dto, @PathVariable long id)
      throws IOException {
    return RestUtils.updateObject(TYPE_LABEL, id, dto, RequisitionDto::to, requisitionService, RequisitionDto::from);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, requisitionService);
  }

  @PostMapping("/{requisitionId}/samples")
  public @ResponseBody ObjectNode addSamples(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    return addSamples(requisition, ids);
  }

  private ObjectNode addSamples(Requisition requisition, List<Long> ids) throws IOException {
    List<Sample> samples = new ArrayList<>();
    for (Long id : ids) {
      Sample sample = RestUtils.retrieve("Sample", id, sampleService, Status.BAD_REQUEST);
      sample.setRequisition(requisition);
      samples.add(sample);
    }
    return asyncOperationManager.startAsyncBulkUpdate("Sample", samples, sampleService);
  }

  @PostMapping("/{requisitionId}/samples/remove")
  public @ResponseBody ObjectNode removeSamples(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    // Retrieve requisition just to validate existence
    RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<Sample> samples = new ArrayList<>();
    for (Long id : ids) {
      Sample sample = RestUtils.retrieve("Sample", id, sampleService, Status.BAD_REQUEST);
      if (sample.getRequisition() == null || sample.getRequisition().getId() != requisitionId) {
        throw new RestException(
            String.format("%s (%s) is not linked to this requisition", sample.getAlias(), sample.getName()),
            Status.BAD_REQUEST);
      }
      sample.setRequisition(null);
      samples.add(sample);
    }
    return asyncOperationManager.startAsyncBulkUpdate("Sample", samples, sampleService);
  }

  @GetMapping("/samplesupdate/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Sample.class);
  }

  @PostMapping("/{requisitionId}/supplementalsamples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void addSupplementalSamples(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<Sample> samples = getSamples(ids);
    requisitionService.addSupplementalSamples(requisition, samples);
  }

  @PostMapping("/{requisitionId}/supplementalsamples/remove")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void removeSupplementalSamples(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    // Retrieve requisition just to validate existence
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<Sample> samples = getSamples(ids);
    requisitionService.removeSupplementalSamples(requisition, samples);
  }

  private List<Sample> getSamples(List<Long> ids) throws IOException {
    List<Sample> samples = new ArrayList<>();
    for (Long id : ids) {
      Sample sample = RestUtils.retrieve("Sample", id, sampleService, Status.BAD_REQUEST);
      samples.add(sample);
    }
    return samples;
  }

  @GetMapping("/search")
  public @ResponseBody List<RequisitionDto> search(@RequestParam String q) throws IOException {
    return requisitionService.list(0, 0, false, "id", PaginationFilter.query(q)).stream()
        .map(RequisitionDto::from)
        .collect(Collectors.toList());
  }

  @GetMapping("/{requisitionId}/runlibraries")
  public @ResponseBody List<RunPartitionAliquotDto> listRunLibraries(@PathVariable long requisitionId)
      throws IOException {
    List<Long> libraryIds = libraryService.listIdsByRequisitionId(requisitionId);
    List<RunPartitionAliquot> runLibraries = runPartitionAliquotService.listByLibraryIdList(libraryIds);
    return runLibraries.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public static class MoveSamplesRequest {
    public Long requisitionId;
    public String requisitionAlias;
    public Long assayId;
    public boolean stopped;
    public List<Long> sampleIds;
  }

  @PostMapping("/{requisitionId}/samples/move")
  public @ResponseBody RequisitionDto moveSamples(@PathVariable long requisitionId,
      @RequestBody MoveSamplesRequest request) throws IOException {
    List<Sample> samples = new ArrayList<>();
    for (Long id : request.sampleIds) {
      Sample sample = RestUtils.retrieve("Sample", id, sampleService, Status.BAD_REQUEST);
      samples.add(sample);
    }
    Requisition requisition = null;
    if (request.requisitionId != null) {
      requisition = RestUtils.retrieve("Requisition", request.requisitionId, requisitionService, Status.BAD_REQUEST);
    } else {
      requisition = new Requisition();
      requisition.setAlias(request.requisitionAlias);
      Assay assay = new Assay();
      assay.setId(request.assayId);
      requisition.setAssay(assay);
      requisition.setStopped(request.stopped);
    }
    Requisition saved = requisitionService.moveToRequisition(requisition, samples);
    return RequisitionDto.from(saved);
  }

}
