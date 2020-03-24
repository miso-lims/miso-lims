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

import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/qctypes")
public class QcTypeRestController extends RestController {

  @Autowired
  private ConstantsController constantsController;

  @Autowired
  private QcTypeService qcTypeService;

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public QcTypeDto createQcType(@RequestBody QcTypeDto qcTypeDto) throws IOException {
    return RestUtils.createObject("QC Type", qcTypeDto, Dtos::to, qcTypeService, qcType -> {
      QcTypeDto dto = Dtos.asDto(qcType);
      constantsController.refreshConstants();
      return dto;
    });
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public QcTypeDto updateSubproject(@PathVariable("id") long id, @RequestBody QcTypeDto qcTypeDto) throws IOException {
    return RestUtils.updateObject("QC Type", id, qcTypeDto, Dtos::to, qcTypeService, qcType -> {
      QcTypeDto dto = Dtos.asDto(qcType);
      constantsController.refreshConstants();
      return dto;
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("QC Type", ids, qcTypeService);
  }

}