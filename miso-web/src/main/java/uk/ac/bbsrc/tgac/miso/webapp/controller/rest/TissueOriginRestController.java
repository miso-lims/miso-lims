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
import java.util.List;
import java.util.Set;

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

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueOriginDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/tissueorigins")
public class TissueOriginRestController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(TissueOriginRestController.class);

  @Autowired
  private TissueOriginService tissueOriginService;

  @Autowired
  private ConstantsController constantsController;

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public TissueOriginDto getTissueOrigin(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueOrigin tissueOrigin = tissueOriginService.get(id);
    if (tissueOrigin == null) {
      throw new RestException("No tissue origin found with ID: " + id, Status.NOT_FOUND);
    } else {
      TissueOriginDto dto = Dtos.asDto(tissueOrigin);
      return dto;
    }
  }

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public Set<TissueOriginDto> getTissueOrigins(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    List<TissueOrigin> tissueOrigins = tissueOriginService.list();
    Set<TissueOriginDto> tissueOriginDtos = Dtos.asTissueOriginDtos(tissueOrigins);
    return tissueOriginDtos;
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody TissueOriginDto createTissueOrigin(@RequestBody TissueOriginDto tissueOriginDto) throws IOException {
    TissueOrigin tissueOrigin = Dtos.to(tissueOriginDto);
    Long id = tissueOriginService.create(tissueOrigin);
    constantsController.refreshConstants();
    return Dtos.asDto(tissueOriginService.get(id));
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  public @ResponseBody TissueOriginDto updateTissueOrigin(@PathVariable("id") Long id, @RequestBody TissueOriginDto tissueOriginDto)
      throws IOException {
    TissueOrigin tissueOrigin = Dtos.to(tissueOriginDto);
    tissueOrigin.setId(id);
    tissueOriginService.update(tissueOrigin);
    constantsController.refreshConstants();
    return Dtos.asDto(tissueOriginService.get(id));
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Tissue Origin", ids, tissueOriginService);
    constantsController.refreshConstants();
  }

}