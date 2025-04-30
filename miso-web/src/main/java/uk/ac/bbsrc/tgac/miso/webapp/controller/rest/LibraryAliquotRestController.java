package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryAliquotSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.ListLibraryAliquotViewService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/libraryaliquots")
public class LibraryAliquotRestController extends AbstractRestController {

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<ListLibraryAliquotView, LibraryAliquotDto> jQueryBackend =
      new JQueryDataTableBackend<>() {
        @Override
        protected LibraryAliquotDto asDto(ListLibraryAliquotView model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<ListLibraryAliquotView> getSource() throws IOException {
          return listLibraryAliquotViewService;
        }
      };

  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private ListLibraryAliquotViewService listLibraryAliquotViewService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setLibraryAliquotService(LibraryAliquotService libraryAliquotService) {
    this.libraryAliquotService = libraryAliquotService;
  }

  @GetMapping(value = "/{aliquotId}", produces = "application/json")
  @ResponseBody
  public LibraryAliquotDto get(@PathVariable Long aliquotId) throws IOException {
    return RestUtils.getObject("Library Aliquot", aliquotId, libraryAliquotService, ldi -> Dtos.asDto(ldi, false));
  }

  @PostMapping(headers = {"Content-type=application/json"})
  @ResponseBody
  public LibraryAliquotDto create(@RequestBody LibraryAliquotDto aliquotDto)
      throws IOException {
    return RestUtils.createObject("Library Aliquot", aliquotDto, Dtos::to, libraryAliquotService,
        ldi -> Dtos.asDto(ldi, false));
  }

  @PutMapping(value = "/{aliquotId}", headers = {"Content-type=application/json"})
  @ResponseBody
  public LibraryAliquotDto update(@PathVariable Long aliquotId, @RequestBody LibraryAliquotDto aliquotDto)
      throws IOException {
    return RestUtils.updateObject("Library Aliquot", aliquotId, aliquotDto, Dtos::to, libraryAliquotService,
        ldi -> Dtos.asDto(ldi, false));
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<LibraryAliquotDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate("Library Aliquot", dtos, Dtos::to, libraryAliquotService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<LibraryAliquotDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate("Library Aliquot", dtos, Dtos::to, libraryAliquotService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, LibraryAliquot.class, libraryAliquotService,
        ali -> Dtos.asDto(ali, false));
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryAliquotDto> getLibraryAliquots(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryAliquotDto> getByProject(@PathVariable("id") Long id, HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  @GetMapping(value = "/dt/pool/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<LibraryAliquotDto> getAvailable(@PathVariable("id") Long poolId,
      HttpServletRequest request)
      throws IOException {

    final Pool pool = poolService.get(poolId);
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @PostMapping(value = "/query", produces = {"application/json"})
  @ResponseBody
  public List<LibraryAliquotDto> getLibraryAliquotsInBulk(@RequestBody List<String> names) throws IOException {
    return listLibraryAliquotViewService.list(0, 0, true, "id", PaginationFilter.bulkLookup(names))
        .stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/spreadsheet", produces = "application/octet-stream")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return MisoWebUtils.generateSpreadsheet(request, libraryAliquotService::listByIdList, detailedSample,
        LibraryAliquotSpreadSheets::valueOf, response);
  }

  private static Stream<Sample> getSample(LibraryAliquot aliquot) {
    return Stream.of(aliquot.getLibrary().getSample());
  }

  private final RelationFinder<LibraryAliquot> parentFinder = (new RelationFinder<LibraryAliquot>() {

    @Override
    protected List<LibraryAliquot> fetchByIds(List<Long> ids) throws IOException {
      return libraryAliquotService.listByIdList(ids);
    }
  })//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.RelationAdapter<LibraryAliquot, Sample, SampleDto>("Sample") {

        @Override
        public SampleDto asDto(Sample model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Sample> find(LibraryAliquot model, Consumer<String> emitError) {
          return Stream.of(model.getLibrary().getSample());
        }
      })//
      .add(new RelationFinder.RelationAdapter<LibraryAliquot, Library, LibraryDto>("Library") {

        @Override
        public LibraryDto asDto(Library model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Library> find(LibraryAliquot model, Consumer<String> emitError) {
          return Stream.of(model.getLibrary());
        }
      });

  @PostMapping(value = "/parents/{category}")
  @ResponseBody
  public List<?> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids,
      HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<LibraryAliquot> childFinder = (new RelationFinder<LibraryAliquot>() {

    @Override
    protected List<LibraryAliquot> fetchByIds(List<Long> ids) throws IOException {
      return libraryAliquotService.listByIdList(ids);
    }
  })//
      .add(new RelationFinder.RelationAdapter<LibraryAliquot, Pool, PoolDto>("Pool") {

        @Override
        public PoolDto asDto(Pool model) {
          return Dtos.asDto(model, false, false, indexChecker);
        }

        @Override
        public Stream<Pool> find(LibraryAliquot model, Consumer<String> emitError) throws IOException {
          List<Pool> children = poolService.listByLibraryAliquotId(model.getId());
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      })
      .add(new RelationFinder.RelationAdapter<LibraryAliquot, Run, RunDto>("Run") {

        @Override
        public RunDto asDto(Run model) {
          return Dtos.asDto(model);
        }

        @Override
        public Stream<Run> find(LibraryAliquot model, Consumer<String> emitError) throws IOException {
          return runService.listByLibraryAliquotId(model.getId()).stream();
        }

      });

  @PostMapping(value = "/children/{category}")
  @ResponseBody
  public List<?> getChildren(@PathVariable("category") String category, @RequestBody List<Long> ids,
      HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return childFinder.list(ids, category);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<LibraryAliquot> aliquots = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null library aliquot", Status.BAD_REQUEST);
      }
      LibraryAliquot aliquot = libraryAliquotService.get(id);
      if (aliquot == null) {
        throw new RestException("Library aliquot " + id + " not found", Status.BAD_REQUEST);
      }
      aliquots.add(aliquot);
    }
    libraryAliquotService.bulkDelete(aliquots);
  }

  @GetMapping(value = "/dt/workset/{id}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<LibraryAliquotDto> getDTLibraryAliquotsByWorkset(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    DataTablesResponseDto<LibraryAliquotDto> response =
        jQueryBackend.get(request, advancedSearchParser, PaginationFilter.workset(id));
    if (!response.getAaData().isEmpty()) {
      Map<Long, Date> addedTimes = worksetService.getLibraryAliquotAddedTimes(id);
      for (LibraryAliquotDto dto : response.getAaData()) {
        dto.setWorksetAddedTime(LimsUtils.formatDateTime(addedTimes.get(dto.getId())));
      }
    }
    return response;
  }

}
