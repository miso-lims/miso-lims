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

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueMaterialDto;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("tissuematerial")
public class TissueMaterialController {

  protected static final Logger log = LoggerFactory.getLogger(TissueMaterialController.class);

  @Autowired
  private TissueMaterialService tissueMaterialService;

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<TissueMaterialDto> getTissueMaterial(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    TissueMaterial tissueMaterial = tissueMaterialService.get(id);
    if (tissueMaterial == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      TissueMaterialDto dto = Dtos.asDto(tissueMaterial);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static TissueMaterialDto writeUrls(TissueMaterialDto tissueMaterialDto, UriComponentsBuilder uriBuilder) {
    tissueMaterialDto.setUrl(uriBuilder.replacePath("/rest/tissuematerial/{id}").buildAndExpand(tissueMaterialDto.getId()).toUriString());
    tissueMaterialDto
        .setCreatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(tissueMaterialDto.getCreatedById()).toUriString());
    tissueMaterialDto
        .setUpdatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(tissueMaterialDto.getUpdatedById()).toUriString());
    return tissueMaterialDto;
  }

  @RequestMapping(value = "/tissuematerials", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<TissueMaterialDto>> getTissueMaterials(UriComponentsBuilder uriBuilder) {
    Set<TissueMaterial> tissueMaterials = tissueMaterialService.getAll();
    if (tissueMaterials.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<TissueMaterialDto> tissueMaterialDtos = Dtos.asTissueMaterialDtos(tissueMaterials);
      for (TissueMaterialDto tissueMaterialDto : tissueMaterialDtos) {
        tissueMaterialDto = writeUrls(tissueMaterialDto, uriBuilder);
      }
      return new ResponseEntity<>(tissueMaterialDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/tissuematerial", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createTissueMaterial(@RequestBody TissueMaterialDto tissueMaterialDto, UriComponentsBuilder b)
      throws IOException {
    TissueMaterial tissueMaterial = Dtos.to(tissueMaterialDto);
    Long id = tissueMaterialService.create(tissueMaterial);
    UriComponents uriComponents = b.path("/tissuematerial/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateTissueMaterial(@PathVariable("id") Long id, @RequestBody TissueMaterialDto tissueMaterialDto)
      throws IOException {
    TissueMaterial tissueMaterial = Dtos.to(tissueMaterialDto);
    tissueMaterial.setTissueMaterialId(id);
    tissueMaterialService.update(tissueMaterial);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteTissueMaterial(@PathVariable("id") Long id) throws IOException {
    tissueMaterialService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}