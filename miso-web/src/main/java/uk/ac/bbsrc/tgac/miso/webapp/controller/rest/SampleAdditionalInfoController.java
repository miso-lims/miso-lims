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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sampleadditionalinfo")
public class SampleAdditionalInfoController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleAdditionalInfoController.class);

  @Autowired
  private SampleAdditionalInfoService sampleAdditionalInfoService;

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleAdditionalInfoDto getSampleAdditionalInfo(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
    SampleAdditionalInfo sampleAdditionalInfo = sampleAdditionalInfoService.get(id);
    if (sampleAdditionalInfo == null) {
      throw new RestException("No sample additional info found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleAdditionalInfoDto dto = Dtos.asDto(sampleAdditionalInfo);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static SampleAdditionalInfoDto writeUrls(SampleAdditionalInfoDto sampleAdditionalInfoDto, UriComponentsBuilder uriBuilder) {

    URI baseUri = uriBuilder.build().toUri();
    sampleAdditionalInfoDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleadditionalinfo/{id}")
        .buildAndExpand(sampleAdditionalInfoDto.getId()).toUriString());
    sampleAdditionalInfoDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleAdditionalInfoDto.getCreatedById()).toUriString());
    sampleAdditionalInfoDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sampleAdditionalInfoDto.getUpdatedById()).toUriString());
    sampleAdditionalInfoDto.setSampleUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/sample/{id}")
        .buildAndExpand(sampleAdditionalInfoDto.getSampleId()).toUriString());
    if (sampleAdditionalInfoDto.getSampleClassId() != null) {
      sampleAdditionalInfoDto.setSampleClassUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/sampleclass/{id}")
          .buildAndExpand(sampleAdditionalInfoDto.getSampleClassId()).toUriString());
    }
    if (sampleAdditionalInfoDto.getTissueOriginId() != null) {
      sampleAdditionalInfoDto.setTissueOriginUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/tissueorigin/{id}")
          .buildAndExpand(sampleAdditionalInfoDto.getTissueOriginId()).toUriString());
    }
    if (sampleAdditionalInfoDto.getTissueTypeId() != null) {
      sampleAdditionalInfoDto.setTissueTypeUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/tissuetype/{id}")
          .buildAndExpand(sampleAdditionalInfoDto.getTissueTypeId()).toUriString());
    }
    if (sampleAdditionalInfoDto.getQcPassedDetailId() != null) {
      sampleAdditionalInfoDto.setQcPassedDetailUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/qcpasseddetail/{id}")
          .buildAndExpand(sampleAdditionalInfoDto.getQcPassedDetailId()).toUriString());
    }
    if (sampleAdditionalInfoDto.getSubprojectId() != null) {
      sampleAdditionalInfoDto.setSubprojectUrl(UriComponentsBuilder.fromUri(baseUri).replacePath("/rest/subproject/{id}")
          .buildAndExpand(sampleAdditionalInfoDto.getSubprojectId()).toUriString());
    }
    return sampleAdditionalInfoDto;
  }

  @RequestMapping(value = "/sampleadditionalinfos", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SampleAdditionalInfoDto> getSampleAdditionalInfos(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<SampleAdditionalInfo> sampleAdditionalInfos = sampleAdditionalInfoService.getAll();
    if (sampleAdditionalInfos.isEmpty()) {
      throw new RestException("No sample additional infos found", Status.NOT_FOUND);
    } else {
      Set<SampleAdditionalInfoDto> sampleAdditionalInfoDtos = Dtos.asSampleAdditionalInfoDtos(sampleAdditionalInfos);
      for (SampleAdditionalInfoDto sampleAdditionalInfoDto : sampleAdditionalInfoDtos) {
        sampleAdditionalInfoDto = writeUrls(sampleAdditionalInfoDto, uriBuilder);
      }
      return sampleAdditionalInfoDtos;
    }
  }

  @RequestMapping(value = "/sampleadditionalinfo", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleAdditionalInfo(@RequestBody SampleAdditionalInfoDto sampleAdditionalInfoDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    Long id = sampleAdditionalInfoService.create(sampleAdditionalInfo, sampleAdditionalInfoDto.getSampleId(),
        sampleAdditionalInfoDto.getTissueOriginId(), sampleAdditionalInfoDto.getTissueTypeId(),
        sampleAdditionalInfoDto.getQcPassedDetailId(), sampleAdditionalInfoDto.getSubprojectId(), sampleAdditionalInfoDto.getPrepKitId(),
        sampleAdditionalInfoDto.getSampleClassId());
    UriComponents uriComponents = b.path("/sampleadditionalinfo/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleAdditionalInfo(@PathVariable("id") Long id,
      @RequestBody SampleAdditionalInfoDto sampleAdditionalInfoDto, HttpServletResponse response) throws IOException {
    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    sampleAdditionalInfo.setSampleAdditionalInfoId(id);
    sampleAdditionalInfoService.update(sampleAdditionalInfo, sampleAdditionalInfoDto.getTissueOriginId(),
        sampleAdditionalInfoDto.getTissueTypeId(), sampleAdditionalInfoDto.getQcPassedDetailId(), sampleAdditionalInfoDto.getPrepKitId(),
        sampleAdditionalInfoDto.getSampleClassId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleAdditionalInfo(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleAdditionalInfoService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}