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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;

@Controller
@RequestMapping("/rest/sampleclasses")
public class SampleClassRestController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleClassRestController.class);

  @Autowired
  private SampleClassService sampleClassService;

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public SampleClassDto getSampleClass(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = sampleClassService.get(id);
    if (sampleClass == null) {
      throw new RestException("No sample class found with ID: " + id, Status.UNAUTHORIZED);
    } else {
      SampleClassDto dto = Dtos.asDto(sampleClass);
      return dto;
    }
  }

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public Set<SampleClassDto> getSampleClasses(UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    return sampleClassService.getAll().stream().map(sc -> {
      SampleClassDto dto = Dtos.asDto(sc);
      return dto;
    }).collect(Collectors.toSet());
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SampleClassDto createSampleClass(@RequestBody SampleClassDto sampleClassDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = Dtos.to(sampleClassDto);
    Long id = sampleClassService.create(sampleClass);
    return Dtos.asDto(sampleClassService.get(id));
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public SampleClassDto updateSampleClass(@PathVariable("id") Long id, @RequestBody SampleClassDto sampleClassDto,
      HttpServletResponse response) throws IOException {
    SampleClass sampleClass = Dtos.to(sampleClassDto);
    sampleClass.setId(id);
    sampleClassService.update(sampleClass);
    return Dtos.asDto(sampleClassService.get(id));
  }

}