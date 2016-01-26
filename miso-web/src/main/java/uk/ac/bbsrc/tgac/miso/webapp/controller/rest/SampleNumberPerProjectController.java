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

import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleNumberPerProjectDto;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("samplenumberperproject")
public class SampleNumberPerProjectController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleNumberPerProjectController.class);

  @Autowired
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @RequestMapping(value = "/samplenumberperproject/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleNumberPerProjectDto getSampleNumberPerProject(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
    SampleNumberPerProject sampleNumberPerProject = sampleNumberPerProjectService.get(id);
    if (sampleNumberPerProject == null) {
      throw new RestException("No sample number per project found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleNumberPerProjectDto dto = Dtos.asDto(sampleNumberPerProject);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SampleNumberPerProjectDto writeUrls(SampleNumberPerProjectDto sampleNumberPerProjectDto, UriComponentsBuilder uriBuilder) {

    URI baseUri = uriBuilder.build().toUri();
    sampleNumberPerProjectDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/samplenumberperproject/{id}")
        .buildAndExpand(sampleNumberPerProjectDto.getId()).toUriString());
    sampleNumberPerProjectDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleNumberPerProjectDto.getCreatedById()).toUriString());
    sampleNumberPerProjectDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleNumberPerProjectDto.getUpdatedById()).toUriString());
    return sampleNumberPerProjectDto;
  }

  @RequestMapping(value = "/samplenumberperprojects", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SampleNumberPerProjectDto> getSampleNumberPerProjects(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<SampleNumberPerProject> sampleNumberPerProjects = sampleNumberPerProjectService.getAll();
    if (sampleNumberPerProjects.isEmpty()) {
      throw new RestException("No sample numbers per project found", Status.NOT_FOUND);
    } else {
      Set<SampleNumberPerProjectDto> sampleNumberPerProjectDtos = Dtos.asSampleNumberPerProjectDtos(sampleNumberPerProjects);
      for (SampleNumberPerProjectDto sampleNumberPerProjectDto : sampleNumberPerProjectDtos) {
        sampleNumberPerProjectDto = writeUrls(sampleNumberPerProjectDto, uriBuilder);
      }
      return sampleNumberPerProjectDtos;
    }
  }

  @RequestMapping(value = "/samplenumberperproject", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleNumberPerProject(@RequestBody SampleNumberPerProjectDto sampleNumberPerProjectDto,
      UriComponentsBuilder b, HttpServletResponse response) throws IOException {
    SampleNumberPerProject sampleNumberPerProject = Dtos.to(sampleNumberPerProjectDto);
    Long id = sampleNumberPerProjectService.create(sampleNumberPerProject, sampleNumberPerProjectDto.getProjectId());
    UriComponents uriComponents = b.path("/samplenumberperproject/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/samplenumberperproject/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleNumberPerProject(@PathVariable("id") Long id,
      @RequestBody SampleNumberPerProjectDto sampleNumberPerProjectDto, HttpServletResponse response) throws IOException {
    SampleNumberPerProject sampleNumberPerProject = Dtos.to(sampleNumberPerProjectDto);
    sampleNumberPerProject.setSampleNumberPerProjectId(id);
    sampleNumberPerProjectService.update(sampleNumberPerProject);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/samplenumberperproject/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleNumberPerProject(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleNumberPerProjectService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}