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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.hasStockParent;

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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sampleclass")
public class SampleClassController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleClassController.class);

  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private SampleValidRelationshipService sampleValidRelationshipService;

  @RequestMapping(value = "/sampleclass/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleClassDto getSampleClass(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = sampleClassService.get(id);
    if (sampleClass == null) {
      throw new RestException("No sample class found with ID: " + id, Status.UNAUTHORIZED);
    } else {
      SampleClassDto dto = Dtos.asDto(sampleClass);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SampleClassDto writeUrls(SampleClassDto sampleClassDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleClassDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleclass/{id}").buildAndExpand(sampleClassDto.getId()).toUriString());
    sampleClassDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(sampleClassDto.getCreatedById()).toUriString());
    sampleClassDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(sampleClassDto.getUpdatedById()).toUriString());
    return sampleClassDto;
  }

  @RequestMapping(value = "/sampleclasses", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SampleClassDto> getSampleClasses(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Iterable<SampleValidRelationship> relationships = sampleValidRelationshipService.getAll();
    Set<SampleClass> sampleClasss = sampleClassService.getAll();
    Set<SampleClassDto> sampleClassDtos = Dtos.asSampleClassDtos(sampleClasss);
    for (SampleClassDto sampleClassDto : sampleClassDtos) {
      sampleClassDto = writeUrls(sampleClassDto, uriBuilder);
      if (sampleClassDto.getSampleCategory().equals(SampleTissue.CATEGORY_NAME)
          || sampleClassDto.getSampleCategory().equals(SampleStock.CATEGORY_NAME)
          || sampleClassDto.getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)
              && hasStockParent(sampleClassDto.getId(), relationships)) {
        sampleClassDto.setCanCreateNew(true);
      }
    }
    return sampleClassDtos;
  }

  @RequestMapping(value = "/sampleclass", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleClass(@RequestBody SampleClassDto sampleClassDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = Dtos.to(sampleClassDto);
    Long id = sampleClassService.create(sampleClass);
    UriComponents uriComponents = b.path("/sampleclass/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sampleclass/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleClass(@PathVariable("id") Long id, @RequestBody SampleClassDto sampleClassDto,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = Dtos.to(sampleClassDto);
    sampleClass.setId(id);
    sampleClassService.update(sampleClass);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sampleclass/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleClass(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    // first delete all SampleValidRelationships which reference this Class (as parent or child)
    for (SampleValidRelationship relationship : sampleValidRelationshipService.getAll()) {
      if (relationship.getChild().getId().equals(id) || relationship.getParent().getId().equals(id)) {
        sampleValidRelationshipService.delete(relationship.getId());
      }
    }
    // then delete the Class itself
    sampleClassService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}