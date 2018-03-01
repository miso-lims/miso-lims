package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;

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

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DilutionDto getDilution(@PathVariable Long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    DilutionDto dilutionDto = Dtos.asMinimalDto(dilution);
    return dilutionDto;
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
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

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
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

  @RequestMapping(value = "dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutions(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutionsByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "dt/pool/{id}/available", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<DilutionDto> availableDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {

    final Pool pool = poolService.get(poolId);
    return jQueryBackend.get(request, response, null, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @RequestMapping(value = "dt/pool/{id}/included", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<DilutionDto> includedDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, null, PaginationFilter.pool(poolId));
  }

  @RequestMapping(value = "query", method = RequestMethod.POST, produces = { "application/json" })
  @ResponseBody
  public List<DilutionDto> getLibraryDilutionsInBulk(@RequestBody List<String> names, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, dilutionService, Dtos::asDto, message -> new RestException(message, Status.BAD_REQUEST));
  }

  private static Sample getSample(LibraryDilution dilution) {
    return dilution.getLibrary().getSample();
  }
  private final ParentFinder<LibraryDilution> parentFinder = (new ParentFinder<LibraryDilution>() {

    @Override
    protected LibraryDilution fetch(long id) throws IOException {
      return dilutionService.get(id);
    }
  })//
      .add(new ParentFinder.SampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class, LibraryDilutionRestController::getSample))//
      .add(new ParentFinder.SampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class, LibraryDilutionRestController::getSample))//
      .add(new ParentFinder.SampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryDilutionRestController::getSample))//
      .add(new ParentFinder.SampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class, LibraryDilutionRestController::getSample))//
      .add(new ParentFinder.SampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class, LibraryDilutionRestController::getSample))//
      .add(new ParentFinder.ParentAdapter<LibraryDilution, Sample, SampleDto>("Sample") {

        @Override
        public SampleDto asDto(Sample model) {
          return Dtos.asDto(model);
        }

        @Override
        public Sample find(LibraryDilution model, Consumer<String> emitError) {
          return model.getLibrary().getSample();
        }
      })//
      .add(new ParentFinder.ParentAdapter<LibraryDilution, Library, LibraryDto>("Library") {

        @Override
        public LibraryDto asDto(Library model) {
          return Dtos.asDto(model);
        }

        @Override
        public Library find(LibraryDilution model, Consumer<String> emitError) {
          return model.getLibrary();
        }
      });

  @RequestMapping(value = "/parents/{category}", method = RequestMethod.POST)
  @ResponseBody
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  @RequestMapping(value = "/bulk-delete", method = RequestMethod.POST)
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
