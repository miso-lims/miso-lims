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

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueOriginDto;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("tissueorigin")
public class TissueOriginController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(TissueOriginController.class);

  @Autowired
  private TissueOriginService tissueOriginService;

  @RequestMapping(value = "/tissueorigin/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public TissueOriginDto getTissueOrigin(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueOrigin tissueOrigin = tissueOriginService.get(id);
    if (tissueOrigin == null) {
      throw new RestException("No tissue origin found with ID: " + id, Status.NOT_FOUND);
    } else {
      TissueOriginDto dto = Dtos.asDto(tissueOrigin);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static TissueOriginDto writeUrls(TissueOriginDto tissueOriginDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    tissueOriginDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/tissueorigin/{id}").buildAndExpand(tissueOriginDto.getId()).toUriString());
    tissueOriginDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(tissueOriginDto.getCreatedById()).toUriString());
    tissueOriginDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(tissueOriginDto.getUpdatedById()).toUriString());
    return tissueOriginDto;
  }

  @RequestMapping(value = "/tissueorigins", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<TissueOriginDto> getTissueOrigins(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<TissueOrigin> tissueOrigins = tissueOriginService.getAll();
    if (tissueOrigins.isEmpty()) {
      throw new RestException("No tissue origins found", Status.NOT_FOUND);
    } else {
      Set<TissueOriginDto> tissueOriginDtos = Dtos.asTissueOriginDtos(tissueOrigins);
      for (TissueOriginDto tissueOriginDto : tissueOriginDtos) {
        tissueOriginDto = writeUrls(tissueOriginDto, uriBuilder);
      }
      return tissueOriginDtos;
    }
  }

  @RequestMapping(value = "/tissueorigin", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createTissueOrigin(@RequestBody TissueOriginDto tissueOriginDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    TissueOrigin tissueOrigin = Dtos.to(tissueOriginDto);
    Long id = tissueOriginService.create(tissueOrigin);
    UriComponents uriComponents = b.path("/tissueorigin/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/tissueorigin/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateTissueOrigin(@PathVariable("id") Long id, @RequestBody TissueOriginDto tissueOriginDto,
      HttpServletResponse response) throws IOException {
    TissueOrigin tissueOrigin = Dtos.to(tissueOriginDto);
    tissueOrigin.setTissueOriginId(id);
    tissueOriginService.update(tissueOrigin);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/tissueorigin/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteTissueOrigin(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    tissueOriginService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}