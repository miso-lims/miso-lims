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

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SamplePurposeDto;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("samplepurpose")
public class SamplePurposeController {

  protected static final Logger log = LoggerFactory.getLogger(SamplePurposeController.class);

  @Autowired
  private SamplePurposeService samplePurposeService;

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SamplePurposeDto> getSamplePurpose(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    SamplePurpose samplePurpose = samplePurposeService.get(id);
    if (samplePurpose == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      SamplePurposeDto dto = Dtos.asDto(samplePurpose);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static SamplePurposeDto writeUrls(SamplePurposeDto samplePurposeDto, UriComponentsBuilder uriBuilder) {
    samplePurposeDto.setUrl(uriBuilder.replacePath("/rest/samplepurpose/{id}").buildAndExpand(samplePurposeDto.getId()).toUriString());
    samplePurposeDto
        .setCreatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(samplePurposeDto.getCreatedById()).toUriString());
    samplePurposeDto
        .setUpdatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(samplePurposeDto.getUpdatedById()).toUriString());
    return samplePurposeDto;
  }

  @RequestMapping(value = "/samplepurposes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SamplePurposeDto>> getSamplePurposes(UriComponentsBuilder uriBuilder) {
    Set<SamplePurpose> samplePurposes = samplePurposeService.getAll();
    if (samplePurposes.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<SamplePurposeDto> samplePurposeDtos = Dtos.asSamplePurposeDtos(samplePurposes);
      for (SamplePurposeDto samplePurposeDto : samplePurposeDtos) {
        samplePurposeDto = writeUrls(samplePurposeDto, uriBuilder);
      }
      return new ResponseEntity<>(samplePurposeDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/samplepurpose", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSamplePurpose(@RequestBody SamplePurposeDto samplePurposeDto, UriComponentsBuilder b) throws IOException {
    SamplePurpose samplePurpose = Dtos.to(samplePurposeDto);
    Long id = samplePurposeService.create(samplePurpose);
    UriComponents uriComponents = b.path("/samplepurpose/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSamplePurpose(@PathVariable("id") Long id, @RequestBody SamplePurposeDto samplePurposeDto)
      throws IOException {
    SamplePurpose samplePurpose = Dtos.to(samplePurposeDto);
    samplePurpose.setSamplePurposeId(id);
    samplePurposeService.update(samplePurpose);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSamplePurpose(@PathVariable("id") Long id) throws IOException {
    samplePurposeService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}