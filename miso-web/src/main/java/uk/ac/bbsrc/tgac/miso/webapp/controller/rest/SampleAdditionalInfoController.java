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

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sampleadditionalinfo")
public class SampleAdditionalInfoController {

  protected static final Logger log = LoggerFactory.getLogger(SampleAdditionalInfoController.class);

  @Autowired
  private SampleAdditionalInfoService sampleAdditionalInfoService;

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SampleAdditionalInfoDto> getSampleAdditionalInfo(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    SampleAdditionalInfo sampleAdditionalInfo = sampleAdditionalInfoService.get(id);
    if (sampleAdditionalInfo == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      SampleAdditionalInfoDto dto = Dtos.asDto(sampleAdditionalInfo);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static SampleAdditionalInfoDto writeUrls(SampleAdditionalInfoDto sampleAdditionalInfoDto, UriComponentsBuilder uriBuilder) {
    sampleAdditionalInfoDto
        .setUrl(uriBuilder.replacePath("/rest/sampleadditionalinfo/{id}").buildAndExpand(sampleAdditionalInfoDto.getId()).toUriString());
    sampleAdditionalInfoDto
        .setCreatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(sampleAdditionalInfoDto.getCreatedById()).toUriString());
    sampleAdditionalInfoDto
        .setUpdatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(sampleAdditionalInfoDto.getUpdatedById()).toUriString());
    return sampleAdditionalInfoDto;
  }

  @RequestMapping(value = "/sampleadditionalinfos", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<SampleAdditionalInfoDto>> getSampleAdditionalInfos(UriComponentsBuilder uriBuilder) {
    Set<SampleAdditionalInfo> sampleAdditionalInfos = sampleAdditionalInfoService.getAll();
    if (sampleAdditionalInfos.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<SampleAdditionalInfoDto> sampleAdditionalInfoDtos = Dtos.asSampleAdditionalInfoDtos(sampleAdditionalInfos);
      for (SampleAdditionalInfoDto sampleAdditionalInfoDto : sampleAdditionalInfoDtos) {
        sampleAdditionalInfoDto = writeUrls(sampleAdditionalInfoDto, uriBuilder);
      }
      return new ResponseEntity<>(sampleAdditionalInfoDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/sampleadditionalinfo", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleAdditionalInfo(@RequestBody SampleAdditionalInfoDto sampleAdditionalInfoDto, UriComponentsBuilder b)
      throws IOException {
    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    Long id = sampleAdditionalInfoService.create(sampleAdditionalInfo);
    UriComponents uriComponents = b.path("/sampleadditionalinfo/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleAdditionalInfo(@PathVariable("id") Long id,
      @RequestBody SampleAdditionalInfoDto sampleAdditionalInfoDto) throws IOException {
    SampleAdditionalInfo sampleAdditionalInfo = Dtos.to(sampleAdditionalInfoDto);
    sampleAdditionalInfo.setSampleAdditionalInfoId(id);
    sampleAdditionalInfoService.update(sampleAdditionalInfo);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sampleadditionalinfo/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleAdditionalInfo(@PathVariable("id") Long id) throws IOException {
    sampleAdditionalInfoService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}