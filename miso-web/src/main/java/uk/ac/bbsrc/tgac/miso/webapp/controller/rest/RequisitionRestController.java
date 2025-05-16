package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.parseLocalDate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Requisitionable;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;
import uk.ac.bbsrc.tgac.miso.core.service.BulkSaveService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingBiFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import uk.ac.bbsrc.tgac.miso.dto.RunPartitionAliquotDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/requisitions")
public class RequisitionRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Requisition";
  private static final String SAMPLE_TYPE_LABEL = "Sample";
  private static final String LIBRARY_TYPE_LABEL = "Library";

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
    return addRequisitionedItems(requisitionId, ids, SAMPLE_TYPE_LABEL, sampleService);
  }

  private <T extends Requisitionable> ObjectNode addRequisitionedItems(long requisitionId, List<Long> ids,
      String typeLabel, BulkSaveService<T> service) throws IOException {
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<T> items = new ArrayList<>();
    for (Long id : ids) {
      T item = RestUtils.retrieve(typeLabel, id, service, Status.BAD_REQUEST);
      item.setRequisition(requisition);
      items.add(item);
    }
    return asyncOperationManager.startAsyncBulkUpdate(typeLabel, items, service);
  }

  @PostMapping("/{requisitionId}/samples/remove")
  public @ResponseBody ObjectNode removeSamples(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    return removeRequisitionedItems(requisitionId, ids, SAMPLE_TYPE_LABEL, sampleService);
  }

  public <T extends Requisitionable> ObjectNode removeRequisitionedItems(long requisitionId, List<Long> ids,
      String typeLabel, BulkSaveService<T> service) throws IOException {
    // Retrieve requisition just to validate existence
    RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<T> items = new ArrayList<>();
    for (Long id : ids) {
      T item = RestUtils.retrieve(typeLabel, id, service, Status.BAD_REQUEST);
      if (item.getRequisition() == null || item.getRequisition().getId() != requisitionId) {
        throw new RestException(
            String.format("%s (%s) is not linked to this requisition", item.getAlias(), item.getName()),
            Status.BAD_REQUEST);
      }
      item.setRequisition(null);
      items.add(item);
    }
    return asyncOperationManager.startAsyncBulkUpdate(typeLabel, items, service);
  }

  @PostMapping("/{requisitionId}/samples/move")
  public @ResponseBody RequisitionDto moveSamples(@PathVariable long requisitionId,
      @RequestBody MoveItemsRequest request) throws IOException {
    return moveRequisitionedItems(requisitionId, request, SAMPLE_TYPE_LABEL, sampleService,
        requisitionService::moveSamplesToRequisition);
  }

  public <T extends Requisitionable> RequisitionDto moveRequisitionedItems(long requisitionId, MoveItemsRequest request,
      String typeLabel, ProviderService<T> service,
      ThrowingBiFunction<Requisition, List<T>, Requisition, IOException> moveFunction) throws IOException {
    List<T> items = new ArrayList<>();
    for (Long id : request.itemIds) {
      T item = RestUtils.retrieve(typeLabel, id, service, Status.BAD_REQUEST);
      items.add(item);
    }
    Requisition requisition = null;
    if (request.requisitionId != null) {
      requisition = RestUtils.retrieve("Requisition", request.requisitionId, requisitionService, Status.BAD_REQUEST);
    } else {
      requisition = new Requisition();
      requisition.setAlias(request.requisitionAlias);
      Assay assay = new Assay();
      assay.setId(request.assayId);
      requisition.getAssays().add(assay);
      requisition.setStopped(request.stopped);
      requisition.setStopReason(request.stopReason);
    }
    Requisition saved = moveFunction.apply(requisition, items);
    return RequisitionDto.from(saved);
  }

  @GetMapping("/samplesupdate/{uuid}")
  public @ResponseBody ObjectNode getSampleProgress(@PathVariable String uuid) throws Exception {
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
      Sample sample = RestUtils.retrieve(SAMPLE_TYPE_LABEL, id, sampleService, Status.BAD_REQUEST);
      samples.add(sample);
    }
    return samples;
  }

  @PostMapping("/{requisitionId}/libraries")
  public @ResponseBody ObjectNode addLibraries(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    return addRequisitionedItems(requisitionId, ids, LIBRARY_TYPE_LABEL, libraryService);
  }

  @PostMapping("/{requisitionId}/libraries/remove")
  public @ResponseBody ObjectNode removeLibraries(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    return removeRequisitionedItems(requisitionId, ids, LIBRARY_TYPE_LABEL, libraryService);
  }

  @PostMapping("/{requisitionId}/libraries/move")
  public @ResponseBody RequisitionDto moveLibraries(@PathVariable long requisitionId,
      @RequestBody MoveItemsRequest request) throws IOException {
    return moveRequisitionedItems(requisitionId, request, LIBRARY_TYPE_LABEL, libraryService,
        requisitionService::moveLibrariesToRequisition);
  }

  @GetMapping("/librariesupdate/{uuid}")
  public @ResponseBody ObjectNode getLibraryProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Library.class);
  }

  @PostMapping("/{requisitionId}/supplementallibraries")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void addSupplementalLibraries(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<Library> libraries = getLibraries(ids);
    requisitionService.addSupplementalLibraries(requisition, libraries);
  }

  @PostMapping("/{requisitionId}/supplementallibraries/remove")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void removeSupplementalLibraries(@PathVariable long requisitionId, @RequestBody List<Long> ids)
      throws IOException {
    // Retrieve requisition just to validate existence
    Requisition requisition = RestUtils.retrieve(TYPE_LABEL, requisitionId, requisitionService, Status.NOT_FOUND);
    List<Library> libraries = getLibraries(ids);
    requisitionService.removeSupplementalLibraries(requisition, libraries);
  }

  private List<Library> getLibraries(List<Long> ids) throws IOException {
    List<Library> libraries = new ArrayList<>();
    for (Long id : ids) {
      Library library = RestUtils.retrieve(LIBRARY_TYPE_LABEL, id, libraryService, Status.BAD_REQUEST);
      libraries.add(library);
    }
    return libraries;
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
    // requisitioned
    List<Long> libraryIds = libraryService.list(0, 0, false, null, PaginationFilter.requisitionId(requisitionId))
        .stream().map(Library::getId).collect(Collectors.toCollection(() -> new ArrayList<>()));
    // supplemental
    libraryIds.addAll(libraryService.list(0, 0, false, null,
        PaginationFilter.supplementalToRequisitionId(requisitionId)).stream().map(Library::getId).toList());
    // prepared
    libraryIds.addAll(libraryService.listPreparedIdsByRequisitionId(requisitionId));

    List<RunPartitionAliquot> runLibraries = runPartitionAliquotService.listByLibraryIdList(libraryIds);
    return runLibraries.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public record MoveItemsRequest(Long requisitionId, String requisitionAlias, Long assayId, boolean stopped,
      String stopReason, List<Long> itemIds) {
  }

  /*
   * @GetMapping("{requisitionId}/arrayruns") public @ResponseBody List<ArrayRunDto>
   * listArrayRuns(@PathVariable long requisitionId, @RequestBody List<Long> ids) throws IOException {
   * List<Long> arrayRunIds =
   * 
   * 
   * }
   * 
   * need to write the service class to use this maybe here you could get samples, then call the
   * arrayservice that returns the arrayrun associated with that sample - if the sample has no array
   * run associated with it but it has a parent, then check the parent recursively - to make this
   * controller method cleaner, do that recursive searching in the array run service method (also
   * kinda feels like a bit of a mess to do it here ngl)
   * 
   * 
   * following the structure of the above method, do the following - get the relevant array run IDs -
   * get the list of the actual objects from those IDs - map to ArrayRunDtos at the end
   * 
   */

  @PostMapping("/paused")
  public @ResponseBody List<RequisitionDto> searchPaused(@RequestBody List<Long> requisitionIds) throws IOException {
    List<Requisition> requisitions = requisitionService.list(0, 0, false, "id", PaginationFilter.status("paused"),
        PaginationFilter.ids(requisitionIds));
    return requisitions.stream().map(RequisitionDto::from).toList();
  }

  private static class BulkResumeRequest {
    public List<Long> requisitionIds;
    public String resumeDate;
  }

  @PostMapping("/bulk-resume")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkResume(@RequestBody BulkResumeRequest request) throws IOException {
    if (request.requisitionIds == null || request.requisitionIds.isEmpty()) {
      throw new RestException("Requisition IDs must be specified", Status.BAD_REQUEST);
    }
    if (request.resumeDate == null) {
      throw new RestException("Resume date must be specified", Status.BAD_REQUEST);
    }
    LocalDate resumeDate = null;
    try {
      resumeDate = parseLocalDate(request.resumeDate);
    } catch (IllegalArgumentException e) {
      throw new RestException("Invalid date format: %s".formatted(request.resumeDate), Status.BAD_REQUEST, e);
    }
    List<Requisition> requisitions = requisitionService.listByIdList(request.requisitionIds);
    for (Requisition requisition : requisitions) {
      for (RequisitionPause pause : requisition.getPauses()) {
        if (pause.getEndDate() == null) {
          pause.setEndDate(resumeDate);
        }
      }
    }
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, requisitions, requisitionService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getRequisitionProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Requisition.class);
  }



}
