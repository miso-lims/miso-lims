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

import uk.ac.bbsrc.tgac.miso.core.data.QcPassedDetail;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcPassedDetailDto;
import uk.ac.bbsrc.tgac.miso.service.QcPassedDetailService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("qcpasseddetail")
public class QcPassedDetailController {

  protected static final Logger log = LoggerFactory.getLogger(QcPassedDetailController.class);

  @Autowired
  private QcPassedDetailService qcPassedDetailService;

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<QcPassedDetailDto> getQcPassedDetail(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    QcPassedDetail qcPassedDetail = qcPassedDetailService.get(id);
    if (qcPassedDetail == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      QcPassedDetailDto dto = Dtos.asDto(qcPassedDetail);
      dto = writeUrls(dto, uriBuilder);
      return new ResponseEntity<>(dto, HttpStatus.OK);
    }
  }

  private static QcPassedDetailDto writeUrls(QcPassedDetailDto qcPassedDetailDto, UriComponentsBuilder uriBuilder) {
    qcPassedDetailDto.setUrl(uriBuilder.replacePath("/rest/qcpasseddetail/{id}").buildAndExpand(qcPassedDetailDto.getId()).toUriString());
    qcPassedDetailDto
        .setCreatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(qcPassedDetailDto.getCreatedById()).toUriString());
    qcPassedDetailDto
        .setUpdatedByUrl(uriBuilder.replacePath("/rest/user/{id}").buildAndExpand(qcPassedDetailDto.getUpdatedById()).toUriString());
    return qcPassedDetailDto;
  }

  @RequestMapping(value = "/qcpasseddetails", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<Set<QcPassedDetailDto>> getQcPassedDetail(UriComponentsBuilder uriBuilder) {
    Set<QcPassedDetail> qcPassedDetails = qcPassedDetailService.getAll();
    if (qcPassedDetails.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Set<QcPassedDetailDto> qcPassedDetailDtos = Dtos.asQcPassedDetailDtos(qcPassedDetails);
      for (QcPassedDetailDto qcPassedDetailDto : qcPassedDetailDtos) {
        qcPassedDetailDto = writeUrls(qcPassedDetailDto, uriBuilder);
      }
      return new ResponseEntity<>(qcPassedDetailDtos, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/qcpasseddetail", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createQcPassedDetail(@RequestBody QcPassedDetailDto qcPassedDetailDto, UriComponentsBuilder b)
      throws IOException {
    QcPassedDetail qcPassedDetail = Dtos.to(qcPassedDetailDto);
    Long id = qcPassedDetailService.create(qcPassedDetail);
    UriComponents uriComponents = b.path("/qcpasseddetails/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateQcPassedDetail(@PathVariable("id") Long id, @RequestBody QcPassedDetailDto qcPassedDetailDto)
      throws IOException {
    QcPassedDetail qcPassedDetail = Dtos.to(qcPassedDetailDto);
    qcPassedDetail.setQcPassedDetailId(id);
    qcPassedDetailService.update(qcPassedDetail);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/qcpasseddetail/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteQcPassedDetail(@PathVariable("id") Long id) throws IOException {
    qcPassedDetailService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}