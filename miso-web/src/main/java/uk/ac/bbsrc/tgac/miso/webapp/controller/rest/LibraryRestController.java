package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
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
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibrarySpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

/**
 * A controller to handle all REST requests for Libraries
 * 
 * @author Rob Davey
 * @date 16-Aug-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/libraries")
public class LibraryRestController extends RestController {

  private static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<Library, LibraryDto> jQueryBackend = new JQueryDataTableBackend<>() {
    @Override
    protected LibraryDto asDto(Library model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Library> getSource() throws IOException {
      return libraryService;
    }
  };

  @Autowired
  private LibraryService libraryService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private WorksetService worksetService;
  @Autowired
  private SampleRestController sampleController;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @GetMapping(value = "/{libraryId}", produces = "application/json")
  @ResponseBody
  public LibraryDto getLibraryById(@PathVariable Long libraryId) throws IOException {
    return RestUtils.getObject("Library", libraryId, libraryService, lib -> Dtos.asDto(lib, false));
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LibraryDto createLibrary(@RequestBody LibraryDto libraryDto) throws IOException {
    RestUtils.validateDtoProvided("Library", libraryDto);
    Library library = buildHierarchy(libraryDto);
    RestUtils.validateNewObject("Library", library);
    long savedId = libraryService.create(library);
    return Dtos.asDto(libraryService.get(savedId), false);
  }

  private Library buildHierarchy(LibraryDto dto) throws IOException {
    Library library = Dtos.to(dto);
    if (dto.getSample() != null) {
      Sample sample = sampleController.buildHierarchy(dto.getSample());
      if (LimsUtils.isDetailedSample(sample)) {
        ((DetailedSample) sample).setSynthetic(true);
      }
      library.setSample(sample);
    }
    return library;
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LibraryDto updateLibrary(@PathVariable("id") long id, @RequestBody LibraryDto libraryDto) throws IOException {
    return RestUtils.updateObject("Library", id, libraryDto, Dtos::to, libraryService, lib -> Dtos.asDto(lib, false));
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibraries(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibrariesForProject(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  @GetMapping(value = "/dt/batch/{batchId:.+}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibrariesForBatch(@PathVariable("batchId") String batchId,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.batchId(batchId));
  }

  @GetMapping(value = "/dt/requisition/{id}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getDTSamplesByRequisition(@PathVariable("id") long id,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.requisitionId(id));
  }

  @GetMapping(value = "/dt/requisition-supplemental/{id}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getDTSamplesByRequisitionSupplemental(@PathVariable("id") long id,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.supplementalToRequisitionId(id));
  }

  @GetMapping(value = "/dt/requisition-prepared/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibrariesForRequisition(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    List<Long> libraryIds = libraryService.listIdsByRequisitionId(id);
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.ids(libraryIds));
  }

  @GetMapping(value = "/dt/workstation/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> dataTableByWorkset(@PathVariable("id") Long id,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.workstationId(id));
  }

  @PostMapping(value = "/query", produces = {"application/json"})
  @ResponseBody
  public List<LibraryDto> getLibrariesInBulk(@RequestBody List<String> names, HttpServletRequest request)
      throws IOException {
    return libraryService.list(0, 0, true, "id", PaginationFilter.bulkLookup(names))
        .stream()
        .map(lib -> Dtos.asDto(lib, false))
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/spreadsheet", produces = "application/octet-stream")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response)
      throws IOException {
    return MisoWebUtils.generateSpreadsheet(request, libraryService::listByIdList, detailedSample,
        LibrarySpreadSheets::valueOf, response);
  }

  private static Stream<Sample> getSample(Library library) {
    return Stream.of(library.getSample());
  }

  private final RelationFinder<Library> parentFinder = (new RelationFinder<Library>() {

    @Override
    protected List<Library> fetchByIds(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids);
    }
  })//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.RelationAdapter<Library, Sample, SampleDto>("Sample") {

        @Override
        public SampleDto asDto(Sample model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Sample> find(Library model, Consumer<String> emitError) {
          return Stream.of(model.getSample());
        }
      });

  @PostMapping(value = "/parents/{category}")
  @ResponseBody
  public List<?> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids,
      HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<Library> childFinder = (new RelationFinder<Library>() {

    @Override
    protected List<Library> fetchByIds(List<Long> ids) throws IOException {
      return libraryService.listByIdList(ids);
    }
  })//
      .add(new RelationFinder.RelationAdapter<Library, LibraryAliquot, LibraryAliquotDto>("Library Aliquot") {

        @Override
        public LibraryAliquotDto asDto(LibraryAliquot model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<LibraryAliquot> find(Library model, Consumer<String> emitError) {
          Collection<LibraryAliquot> children = model.getLibraryAliquots();
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      })
      .add(new RelationFinder.RelationAdapter<Library, Pool, PoolDto>("Pool") {

        @Override
        public PoolDto asDto(Pool model) {
          return Dtos.asDto(model, false, false, indexChecker);
        }

        @Override
        public Stream<Pool> find(Library model, Consumer<String> emitError) {
          Set<Pool> children = new HashSet<>();
          for (LibraryAliquot aliquot : model.getLibraryAliquots()) {
            try {
              children.addAll(poolService.listByLibraryAliquotId(aliquot.getId()));
            } catch (IOException e) {
              log.error("Failed looking up pools for library", e);
              emitError.accept("Database error");
              return Stream.empty();
            }
          }
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      })
      .add(new RelationFinder.RelationAdapter<Library, Run, RunDto>("Run") {

        @Override
        public RunDto asDto(Run model) {
          return Dtos.asDto(model);
        }

        @Override
        public Stream<Run> find(Library model, Consumer<String> emitError) throws IOException {
          Set<Run> children = new HashSet<>();
          for (LibraryAliquot aliquot : model.getLibraryAliquots()) {
            children.addAll(runService.listByLibraryAliquotId(aliquot.getId()));
          }
          return children.stream();
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
    List<Library> libraries = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null library", Status.BAD_REQUEST);
      }
      Library library = libraryService.get(id);
      if (library == null) {
        throw new RestException("Library " + id + " not found", Status.BAD_REQUEST);
      }
      libraries.add(library);
    }
    libraryService.bulkDelete(libraries);
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<LibraryDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate("Library", dtos, WhineyFunction.rethrow(this::buildHierarchy),
        libraryService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<LibraryDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate("Library", dtos, WhineyFunction.rethrow(this::buildHierarchy),
        libraryService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Library.class, libraryService, lib -> Dtos.asDto(lib, false));
  }

  @GetMapping(value = "/dt/workset/{id}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getDTLibrariesByWorkset(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    DataTablesResponseDto<LibraryDto> response =
        jQueryBackend.get(request, advancedSearchParser, PaginationFilter.workset(id));
    if (!response.getAaData().isEmpty()) {
      Map<Long, Date> addedTimes = worksetService.getLibraryAddedTimes(id);
      for (LibraryDto dto : response.getAaData()) {
        dto.setWorksetAddedTime(LimsUtils.formatDateTime(addedTimes.get(dto.getId())));
      }
    }
    return response;
  }

}
