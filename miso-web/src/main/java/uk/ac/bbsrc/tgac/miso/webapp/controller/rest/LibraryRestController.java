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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibrarySpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

/**
 * A controller to handle all REST requests for Libraries
 * 
 * @author Rob Davey
 * @date 16-Aug-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/library")
@SessionAttributes("library")
public class LibraryRestController extends RestController {
  private static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  private final JQueryDataTableBackend<Library, LibraryDto> jQueryBackend = new JQueryDataTableBackend<Library, LibraryDto>() {
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
  private SampleRestController sampleController;

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @GetMapping(value = "/{libraryId}", produces = "application/json")
  @ResponseBody
  public LibraryDto getLibraryById(@PathVariable Long libraryId) throws IOException {
    Library l = libraryService.get(libraryId);
    if (l == null) {
      throw new RestException("No library found with ID: " + libraryId, Status.NOT_FOUND);
    }
    LibraryDto dto = Dtos.asDto(l, false);
    return dto;
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LibraryDto createLibrary(@RequestBody LibraryDto libraryDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    if (libraryDto == null) {
      log.error(
          "Received null libraryDto from front end; cannot convert to Library. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Library", Status.BAD_REQUEST);
    }
    Long id = null;
    try {
      Library library = Dtos.to(libraryDto);
      if (libraryDto.getSample() != null) {
        Sample sample = sampleController.buildHierarchy(libraryDto.getSample());
        if (LimsUtils.isDetailedSample(sample)) {
          ((DetailedSample) sample).setSynthetic(true);
        }
        library.setSample(sample);
      }
      id = libraryService.create(library);
    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating library. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }
    LibraryDto created = getLibraryById(id);
    UriComponents uriComponents = b.path("/library/{id}").buildAndExpand(id);
    response.setHeader("Location", uriComponents.toUri().toString());
    return created;
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LibraryDto updateLibrary(@PathVariable("id") Long id, @RequestBody LibraryDto libraryDto) throws IOException {
    if (libraryDto == null) {
      log.error(
          "Received null libraryDto from front end; cannot convert to Library. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Library", Status.BAD_REQUEST);
    }
    Library library = libraryService.get(id);
    if (library == null) {
      throw new RestException("No such library.", Status.NOT_FOUND);
    }
    library = Dtos.to(libraryDto);
    libraryService.update(library);
    return getLibraryById(id);
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibraries(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibrariesForProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @PostMapping(value = "/query", produces = { "application/json" })
  @ResponseBody
  public List<LibraryDto> getLibariesInBulk(@RequestBody List<String> names, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, libraryService, lib -> Dtos.asDto(lib, false),
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @PostMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(request, libraryService::get, LibrarySpreadSheets::valueOf, response);
  }

  private static Stream<Sample> getSample(Library library) {
    return Stream.of(library.getSample());
  }

  private final RelationFinder<Library> parentFinder = (new RelationFinder<Library>() {

    @Override
    protected Library fetch(long id) throws IOException {
      return libraryService.get(id);
    }
  })//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class, LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class, LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class, LibraryRestController::getSample))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class, LibraryRestController::getSample))//
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
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<Library> childFinder = (new RelationFinder<Library>() {

    @Override
    protected Library fetch(long id) throws IOException {
      return libraryService.get(id);
    }
  })//
      .add(new RelationFinder.RelationAdapter<Library, LibraryDilution, DilutionDto>("Dilution") {

        @Override
        public DilutionDto asDto(LibraryDilution model) {
          return Dtos.asDto(model, false, false);
        }

        @Override
        public Stream<LibraryDilution> find(Library model, Consumer<String> emitError) {
          Collection<LibraryDilution> children = model.getLibraryDilutions();
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
          return Dtos.asDto(model, false, false);
        }

        @Override
        public Stream<Pool> find(Library model, Consumer<String> emitError) {
          Set<Pool> children = model.getLibraryDilutions().stream().flatMap(dilution -> dilution.getPools().stream())
              .collect(Collectors.toSet());
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

}
