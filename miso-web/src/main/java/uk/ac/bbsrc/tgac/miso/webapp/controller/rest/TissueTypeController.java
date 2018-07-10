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
import java.net.URI;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueTypeDto;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("tissuetype")
public class TissueTypeController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(TissueTypeController.class);

  @Autowired
  private TissueTypeService tissueTypeService;

  @GetMapping(value = "/tissuetype/{id}", produces = { "application/json" })
  @ResponseBody
  public TissueTypeDto getTissueType(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueType tissueType = tissueTypeService.get(id);
    if (tissueType == null) {
      throw new RestException("No tissue type found with ID: " + id, Status.NOT_FOUND);
    } else {
      TissueTypeDto dto = Dtos.asDto(tissueType);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static TissueTypeDto writeUrls(TissueTypeDto tissueTypeDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    tissueTypeDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuetype/{id}").buildAndExpand(tissueTypeDto.getId()).toUriString());
    tissueTypeDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(tissueTypeDto.getCreatedById()).toUriString());
    tissueTypeDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(tissueTypeDto.getUpdatedById()).toUriString());
    return tissueTypeDto;
  }

  @GetMapping(value = "/tissuetypes", produces = { "application/json" })
  @ResponseBody
  public Set<TissueTypeDto> getTissueTypes(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<TissueType> tissueTypes = tissueTypeService.getAll();
    Set<TissueTypeDto> tissueTypeDtos = Dtos.asTissueTypeDtos(tissueTypes);
    for (TissueTypeDto tissueTypeDto : tissueTypeDtos) {
      tissueTypeDto = writeUrls(tissueTypeDto, uriBuilder);
    }
    return tissueTypeDtos;
  }

  @PostMapping(value = "/tissuetype", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public TissueTypeDto createTissueType(@RequestBody TissueTypeDto tissueTypeDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    TissueType tissueType = Dtos.to(tissueTypeDto);
    Long id = tissueTypeService.create(tissueType);
    return Dtos.asDto(tissueTypeService.get(id));
  }

  @PutMapping(value = "/tissuetype/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public TissueTypeDto updateTissueType(@PathVariable("id") Long id, @RequestBody TissueTypeDto tissueTypeDto,
      HttpServletResponse response) throws IOException {
    TissueType tissueType = Dtos.to(tissueTypeDto);
    tissueType.setId(id);
    tissueTypeService.update(tissueType);
    return Dtos.asDto(tissueTypeService.get(id));
  }

}