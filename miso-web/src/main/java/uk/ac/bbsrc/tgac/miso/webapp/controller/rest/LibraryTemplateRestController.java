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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/librarytemplate")
public class LibraryTemplateRestController extends RestController {

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private LibraryTemplateService libraryTemplateService;

  private final JQueryDataTableBackend<LibraryTemplate, LibraryTemplateDto> jQueryBackend = new JQueryDataTableBackend<LibraryTemplate, LibraryTemplateDto>() {

    @Override
    protected LibraryTemplateDto asDto(LibraryTemplate model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<LibraryTemplate> getSource() throws IOException {
      return libraryTemplateService;
    }

  };

  protected static final Logger log = LoggerFactory.getLogger(LibraryTemplateRestController.class);

  @PostMapping(produces = "application/json")
  @ResponseBody
  public LibraryTemplateDto createLibraryTemplate(@RequestBody LibraryTemplateDto libraryTemplate, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    LibraryTemplate libraryTemplateObj = Dtos.to(libraryTemplate);
    Long id = libraryTemplateService.create(libraryTemplateObj);
    return Dtos.asDto(libraryTemplateService.get(id));
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LibraryTemplateDto updateLibraryTemplate(@PathVariable("id") Long id, @RequestBody LibraryTemplateDto libraryTemplateDto)
      throws IOException {
    if (libraryTemplateDto == null) {
      throw new RestException("Cannot convert null to LibraryTemplate", Status.BAD_REQUEST);
    }
    LibraryTemplate libraryTemplate = libraryTemplateService.get(id);
    if (libraryTemplate == null) {
      throw new RestException("No such LibraryTemplate.", Status.NOT_FOUND);
    }
    libraryTemplate = Dtos.to(libraryTemplateDto);
    libraryTemplateService.update(libraryTemplate);
    return Dtos.asDto(libraryTemplateService.get(id));
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryTemplateDto> getDTLibraryTemplatesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    List<LibraryTemplate> libraryTemplates = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null LibraryTemplate", Status.BAD_REQUEST);
      }
      LibraryTemplate libraryTemplate = libraryTemplateService.get(id);
      if (libraryTemplate == null) {
        throw new RestException("LibraryTemplate " + id + " not found", Status.BAD_REQUEST);
      }
      libraryTemplates.add(libraryTemplate);
    }
    libraryTemplateService.bulkDelete(libraryTemplates);
  }

}
