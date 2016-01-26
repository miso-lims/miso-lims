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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAnalyte;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAnalyteDto;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sampleanalyte")
public class SampleAnalyteController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleAnalyteController.class);

  @Autowired
  private SampleAnalyteService sampleAnalyteService;

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleAnalyteDto getSampleAnalyte(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    SampleAnalyte sampleAnalyte = sampleAnalyteService.get(id);
    if (sampleAnalyte == null) {
      throw new RestException("No sample analyte found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleAnalyteDto dto = Dtos.asDto(sampleAnalyte);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SampleAnalyteDto writeUrls(SampleAnalyteDto sampleAnalyteDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleAnalyteDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleanalyte/{id}")
        .buildAndExpand(sampleAnalyteDto.getId()).toUriString());
    sampleAnalyteDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleAnalyteDto.getCreatedById()).toUriString());
    sampleAnalyteDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleAnalyteDto.getUpdatedById()).toUriString());
    sampleAnalyteDto.setSampleUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/sample/{id}")
        .buildAndExpand(sampleAnalyteDto.getSampleId()).toUriString());
    sampleAnalyteDto.setSamplePurposeUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/samplepurpose/{id}")
        .buildAndExpand(sampleAnalyteDto.getSamplePurposeId()).toUriString());
    sampleAnalyteDto.setSampleGroupUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/samplegroup/{id}")
        .buildAndExpand(sampleAnalyteDto.getSampleGroupId()).toUriString());
    sampleAnalyteDto.setTissueMaterialUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/tissuematerial/{id}")
        .buildAndExpand(sampleAnalyteDto.getTissueMaterialId()).toUriString());
    return sampleAnalyteDto;
  }

  @RequestMapping(value = "/sampleanalytes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SampleAnalyteDto> getSampleAnalytes(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<SampleAnalyte> sampleAnalytes = sampleAnalyteService.getAll();
    if (sampleAnalytes.isEmpty()) {
      throw new RestException("No sample analytes found", Status.NOT_FOUND);
    } else {
      Set<SampleAnalyteDto> sampleAnalyteDtos = Dtos.asSampleAnalyteDtos(sampleAnalytes);
      for (SampleAnalyteDto sampleAnalyteDto : sampleAnalyteDtos) {
        sampleAnalyteDto = writeUrls(sampleAnalyteDto, uriBuilder);
      }
      return sampleAnalyteDtos;
    }
  }

  @RequestMapping(value = "/sampleanalyte", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleAnalyte(@RequestBody SampleAnalyteDto sampleAnalyteDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    Long id = sampleAnalyteService.create(sampleAnalyte, sampleAnalyteDto.getSampleId(), sampleAnalyteDto.getSamplePurposeId(),
        sampleAnalyteDto.getSampleGroupId(), sampleAnalyteDto.getTissueMaterialId());
    UriComponents uriComponents = b.path("/sampleanalyte/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleAnalyte(@PathVariable("id") Long id, @RequestBody SampleAnalyteDto sampleAnalyteDto,
      HttpServletResponse response) throws IOException {
    SampleAnalyte sampleAnalyte = Dtos.to(sampleAnalyteDto);
    sampleAnalyte.setSampleAnalyteId(id);
    sampleAnalyteService.update(sampleAnalyte, sampleAnalyteDto.getSamplePurposeId(), sampleAnalyteDto.getSampleGroupId(),
        sampleAnalyteDto.getTissueMaterialId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sampleanalyte/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleAnalyte(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleAnalyteService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}