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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.dto.DetailedQcStatusDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("detailedqcstatus")
public class DetailedQcStatusController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(DetailedQcStatusController.class);

  @Autowired
  private DetailedQcStatusService detailedQcStatusService;

  @RequestMapping(value = "/detailedqcstatus/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DetailedQcStatusDto getDetailedQcStatus(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    DetailedQcStatus detailedQcStatus = detailedQcStatusService.get(id);
    if (detailedQcStatus == null) {
      throw new RestException("No detailed QC status found with ID: " + id, Status.NOT_FOUND);
    } else {
      DetailedQcStatusDto dto = Dtos.asDto(detailedQcStatus);
      dto.writeUrls(uriBuilder);
      return dto;
    }
  }

  @RequestMapping(value = "/detailedqcstatuses", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<DetailedQcStatusDto> getDetailedQcStatuses(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    Set<DetailedQcStatus> detailedQcStatuses = detailedQcStatusService.getAll();
    Set<DetailedQcStatusDto> detailedQcStatusDtos = Dtos.asDetailedQcStatusDtos(detailedQcStatuses);
    for (DetailedQcStatusDto detailedQcStatusDto : detailedQcStatusDtos) {
      detailedQcStatusDto.writeUrls(uriBuilder);
    }
    return detailedQcStatusDtos;
  }

  @RequestMapping(value = "/detailedqcstatus", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createDetailedQcStatus(@RequestBody DetailedQcStatusDto detailedQcStatusDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    DetailedQcStatus detailedQcStatus = Dtos.to(detailedQcStatusDto);
    Long id = detailedQcStatusService.create(detailedQcStatus);
    UriComponents uriComponents = b.path("/detailedqcstatus/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/detailedqcstatus/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateDetailedQcStatus(@PathVariable("id") Long id, @RequestBody DetailedQcStatusDto detailedQcStatusDto,
      HttpServletResponse response) throws IOException {
    DetailedQcStatus detailedQcStatus = Dtos.to(detailedQcStatusDto);
    detailedQcStatus.setId(id);
    detailedQcStatusService.update(detailedQcStatus);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/detailedqcstatus/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteDetailedQcStatus(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    detailedQcStatusService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
}