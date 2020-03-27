package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.NotFoundException;
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

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.TriConsumer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ListWorksetViewDto;
import uk.ac.bbsrc.tgac.miso.dto.WorksetDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.ClientErrorException;

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
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<ListWorksetView, ListWorksetViewDto> jQueryBackend = new JQueryDataTableBackend<ListWorksetView, ListWorksetViewDto>() {

    @Override
    protected ListWorksetViewDto asDto(ListWorksetView model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<ListWorksetView> getSource() throws IOException {
      return worksetService;
    }

  };

  @GetMapping(value = "/dt/all", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<ListWorksetViewDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/mine", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<ListWorksetViewDto> dataTableForUser(HttpServletRequest request) throws IOException {
    String username = authorizationManager.getCurrentUser().getLoginName();
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.user(username, true));
  }

  @GetMapping
  public @ResponseBody List<ListWorksetViewDto> queryWorksets(@RequestParam String q) throws IOException {
    return worksetService.listBySearch(q).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping
  public @ResponseBody WorksetDto createWorkset(@RequestBody WorksetDto dto) throws IOException {
    return RestUtils.createObject("Workset", dto, Dtos::to, worksetService, Dtos::asDto);
  }

  @PutMapping(value = "/{worksetId}")
  public @ResponseBody WorksetDto updateWorkset(@RequestBody WorksetDto dto,
      @PathVariable(value = "worksetId", required = true) long worksetId) throws IOException {
    return RestUtils.updateObject("Workset", worksetId, dto, Dtos::to, worksetService, Dtos::asDto);
  }

  @PostMapping(value = "/{worksetId}/samples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addSamples(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> sampleIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Sample", workset.getSamples(), sampleIds, WhineyFunction.rethrow(sampleService::get));
    worksetService.update(workset);
  }

  @PostMapping(value = "/{worksetId}/libraries")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addLibraries(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> libraryIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Library", workset.getLibraries(), libraryIds, WhineyFunction.rethrow(libraryService::get));
    worksetService.update(workset);
  }

  @PostMapping(value = "/{worksetId}/libraryaliquots")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addLibraryAliquots(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> aliquotIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    addByIds("Library aliquot", workset.getLibraryAliquots(), aliquotIds, WhineyFunction.rethrow(libraryAliquotService::get));
    worksetService.update(workset);
  }

  @DeleteMapping("/{worksetId}/samples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeSamples(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> sampleIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Sample", workset.getSamples(), sampleIds);
    worksetService.update(workset);
  }

  @DeleteMapping("/{worksetId}/libraries")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeLibraries(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> libraryIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Library", workset.getLibraries(), libraryIds);
    worksetService.update(workset);
  }

  @DeleteMapping("/{worksetId}/libraryaliquots")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeLibraryAliquots(@PathVariable(value = "worksetId", required = true) long worksetId, @RequestBody List<Long> aliquotIds)
      throws IOException {
    Workset workset = getWorkset(worksetId);
    removeByIds("Library aliquot", workset.getLibraryAliquots(), aliquotIds);
    worksetService.update(workset);
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
      workset.getLibraryAliquots().addAll(child.getLibraryAliquots());
    }
    worksetService.create(workset);
    return Dtos.asDto(workset);
  }

  public static class MoveItemsDto {

    private Long targetWorksetId;
    private List<Long> itemIds;

    public Long getTargetWorksetId() {
      return targetWorksetId;
    }

    public void setTargetWorksetId(Long targetWorksetId) {
      this.targetWorksetId = targetWorksetId;
    }

    public List<Long> getItemIds() {
      return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
      this.itemIds = itemIds;
    }

  }

  @PostMapping("/{worksetId}/samples/move")
  public @ResponseBody void moveSamples(@PathVariable long worksetId, @RequestBody MoveItemsDto dto) throws IOException {
    moveItems(worksetId, dto, "Sample", sampleService, worksetService::moveSamples);
  }

  @PostMapping("/{worksetId}/libraries/move")
  public @ResponseBody void moveLibraries(@PathVariable long worksetId, @RequestBody MoveItemsDto dto) throws IOException {
    moveItems(worksetId, dto, "Library", libraryService, worksetService::moveLibraries);
  }

  @PostMapping("/{worksetId}/libraryaliquots/move")
  public @ResponseBody void moveLibraryAliquots(@PathVariable long worksetId, @RequestBody MoveItemsDto dto) throws IOException {
    moveItems(worksetId, dto, "Library aliquot", libraryAliquotService, worksetService::moveLibraryAliquots);
  }

  private <T extends Identifiable> void moveItems(long sourceWorksetId, MoveItemsDto dto, String itemTypeName, ProviderService<T> service,
      TriConsumer<Workset, Workset, Collection<T>> moveFunction) throws IOException {
    Workset sourceWorkset = worksetService.get(sourceWorksetId);
    if (sourceWorkset == null) {
      throw new NotFoundException(String.format("Source workset %d not found", sourceWorksetId));
    }
    if (dto.getTargetWorksetId() == null) {
      throw new ClientErrorException("Target workset ID missing");
    } else if (dto.getItemIds() == null || dto.getItemIds().isEmpty()) {
      throw new ClientErrorException("Item IDs missing");
    }
    Workset targetWorkset = worksetService.get(dto.getTargetWorksetId());
    if (targetWorkset == null) {
      throw new ClientErrorException(String.format("Target workset %d not found", dto.getTargetWorksetId()));
    }
    List<T> items = new ArrayList<>();
    for (Long id : dto.getItemIds()) {
      T item = service.get(id);
      if (item == null) {
        throw new ClientErrorException(String.format("%s %d not found", itemTypeName, id));
      }
      items.add(item);
    }
    moveFunction.accept(sourceWorkset, targetWorkset, items);
  }

}
