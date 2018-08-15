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

import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.web.util.UriComponents;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryDilutionSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/librarydilution")
public class LibraryDilutionRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryDilutionRestController.class);

  private final JQueryDataTableBackend<PoolableElementView, DilutionDto> jQueryBackend = new JQueryDataTableBackend<PoolableElementView, DilutionDto>() {
    @Override
    protected DilutionDto asDto(PoolableElementView model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<PoolableElementView> getSource() throws IOException {
      return poolableElementViewService;
    }
  };

  @Autowired
  private LibraryDilutionService dilutionService;

  @Autowired
  private PoolableElementViewService poolableElementViewService;

  @Autowired
  private PoolService poolService;

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  @GetMapping(value = "{dilutionId}", produces = "application/json")
  @ResponseBody
  public DilutionDto getDilution(@PathVariable Long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    DilutionDto dilutionDto = Dtos.asDto(dilution, false, false);
    return dilutionDto;
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public DilutionDto createDilution(@RequestBody DilutionDto dilutionDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    if (dilutionDto == null) {
      log.error(
          "Received null dilutionDto from front end; cannot convert to Dilution. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Dilution", Status.BAD_REQUEST);
    }
    Long id = null;
    LibraryDilution dilution;
    try {
      dilution = Dtos.to(dilutionDto);
      id = dilutionService.create(dilution);
    } catch (ConstraintViolationException e) {
      log.error("Error while creating dilution", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      restException.addData("constraintName", e.getConstraintName());
      throw restException;
    }
    UriComponents uriComponents = b.path("/{id}").buildAndExpand(dilution.getId());
    response.setHeader("Location", uriComponents.toUri().toString());
    return getDilution(id);
  }

  @PutMapping(value = "{dilutionId}", headers = { "Content-type=application/json" })
  @ResponseBody
  public DilutionDto updateDilution(@PathVariable Long dilutionId, @RequestBody DilutionDto dilutionDto)
      throws IOException {
    if (dilutionDto == null) {
      log.error(
          "Received null dilutionDto from front end; cannot convert to Dilution. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Dilution", Status.BAD_REQUEST);
    }
    LibraryDilution dilution = Dtos.to(dilutionDto);
    dilution.setId(dilutionId);
    dilutionService.update(dilution);
    return getDilution(dilutionId);
  }

  @GetMapping(value = "dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutions(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutionsByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @GetMapping(value = "dt/pool/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<DilutionDto> availableDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {

    final Pool pool = poolService.get(poolId);
    return jQueryBackend.get(request, response, null, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @PostMapping(value = "query", produces = { "application/json" })
  @ResponseBody
  public List<DilutionDto> getLibraryDilutionsInBulk(@RequestBody List<String> names, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, dilutionService, ldi -> Dtos.asDto(ldi, false, false),
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @GetMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(HttpServletRequest request, HttpServletResponse response, UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(dilutionService::get, LibraryDilutionSpreadSheets::valueOf, request, response);
  }

  private static Stream<Sample> getSample(LibraryDilution dilution) {
    return Stream.of(dilution.getLibrary().getSample());
  }
  private final RelationFinder<LibraryDilution> parentFinder = (new RelationFinder<LibraryDilution>() {

    @Override
    protected LibraryDilution fetch(long id) throws IOException {
      return dilutionService.get(id);
    }
  })//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class, LibraryDilutionRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class, LibraryDilutionRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryDilutionRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class, LibraryDilutionRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class, LibraryDilutionRestController::getSample))//
      .add(new RelationFinder.RelationAdapter<LibraryDilution, Sample, SampleDto>("Sample") {

        @Override
        public SampleDto asDto(Sample model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Sample> find(LibraryDilution model, Consumer<String> emitError) {
          return Stream.of(model.getLibrary().getSample());
        }
      })//
      .add(new RelationFinder.RelationAdapter<LibraryDilution, Library, LibraryDto>("Library") {

        @Override
        public LibraryDto asDto(Library model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Library> find(LibraryDilution model, Consumer<String> emitError) {
          return Stream.of(model.getLibrary());
        }
      });

  @PostMapping(value = "/parents/{category}")
  @ResponseBody
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<LibraryDilution> childFinder = (new RelationFinder<LibraryDilution>() {

    @Override
    protected LibraryDilution fetch(long id) throws IOException {
      return dilutionService.get(id);
    }
  })//
      .add(new RelationFinder.RelationAdapter<LibraryDilution, Pool, PoolDto>("Pool") {

        @Override
        public PoolDto asDto(Pool model) {
          return Dtos.asDto(model, false, false);
        }

        @Override
        public Stream<Pool> find(LibraryDilution model, Consumer<String> emitError) {
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
    List<LibraryDilution> dilutions = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null library dilution", Status.BAD_REQUEST);
      }
      LibraryDilution dilution = dilutionService.get(id);
      if (dilution == null) {
        throw new RestException("Library Dilution " + id + " not found", Status.BAD_REQUEST);
      }
      dilutions.add(dilution);
    }
    dilutionService.bulkDelete(dilutions);
  }

}
