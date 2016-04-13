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

import uk.ac.bbsrc.tgac.miso.core.data.LibraryAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAdditionalInfoDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;

@Controller
@RequestMapping("/rest/library")
@SessionAttributes("libraryadditionalinfo")
public class LibraryAdditionalInfoController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(LibraryAdditionalInfoController.class);

  @Autowired
  private LibraryAdditionalInfoService libraryAdditionalInfoService;

  @RequestMapping(value = "/additionalinfo/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public LibraryAdditionalInfoDto getLibraryAdditionalInfo(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    LibraryAdditionalInfo libraryAdditionalInfo = libraryAdditionalInfoService.get(id);
    if (libraryAdditionalInfo == null) {
      throw new RestException("No library additional info found with ID: " + id, Status.NOT_FOUND);
    } else {
      LibraryAdditionalInfoDto dto = Dtos.asDto(libraryAdditionalInfo);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static LibraryAdditionalInfoDto writeUrls(LibraryAdditionalInfoDto libraryAdditionalInfoDto, UriComponentsBuilder uriBuilder) {

    URI baseUri = uriBuilder.build().toUri();
    libraryAdditionalInfoDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/library/additionalinfo/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getId()).toUriString());
    libraryAdditionalInfoDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getCreatedById()).toUriString());
    libraryAdditionalInfoDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getUpdatedById()).toUriString());
    libraryAdditionalInfoDto.setLibraryUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/library/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getLibraryId()).toUriString());
    libraryAdditionalInfoDto.getTissueOrigin().setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissueorigin/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getTissueOrigin().getId()).toUriString());
    libraryAdditionalInfoDto.getTissueType().setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuetype/{id}")
        .buildAndExpand(libraryAdditionalInfoDto.getTissueType().getId()).toUriString());
    if (libraryAdditionalInfoDto.getSampleGroup() != null) {
      libraryAdditionalInfoDto.getSampleGroup().setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/samplegroup/{id}")
          .buildAndExpand(libraryAdditionalInfoDto.getSampleGroup().getId()).toUriString());
    }
    if (libraryAdditionalInfoDto.getPrepKit() != null && libraryAdditionalInfoDto.getPrepKit().getId() != null) {
      libraryAdditionalInfoDto.getPrepKit().setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/kitdescriptor/{id}")
          .buildAndExpand(libraryAdditionalInfoDto.getPrepKit().getId()).toUriString());
    }
    return libraryAdditionalInfoDto;
  }

  @RequestMapping(value = "/additionalinfos", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<LibraryAdditionalInfoDto> getLibraryAdditionalInfos(UriComponentsBuilder uriBuilder, HttpServletResponse response) 
      throws IOException {
    Set<LibraryAdditionalInfo> libraryAdditionalInfos = libraryAdditionalInfoService.getAll();
    Set<LibraryAdditionalInfoDto> libraryAdditionalInfoDtos = Dtos.asLibraryAdditionalInfoDtos(libraryAdditionalInfos);
    for (LibraryAdditionalInfoDto libraryAdditionalInfoDto : libraryAdditionalInfoDtos) {
      libraryAdditionalInfoDto = writeUrls(libraryAdditionalInfoDto, uriBuilder);
    }
    return libraryAdditionalInfoDtos;
  }

  @RequestMapping(value = "/additionalinfo", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createLibraryAdditionalInfo(@RequestBody LibraryAdditionalInfoDto libraryAdditionalInfoDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    LibraryAdditionalInfo libraryAdditionalInfo = Dtos.to(libraryAdditionalInfoDto);
    Long id = libraryAdditionalInfoService.create(libraryAdditionalInfo, libraryAdditionalInfoDto.getLibraryId());
    UriComponents uriComponents = b.path("/library/additionalinfo/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/additionalinfo/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateLibraryAdditionalInfo(@PathVariable("id") Long id,
      @RequestBody LibraryAdditionalInfoDto libraryAdditionalInfoDto, HttpServletResponse response) throws IOException {
    LibraryAdditionalInfo libraryAdditionalInfo = Dtos.to(libraryAdditionalInfoDto);
    libraryAdditionalInfo.setLibraryId(id);
    libraryAdditionalInfoService.update(libraryAdditionalInfo);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/additionalinfo/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteLibraryAdditionalInfo(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    libraryAdditionalInfoService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}