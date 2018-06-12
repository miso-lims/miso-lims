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
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcTypeDto;
import uk.ac.bbsrc.tgac.miso.service.QcTypeService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("qctype")
public class QcTypeController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(QcTypeController.class);

  @Autowired
  private QcTypeService qcTypeService;

  @RequestMapping(value = "/qctype/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public QcTypeDto getQcType(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    QcType qcType = qcTypeService.get(id);
    if (qcType == null) {
      throw new RestException("No subproject found with ID: " + id, Status.NOT_FOUND);
    } else {
      QcTypeDto dto = Dtos.asDto(qcType);
      return dto;
    }
  }

  @RequestMapping(value = "/qctypes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<QcTypeDto> getQcTypes(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<QcType> qcTypes = (Set<QcType>) qcTypeService.getAll();
    Set<QcTypeDto> qcTypeDtos = Dtos.asQcTypeDtos(qcTypes);
    return qcTypeDtos;
  }

  @RequestMapping(value = "/qctype", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public QcTypeDto createQcType(@RequestBody QcTypeDto qcTypeDto, UriComponentsBuilder uriBuilder,
      HttpServletResponse response)
      throws IOException {
    QcType qcType = Dtos.to(qcTypeDto);
    Long id = qcTypeService.create(qcType);
    return getQcType(id, uriBuilder, response);
  }

  @RequestMapping(value = "/qctype/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public QcTypeDto updateSubproject(@PathVariable("id") Long id, @RequestBody QcTypeDto qcTypeDto,
      UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    QcType qcType = Dtos.to(qcTypeDto);
    qcType.setQcTypeId(id);
    qcTypeService.update(qcType);
    return getQcType(id, uriBuilder, response);
  }

}