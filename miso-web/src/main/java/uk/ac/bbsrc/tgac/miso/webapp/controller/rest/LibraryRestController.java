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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;

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
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Library> getSource() throws IOException {
      return libraryService;
    }
  };

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private RequestManager requestManager;

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @RequestMapping(value = "{libraryId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String getLibraryById(@PathVariable Long libraryId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Library l = libraryService.get(libraryId);
    if (l == null) {
      throw new RestException("No library found with ID: " + libraryId, Status.NOT_FOUND);
    }
    return mapper.writeValueAsString(l);
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String listAllLibraries() throws IOException {
    Collection<Library> libraries = libraryService.list();
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(libraries);
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createLibrary(@RequestBody LibraryDto libraryDto, UriComponentsBuilder b) throws IOException {
    if (libraryDto == null) {
      log.error(
          "Received null libraryDto from front end; cannot convert to Library. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Library", Status.BAD_REQUEST);
    }
    Long id = null;
    try {
      Library library = Dtos.to(libraryDto);
      id = libraryService.create(library);
    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating library. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }
    UriComponents uriComponents = b.path("/library/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateLibrary(@PathVariable("id") Long id, @RequestBody LibraryDto libraryDto) throws IOException {
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
    if (libraryDto.getQcQubit() != null) {
      LibraryQC qc = new LibraryQCImpl();
      qc.setQcType(requestManager.getLibraryQcTypeByName("Qubit"));
      qc.setResults(libraryDto.getQcQubit());
      libraryService.addQc(library, qc);
    }
    if (libraryDto.getQcTapeStation() != null) {
      LibraryQC qc = new LibraryQCImpl();
      qc.setQcType(requestManager.getLibraryQcTypeByName("Tape Station"));
      qc.setResults(libraryDto.getQcTapeStation());
      libraryService.addQc(library, qc);
    }
    if (libraryDto.getQcQPcr() != null) {
      LibraryQC qc = new LibraryQCImpl();
      qc.setQcType(requestManager.getLibraryQcTypeByName("qPCR"));
      qc.setResults(libraryDto.getQcQPcr());
      libraryService.addQc(library, qc);
    }
    libraryService.update(library);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibraries(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibrariesForProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

}
