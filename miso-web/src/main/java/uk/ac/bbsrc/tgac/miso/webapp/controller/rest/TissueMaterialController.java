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

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueMaterialDto;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("tissuematerial")
public class TissueMaterialController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(TissueMaterialController.class);

  @Autowired
  private TissueMaterialService tissueMaterialService;

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public TissueMaterialDto getTissueMaterial(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueMaterial tissueMaterial = tissueMaterialService.get(id);
    if (tissueMaterial == null) {
      throw new RestException("No tissue material found with ID: " + id, Status.NOT_FOUND);
    } else {
      TissueMaterialDto dto = Dtos.asDto(tissueMaterial);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static TissueMaterialDto writeUrls(TissueMaterialDto tissueMaterialDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    tissueMaterialDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuematerial/{id}")
        .buildAndExpand(tissueMaterialDto.getId()).toUriString());
    tissueMaterialDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(tissueMaterialDto.getCreatedById()).toUriString());
    tissueMaterialDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(tissueMaterialDto.getUpdatedById()).toUriString());
    return tissueMaterialDto;
  }

  @RequestMapping(value = "/tissuematerials", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<TissueMaterialDto> getTissueMaterials(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<TissueMaterial> tissueMaterials = tissueMaterialService.getAll();
    Set<TissueMaterialDto> tissueMaterialDtos = Dtos.asTissueMaterialDtos(tissueMaterials);
    for (TissueMaterialDto tissueMaterialDto : tissueMaterialDtos) {
      tissueMaterialDto = writeUrls(tissueMaterialDto, uriBuilder);
    }
    return tissueMaterialDtos;
  }

  @RequestMapping(value = "/tissuematerial", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public TissueMaterialDto createTissueMaterial(@RequestBody TissueMaterialDto tissueMaterialDto, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueMaterial tissueMaterial = Dtos.to(tissueMaterialDto);
    Long id = tissueMaterialService.create(tissueMaterial);
    return getTissueMaterial(id, uriBuilder, response);
  }

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public TissueMaterialDto updateTissueMaterial(@PathVariable("id") Long id, @RequestBody TissueMaterialDto tissueMaterialDto,
      UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    TissueMaterial tissueMaterial = Dtos.to(tissueMaterialDto);
    tissueMaterial.setId(id);
    tissueMaterialService.update(tissueMaterial);
    return getTissueMaterial(id, uriBuilder, response);
  }

  @RequestMapping(value = "/tissuematerial/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteTissueMaterial(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    TissueMaterial tissueMaterial = tissueMaterialService.get(id);
    if (tissueMaterial == null) {
      throw new RestException("Tissue Material " + id + " not found", Status.NOT_FOUND);
    }
    tissueMaterialService.delete(tissueMaterial);
  }

}