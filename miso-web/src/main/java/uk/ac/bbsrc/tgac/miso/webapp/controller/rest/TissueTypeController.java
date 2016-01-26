/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

  @RequestMapping(value = "/tissuetype/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public TissueTypeDto getTissueType(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
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

  @RequestMapping(value = "/tissuetypes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<TissueTypeDto> getTissueTypes(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<TissueType> tissueTypes = tissueTypeService.getAll();
    if (tissueTypes.isEmpty()) {
      throw new RestException("No tissue types found", Status.NOT_FOUND);
    } else {
      Set<TissueTypeDto> tissueTypeDtos = Dtos.asTissueTypeDtos(tissueTypes);
      for (TissueTypeDto tissueTypeDto : tissueTypeDtos) {
        tissueTypeDto = writeUrls(tissueTypeDto, uriBuilder);
      }
      return tissueTypeDtos;
    }
  }

  @RequestMapping(value = "/tissuetype", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createTissueType(@RequestBody TissueTypeDto tissueTypeDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    TissueType tissueType = Dtos.to(tissueTypeDto);
    Long id = tissueTypeService.create(tissueType);
    UriComponents uriComponents = b.path("/tissuetype/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/tissuetype/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateTissueType(@PathVariable("id") Long id, @RequestBody TissueTypeDto tissueTypeDto,
      HttpServletResponse response) throws IOException {
    TissueType tissueType = Dtos.to(tissueTypeDto);
    tissueType.setTissueTypeId(id);
    tissueTypeService.update(tissueType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/tissuetype/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteTissueType(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    tissueTypeService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}