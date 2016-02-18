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

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Controller
@RequestMapping("/rest/fred")
@SessionAttributes("sample")
public class SampleController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleController.class);

  @Autowired
  private SampleService sampleService;

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleDto getSample(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response) 
      throws IOException {
    Sample sample = sampleService.get(id);
    if (sample == null) {
      throw new RestException("No sample found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleDto dto = Dtos.asDto(sample);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SampleDto writeUrls(SampleDto sampleDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/fred/sample/{id}").buildAndExpand(sampleDto.getId()).toUriString());
    // sampleDto.setCreatedByUrl(uriBuilder.path("/rest/user/{id}").buildAndExpand(sampleDto.getCreatedById()).toUriString());
    // sampleDto.setUpdatedByUrl(uriBuilder.path("/rest/user/{id}").buildAndExpand(sampleDto.getUpdatedById()).toUriString());
    if (sampleDto.getParentId() != null) {
      sampleDto.setParentUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/fred/sample/{id}").buildAndExpand(sampleDto.getParentId()).toUriString());
    }
    if (sampleDto.getRootSampleClassId() != null) {
      sampleDto.setRootSampleClassUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleclass/{id}")
          .buildAndExpand(sampleDto.getRootSampleClassId()).toUriString());
    }
    return sampleDto;
  }

  @RequestMapping(value = "/samples", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleDto>> getSamples(UriComponentsBuilder uriBuilder) throws IOException {
    Set<Sample> samples = sampleService.getAll();
    if (samples.isEmpty()) {
      throw new RestException("No samples found", Status.NOT_FOUND);
    } else {
      Set<SampleDto> sampleDtos = Dtos.asSampleDtos(samples);
      for (SampleDto sampleDto : sampleDtos) {
        sampleDto = writeUrls(sampleDto, uriBuilder);
      }
      return new ResponseEntity<>(sampleDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/sample", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSample(@RequestBody SampleDto sampleDto, UriComponentsBuilder b) throws IOException {
    Sample sample = Dtos.to(sampleDto);
    Long id = sampleService.create(sample, sampleDto.getProjectId(), sampleDto.getParentId(), sampleDto.getRootSampleClassId());
    UriComponents uriComponents = b.path("/sample/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSample(@PathVariable("id") Long id, @RequestBody SampleDto sampleDto) throws IOException {
    Sample sample = Dtos.to(sampleDto);
    sample.setSampleId(id);
    sampleService.update(sample);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSample(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}