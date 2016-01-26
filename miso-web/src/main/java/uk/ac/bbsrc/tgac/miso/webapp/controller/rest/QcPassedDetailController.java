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

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcPassedDetailDto;
import uk.ac.bbsrc.tgac.miso.service.QcPassedDetailService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("qcpasseddetail")
public class QcPassedDetailController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(QcPassedDetailController.class);

  @Autowired
  private QcPassedDetailService qcPassedDetailService;

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public QcPassedDetailDto getQcPassedDetail(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) {
    QcPassedDetail qcPassedDetail = qcPassedDetailService.get(id);
    if (qcPassedDetail == null) {
      throw new RestException("No QC passed detail found with ID: " + id, Status.NOT_FOUND);
    } else {
      QcPassedDetailDto dto = Dtos.asDto(qcPassedDetail);
      dto = writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  private static QcPassedDetailDto writeUrls(QcPassedDetailDto qcPassedDetailDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    qcPassedDetailDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/qcpasseddetail/{id}")
        .buildAndExpand(qcPassedDetailDto.getId()).toUriString());
    qcPassedDetailDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(qcPassedDetailDto.getCreatedById()).toUriString());
    qcPassedDetailDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(qcPassedDetailDto.getUpdatedById()).toUriString());
    return qcPassedDetailDto;
  }

  @RequestMapping(value = "/qcpasseddetails", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<QcPassedDetailDto> getQcPassedDetail(UriComponentsBuilder uriBuilder, HttpServletResponse response) {
    Set<QcPassedDetail> qcPassedDetails = qcPassedDetailService.getAll();
    if (qcPassedDetails.isEmpty()) {
      throw new RestException("No QC passed details found", Status.NOT_FOUND);
    } else {
      Set<QcPassedDetailDto> qcPassedDetailDtos = Dtos.asQcPassedDetailDtos(qcPassedDetails);
      for (QcPassedDetailDto qcPassedDetailDto : qcPassedDetailDtos) {
        qcPassedDetailDto = writeUrls(qcPassedDetailDto, uriBuilder);
      }
      return qcPassedDetailDtos;
    }
  }

  @RequestMapping(value = "/qcpasseddetail", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createQcPassedDetail(@RequestBody QcPassedDetailDto qcPassedDetailDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    QcPassedDetail qcPassedDetail = Dtos.to(qcPassedDetailDto);
    Long id = qcPassedDetailService.create(qcPassedDetail);
    UriComponents uriComponents = b.path("/qcpasseddetails/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateQcPassedDetail(@PathVariable("id") Long id, @RequestBody QcPassedDetailDto qcPassedDetailDto,
      HttpServletResponse response) throws IOException {
    QcPassedDetail qcPassedDetail = Dtos.to(qcPassedDetailDto);
    qcPassedDetail.setQcPassedDetailId(id);
    qcPassedDetailService.update(qcPassedDetail);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteQcPassedDetail(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    qcPassedDetailService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
}