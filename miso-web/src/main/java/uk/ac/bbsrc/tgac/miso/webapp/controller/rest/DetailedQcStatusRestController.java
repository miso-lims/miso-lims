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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.dto.DetailedQcStatusDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/detailedqcstatuses")
public class DetailedQcStatusRestController extends RestController {

  @Autowired
  private DetailedQcStatusService detailedQcStatusService;

  @Autowired
  private MenuController menuController;

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public DetailedQcStatusDto createDetailedQcStatus(@RequestBody DetailedQcStatusDto detailedQcStatusDto) throws IOException {
    return RestUtils.createObject("Detailed QC Status", detailedQcStatusDto, Dtos::to, detailedQcStatusService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public DetailedQcStatusDto updateDetailedQcStatus(@PathVariable long id, @RequestBody DetailedQcStatusDto detailedQcStatusDto)
      throws IOException {
    return RestUtils.updateObject("Detailed QC Status", id, detailedQcStatusDto, Dtos::to, detailedQcStatusService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Detailed QC Status", ids, detailedQcStatusService);
  }
  
}