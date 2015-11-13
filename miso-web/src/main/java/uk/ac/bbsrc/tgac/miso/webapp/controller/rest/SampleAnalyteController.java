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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAnalyteDto;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sampleanalyte")
public class SampleAnalyteController {

  protected static final Logger log = LoggerFactory.getLogger(SampleAnalyteController.class);

  @Autowired
  private SampleAnalyteService sampleAnalyteService;

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SampleAnalyteDto> getSampleAnalyte(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    SampleAnalyte sampleAnalyte = sampleAnalyteService.get(id);
    if (sampleAnalyte == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      SampleAnalyteDto dto = Dtos.asDto(sampleAnalyte);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static SampleAnalyteDto writeUrls(SampleAnalyteDto sampleAnalyteDto, UriComponentsBuilder uriBuilder) {
    sampleAnalyteDto.setUrl(uriBuilder.replacePath("/rest/sampleanalyte/{id}").buildAndExpand(sampleAnalyteDto.getId()).toUriString());
    sampleAnalyteDto
        .setCreatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(sampleAnalyteDto.getCreatedById()).toUriString());
    sampleAnalyteDto
        .setUpdatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(sampleAnalyteDto.getUpdatedById()).toUriString());
    return sampleAnalyteDto;
  }

  @RequestMapping(value = "/sampleanalytes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleAnalyteDto>> getSampleAnalytes(UriComponentsBuilder uriBuilder) {
    Set<SampleAnalyte> sampleAnalytes = sampleAnalyteService.getAll();
    if (sampleAnalytes.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<SampleAnalyteDto> sampleAnalyteDtos = Dtos.asSampleAnalyteDtos(sampleAnalytes);
      for (SampleAnalyteDto sampleAnalyteDto : sampleAnalyteDtos) {
        sampleAnalyteDto = writeUrls(sampleAnalyteDto, uriBuilder);
      }
      return new ResponseEntity<>(sampleAnalyteDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/sampleanalyte", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleAnalyte(@RequestBody SampleAnalyteDto sampleAnalyteDto, UriComponentsBuilder b) throws IOException {
    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    Long id = sampleAnalyteService.create(sampleAnalyte);
    UriComponents uriComponents = b.path("/sampleanalyte/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleAnalyte(@PathVariable("id") Long id, @RequestBody SampleAnalyteDto sampleAnalyteDto)
      throws IOException {
    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    sampleAnalyte.setSampleAnalyteId(id);
    sampleAnalyteService.update(sampleAnalyte);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleAnalyte(@PathVariable("id") Long id) throws IOException {
    sampleAnalyteService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}