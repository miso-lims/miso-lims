package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.WorksetDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/worksets")
public class WorksetRestController extends RestController {

  @Autowired
  private WorksetService worksetService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private AuthorizationManager authorizationManager;

  private final JQueryDataTableBackend<Workset, WorksetDto> jQueryBackend = new JQueryDataTableBackend<Workset, WorksetDto>() {

    @Override
    protected WorksetDto asDto(Workset model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Workset> getSource() throws IOException {
      return worksetService;
    }

  };

  @GetMapping(value = "/dt/all", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<WorksetDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/mine", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<WorksetDto> dataTableForUser(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    String username = authorizationManager.getCurrentUser().getLoginName();
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.user(username, true));
  }

  @GetMapping
  public @ResponseBody List<WorksetDto> queryWorksets(@RequestParam String q) {
    return worksetService.listBySearch(q).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping
  public @ResponseBody WorksetDto createWorkset(@RequestBody WorksetDto dto) throws IOException {
    return doSave(dto);
  }

  @PutMapping(value = "/{worksetId}")
  public @ResponseBody WorksetDto updateWorkset(@RequestBody WorksetDto dto,
      @PathVariable(value = "worksetId", required = true) long worksetId) throws IOException {
    if (dto.getId().longValue() != worksetId) {
      throw new RestException("Workset ID mismatch", Status.BAD_REQUEST);
    }
    // call to make sure it exists or throw not found
    getWorkset(worksetId);
    return doSave(dto);
  }

  private WorksetDto doSave(WorksetDto dto) throws IOException {
    Workset workset = Dtos.to(dto);
    long savedId = worksetService.save(workset);
    return Dtos.asDto(worksetService.get(savedId));
  }

  @PostMapping(value = "/{worksetId}/samples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addSamples(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> sampleIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Sample", workset.getSamples(), sampleIds, WhineyFunction.rethrow(id -> sampleService.get(id)));
    worksetService.save(workset);
  }

  @PostMapping(value = "/{worksetId}/libraries")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addLibraries(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> libraryIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Library", workset.getLibraries(), libraryIds, WhineyFunction.rethrow(id -> libraryService.get(id)));
    worksetService.save(workset);
  }

  @PostMapping(value = "/{worksetId}/dilutions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addDilutions(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> dilutionIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Dilution", workset.getDilutions(), dilutionIds, WhineyFunction.rethrow(id -> dilutionService.get(id)));
    worksetService.save(workset);
  }

  @DeleteMapping("/{worksetId}/samples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeSamples(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> sampleIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Sample", workset.getSamples(), sampleIds);
    worksetService.save(workset);
  }

  @DeleteMapping("/{worksetId}/libraries")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeLibraries(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> libraryIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Library", workset.getLibraries(), libraryIds);
    worksetService.save(workset);
  }

  @DeleteMapping("/{worksetId}/dilutions")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeDilutions(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> dilutionIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Dilution", workset.getDilutions(), dilutionIds);
    worksetService.save(workset);
  }

  private Workset getWorkset(long id) throws IOException {
    Workset workset = worksetService.get(id);
    if (workset == null) {
      throw new RestException("Workset not found", Status.NOT_FOUND);
    }
    return workset;
  }

  private <T extends Identifiable> void addByIds(String typeName, Collection<T> collection, List<Long> ids, Function<Long, T> getter) {
    for (Long id : ids) {
      T item = getter.apply(id);
      if (item == null) {
        throw new RestException(String.format("%s %d not found", typeName, id), Status.BAD_REQUEST);
      }
      collection.add(item);
    }
  }

  private <T extends Identifiable> void removeByIds(String typeName, Collection<T> collection, Collection<Long> ids) {
    for (Long id : ids) {
      if (!collection.removeIf(item -> item.getId() == id.longValue())) {
        throw new RestException(String.format("%s %d not found in workset", typeName, id), Status.BAD_REQUEST);
      }
    }
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<Workset> worksets = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null workset", Status.BAD_REQUEST);
      }
      Workset workset = worksetService.get(id);
      if (workset == null) {
        throw new RestException("Workset " + id + " not found", Status.BAD_REQUEST);
      }
      worksets.add(workset);
    }
    worksetService.bulkDelete(worksets);
  }

  private static class MergeWorksetsRequestData {

    private List<Long> ids;
    private String alias;
    private String description;

    public List<Long> getIds() {
      return ids;
    }

    public String getAlias() {
      return alias;
    }

    public String getDescription() {
      return description;
    }

  }

  @PostMapping("/merge")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody WorksetDto mergeWorksets(@RequestBody(required = true) MergeWorksetsRequestData data) throws IOException {
    Workset workset = new Workset();
    if (LimsUtils.isStringEmptyOrNull(data.getAlias())) {
      throw new RestException("No alias provided for new workset", Status.BAD_REQUEST);
    }
    workset.setAlias(data.getAlias());
    if (!LimsUtils.isStringEmptyOrNull(data.getDescription())) {
      workset.setDescription(data.getDescription());
    }
    for (Long id : data.getIds()) {
      Workset child = worksetService.get(id);
      if (child == null) {
        throw new RestException("No workset found with ID: " + id, Status.BAD_REQUEST);
      }
      workset.getSamples().addAll(child.getSamples());
      workset.getLibraries().addAll(child.getLibraries());
      workset.getDilutions().addAll(child.getDilutions());
    }
    worksetService.save(workset);
    return Dtos.asDto(workset);
  }

}
