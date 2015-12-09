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

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleValidRelationshipDto;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("samplevalidrelationship")
public class SampleValidRelationshipController {

  protected static final Logger log = LoggerFactory.getLogger(SampleValidRelationshipController.class);

  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;

  @RequestMapping(value = "/samplevalidrelationship/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SampleValidRelationshipDto> getSampleValidRelationship(@PathVariable("id") Long id,
      UriComponentsBuilder uriBuilder) {
    SampleValidRelationship sampleValidRelationship = sampleValidRelationshipService.get(id);
    if (sampleValidRelationship == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      SampleValidRelationshipDto dto = Dtos.asDto(sampleValidRelationship);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static SampleValidRelationshipDto writeUrls(SampleValidRelationshipDto sampleValidRelationshipDto,
      UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleValidRelationshipDto.setUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/samplevalidrelationship/{id}")
        .buildAndExpand(sampleValidRelationshipDto.getId()).toUriString());
    sampleValidRelationshipDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/user/{id}")
        .buildAndExpand(sampleValidRelationshipDto.getCreatedById()).toUriString());
    sampleValidRelationshipDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/user/{id}")
        .buildAndExpand(sampleValidRelationshipDto.getUpdatedById()).toUriString());
    return sampleValidRelationshipDto;
  }

  @RequestMapping(value = "/samplevalidrelationships", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleValidRelationshipDto>> getSampleValidRelationships(UriComponentsBuilder uriBuilder) {
    Set<SampleValidRelationship> sampleValidRelationships = sampleValidRelationshipService.getAll();
    if (sampleValidRelationships.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<SampleValidRelationshipDto> sampleValidRelationshipDtos = Dtos.asSampleValidRelationshipDtos(sampleValidRelationships);
      for (SampleValidRelationshipDto sampleValidRelationshipDto : sampleValidRelationshipDtos) {
        sampleValidRelationshipDto = writeUrls(sampleValidRelationshipDto, uriBuilder);
      }
      return new ResponseEntity<>(sampleValidRelationshipDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/samplevalidrelationship", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleValidRelationship(@RequestBody SampleValidRelationshipDto sampleValidRelationshipDto,
      UriComponentsBuilder b) throws IOException {
    SampleValidRelationship sampleValidRelationship = Dtos.to(sampleValidRelationshipDto);
    Long id = sampleValidRelationshipService.create(sampleValidRelationship);
    UriComponents uriComponents = b.path("/samplevalidrelationship/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/samplevalidrelationship/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleValidRelationship(@PathVariable("id") Long id,
      @RequestBody SampleValidRelationshipDto sampleValidRelationshipDto) throws IOException {
    SampleValidRelationship sampleValidRelationship = Dtos.to(sampleValidRelationshipDto);
    sampleValidRelationship.setSampleValidRelationshipId(id);
    sampleValidRelationshipService.update(sampleValidRelationship);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/samplevalidrelationship/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleValidRelationship(@PathVariable("id") Long id) throws IOException {
    sampleValidRelationshipService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}