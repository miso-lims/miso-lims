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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SubprojectDto;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/subprojects")
public class SubprojectController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SubprojectController.class);

  @Autowired
  private SubprojectService subprojectService;

  @Autowired
  private SampleGroupService sampleGroupService;

  @Autowired
  private MenuController menuController;

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public SubprojectDto getSubproject(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    Subproject subproject = subprojectService.get(id);
    if (subproject == null) {
      throw new RestException("No subproject found with ID: " + id, Status.NOT_FOUND);
    } else {
      SubprojectDto dto = Dtos.asDto(subproject);
      return dto;
    }
  }

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public Set<SubprojectDto> getSubprojects(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    List<Subproject> subprojects = subprojectService.list();
    Set<SubprojectDto> subprojectDtos = Dtos.asSubprojectDtos(subprojects);
    return subprojectDtos;
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public SubprojectDto createSubproject(@RequestBody SubprojectDto subprojectDto, UriComponentsBuilder uriBuilder,
      HttpServletResponse response)
      throws IOException {
    Subproject subproject = Dtos.to(subprojectDto);
    Long id = subprojectService.create(subproject, subprojectDto.getParentProjectId());
    menuController.refreshConstants();
    return getSubproject(id, uriBuilder, response);
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public SubprojectDto updateSubproject(@PathVariable("id") Long id, @RequestBody SubprojectDto subprojectDto,
      UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    Subproject subproject = Dtos.to(subprojectDto);
    subproject.setId(id);
    subprojectService.update(subproject);
    menuController.refreshConstants();
    return getSubproject(id, uriBuilder, response);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  public void deleteSubproject(@PathVariable(name = "id", required = true) long id) throws IOException {
    Subproject subproject = subprojectService.get(id);
    if (subproject == null) {
      throw new RestException("Subproject " + id + " not found", Status.NOT_FOUND);
    }
    subprojectService.delete(subproject);
    menuController.refreshConstants();
  }

  @GetMapping(value = "/{id}/groups", produces = { "application/json" })
  @ResponseBody
  public Collection<Integer> getSubprojectSampleGroups(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    Set<Integer> groups = new HashSet<>();
    for (SampleGroupId sgi : sampleGroupService.getAllForSubproject(id)) {
      groups.add(sgi.getGroupId());
    }
    return groups;
  }

}