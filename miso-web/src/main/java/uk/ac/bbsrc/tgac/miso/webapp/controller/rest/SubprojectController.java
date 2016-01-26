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

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SubprojectDto;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("subproject")
public class SubprojectController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SubprojectController.class);

  @Autowired
  private SubprojectService subprojectService;

  @RequestMapping(value = "/subproject/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SubprojectDto getSubproject(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
    Subproject subproject = subprojectService.get(id);
    if (subproject == null) {
      throw new RestException("No subproject found with ID: " + id, Status.NOT_FOUND);
    } else {
      SubprojectDto dto = Dtos.asDto(subproject);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SubprojectDto writeUrls(SubprojectDto subprojectDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    subprojectDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/subproject/{id}").buildAndExpand(subprojectDto.getId()).toUriString());
    subprojectDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(subprojectDto.getCreatedById()).toUriString());
    subprojectDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(subprojectDto.getUpdatedById()).toUriString());
    return subprojectDto;
  }

  @RequestMapping(value = "/subprojects", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SubprojectDto> getSubprojects(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<Subproject> subprojects = subprojectService.getAll();
    if (subprojects.isEmpty()) {
      throw new RestException("No subprojects found", Status.NOT_FOUND);
    } else {
      Set<SubprojectDto> subprojectDtos = Dtos.asSubprojectDtos(subprojects);
      for (SubprojectDto subprojectDto : subprojectDtos) {
        subprojectDto = writeUrls(subprojectDto, uriBuilder);
      }
      return subprojectDtos;
    }
  }

  @RequestMapping(value = "/subproject", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSubproject(@RequestBody SubprojectDto subprojectDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    Subproject subproject = Dtos.to(subprojectDto);
    Long id = subprojectService.create(subproject, subprojectDto.getParentProjectId());
    UriComponents uriComponents = b.path("/subproject/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/subproject/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSubproject(@PathVariable("id") Long id, @RequestBody SubprojectDto subprojectDto,
      HttpServletResponse response) throws IOException {
    Subproject subproject = Dtos.to(subprojectDto);
    subproject.setSubprojectId(id);
    subprojectService.update(subproject);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/subproject/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSubproject(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    subprojectService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}