/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SamplePurposeDto;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("samplepurpose")
public class SamplePurposeController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SamplePurposeController.class);

  @Autowired
  private SamplePurposeService samplePurposeService;

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SamplePurposeDto getSamplePurpose(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SamplePurpose samplePurpose = samplePurposeService.get(id);
    if (samplePurpose == null) {
      throw new RestException("No sample purpose found with ID: " + id, Status.NOT_FOUND);
    } else {
      SamplePurposeDto dto = Dtos.asDto(samplePurpose);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SamplePurposeDto writeUrls(SamplePurposeDto samplePurposeDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    samplePurposeDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/samplepurpose/{id}")
        .buildAndExpand(samplePurposeDto.getId()).toUriString());
    samplePurposeDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(samplePurposeDto.getCreatedById()).toUriString());
    samplePurposeDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(samplePurposeDto.getUpdatedById()).toUriString());
    return samplePurposeDto;
  }

  @RequestMapping(value = "/samplepurposes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SamplePurposeDto> getSamplePurposes(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<SamplePurpose> samplePurposes = samplePurposeService.getAll();
    Set<SamplePurposeDto> samplePurposeDtos = Dtos.asSamplePurposeDtos(samplePurposes);
    for (SamplePurposeDto samplePurposeDto : samplePurposeDtos) {
      samplePurposeDto = writeUrls(samplePurposeDto, uriBuilder);
    }
    return samplePurposeDtos;
  }

  @RequestMapping(value = "/samplepurpose", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public SamplePurposeDto createSamplePurpose(@RequestBody SamplePurposeDto samplePurposeDto, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SamplePurpose samplePurpose = Dtos.to(samplePurposeDto);
    Long id = samplePurposeService.create(samplePurpose);
    return getSamplePurpose(id, uriBuilder, response);
  }

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public SamplePurposeDto updateSamplePurpose(@PathVariable("id") Long id, @RequestBody SamplePurposeDto samplePurposeDto,
      UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SamplePurpose samplePurpose = Dtos.to(samplePurposeDto);
    samplePurpose.setId(id);
    samplePurposeService.update(samplePurpose);
    return getSamplePurpose(id, uriBuilder, response);
  }

  @RequestMapping(value = "/samplepurpose/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteSamplePurpose(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    SamplePurpose samplePurpose = samplePurposeService.get(id);
    if (samplePurpose == null) {
      throw new RestException("Sample Purpose " + id + " not found", Status.NOT_FOUND);
    }
    samplePurposeService.delete(samplePurpose);
  }

}