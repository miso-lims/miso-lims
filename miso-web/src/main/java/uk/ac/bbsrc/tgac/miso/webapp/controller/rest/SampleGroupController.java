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

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleGroupDto;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("samplegroup")
public class SampleGroupController {

  protected static final Logger log = LoggerFactory.getLogger(SampleGroupController.class);

  @Autowired
  private SampleGroupService sampleGroupService;

  @RequestMapping(value = "/samplegroup/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SampleGroupDto> getSampleGroup(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
    if (response.containsHeader("x-authentication-failed")) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    SampleGroupId sampleGroup = sampleGroupService.get(id);
    if (sampleGroup == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      SampleGroupDto dto = Dtos.asDto(sampleGroup);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static SampleGroupDto writeUrls(SampleGroupDto sampleGroupDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleGroupDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/samplegroup/{id}").buildAndExpand(sampleGroupDto.getId()).toUriString());
    sampleGroupDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/user/{id}").buildAndExpand(sampleGroupDto.getCreatedById()).toUriString());
    sampleGroupDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/user/{id}").buildAndExpand(sampleGroupDto.getUpdatedById()).toUriString());
    return sampleGroupDto;
  }

  @RequestMapping(value = "/samplegroups", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleGroupDto>> getSampleGroups(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<SampleGroupId> sampleGroups = sampleGroupService.getAll();
    if (sampleGroups.isEmpty()) {
      if (response.containsHeader("x-authentication-failed")) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<SampleGroupDto> sampleGroupDtos = Dtos.asSampleGroupDtos(sampleGroups);
      for (SampleGroupDto sampleGroupDto : sampleGroupDtos) {
        sampleGroupDto = writeUrls(sampleGroupDto, uriBuilder);
      }
      return new ResponseEntity<>(sampleGroupDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/samplegroup", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleGroup(@RequestBody SampleGroupDto sampleGroupDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    if (response.containsHeader("x-authentication-failed")) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    SampleGroupId sampleGroup = Dtos.to(sampleGroupDto);
    Long id = sampleGroupService.create(sampleGroup);
    UriComponents uriComponents = b.path("/samplegroup/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/samplegroup/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleGroup(@PathVariable("id") Long id, @RequestBody SampleGroupDto sampleGroupDto,
      HttpServletResponse response) throws IOException {
    if (response.containsHeader("x-authentication-failed")) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    SampleGroupId sampleGroup = Dtos.to(sampleGroupDto);
    sampleGroup.setSampleGroupId(id);
    sampleGroupService.update(sampleGroup);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/samplegroup/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleGroup(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    if (response.containsHeader("x-authentication-failed")) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    sampleGroupService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}