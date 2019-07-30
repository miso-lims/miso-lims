package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryAliquotSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.DuplicateIndicesChecker;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/libraryaliquots")
public class LibraryAliquotRestController extends RestController {

  private final JQueryDataTableBackend<PoolableElementView, LibraryAliquotDto> jQueryBackend = new JQueryDataTableBackend<PoolableElementView, LibraryAliquotDto>() {
    @Override
    protected LibraryAliquotDto asDto(PoolableElementView model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<PoolableElementView> getSource() throws IOException {
      return poolableElementViewService;
    }
  };

  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private DuplicateIndicesChecker indexChecker;

  public void setLibraryAliquotService(LibraryAliquotService libraryAliquotService) {
    this.libraryAliquotService = libraryAliquotService;
  }

  @GetMapping(value = "/{aliquotId}", produces = "application/json")
  @ResponseBody
  public LibraryAliquotDto get(@PathVariable Long aliquotId) throws IOException {
    return RestUtils.getObject("Library Aliquot", aliquotId, libraryAliquotService, ldi -> Dtos.asDto(ldi, false));
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public LibraryAliquotDto create(@RequestBody LibraryAliquotDto aliquotDto)
      throws IOException {
    return RestUtils.createObject("Library Aliquot", aliquotDto, Dtos::to, libraryAliquotService, ldi -> Dtos.asDto(ldi, false));
  }

  @PutMapping(value = "/{aliquotId}", headers = { "Content-type=application/json" })
  @ResponseBody
  public LibraryAliquotDto update(@PathVariable Long aliquotId, @RequestBody LibraryAliquotDto aliquotDto) throws IOException {
    return RestUtils.updateObject("Library Aliquot", aliquotId, aliquotDto, Dtos::to, libraryAliquotService,
        ldi -> Dtos.asDto(ldi, false));
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryAliquotDto> getLibraryAliquots(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryAliquotDto> getByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @GetMapping(value = "/dt/pool/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<LibraryAliquotDto> getAvailable(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {

    final Pool pool = poolService.get(poolId);
    return jQueryBackend.get(request, response, null, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @PostMapping(value = "query", produces = { "application/json" })
  @ResponseBody
  public List<LibraryAliquotDto> getLibraryAliquotsInBulk(@RequestBody List<String> names, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, libraryAliquotService, ldi -> Dtos.asDto(ldi, false),
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @PostMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(request, libraryAliquotService::get, LibraryAliquotSpreadSheets::valueOf, response);
  }

  private static Stream<Sample> getSample(LibraryAliquot aliquot) {
    return Stream.of(aliquot.getLibrary().getSample());
  }
  private final RelationFinder<LibraryAliquot> parentFinder = (new RelationFinder<LibraryAliquot>() {

    @Override
    protected LibraryAliquot fetch(long id) throws IOException {
      return libraryAliquotService.get(id);
    }
  })//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class, LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class, LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class, LibraryAliquotRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class, LibraryAliquotRestController::getSample))//
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
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<LibraryAliquot> childFinder = (new RelationFinder<LibraryAliquot>() {

    @Override
    protected LibraryAliquot fetch(long id) throws IOException {
      return libraryAliquotService.get(id);
    }
  })//
      .add(new RelationFinder.RelationAdapter<LibraryAliquot, Pool, PoolDto>("Pool") {

        @Override
        public PoolDto asDto(Pool model) {
          return Dtos.asDto(model, false, false, indexChecker);
        }

        @Override
        public Stream<Pool> find(LibraryAliquot model, Consumer<String> emitError) {
          Set<Pool> children = model.getPools();
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      });

  @PostMapping(value = "/children/{category}")
  @ResponseBody
  public HttpEntity<byte[]> getChildren(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
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

}
