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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
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

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Controller
@RequestMapping("/rest/tree/")
@SessionAttributes("sample")
public class SampleController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleController.class);

  @Autowired
  private SampleService sampleService;

  @Autowired
  private SampleClassService sampleClassService;

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleDto getSample(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    Sample sample = sampleService.get(id);
    if (sample == null) {
      throw new RestException("No sample found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleDto dto = Dtos.asDto(sample);
      dto.writeUrls(uriBuilder);
      return dto;
    }
  }

  @RequestMapping(value = "/samples", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public List<SampleDto> getSamples(UriComponentsBuilder uriBuilder) throws IOException {
    List<Sample> samples = sampleService.getAll();
    // return all samples
    List<SampleDto> sampleDtos = Dtos.asSampleDtos(samples);
    for (SampleDto sampleDto : sampleDtos) {
      sampleDto.writeUrls(uriBuilder);
    }
    return sampleDtos;
  }

  @RequestMapping(value = "/samples/dt", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamples(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    if (request.getParameterMap().size() > 0) {
      Long numSamples = sampleService.countAll();
      // get request params from DataTables
      Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
      Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
      String sSearch = request.getParameter("sSearch");
      String sSortDir = request.getParameter("sSortDir_0");
      String sortColIndex = request.getParameter("iSortCol_0");
      String sortCol = request.getParameter("mDataProp_" + sortColIndex);

      // get requested subset of samples
      Collection<Sample> sampleSubset;
      Long numMatches;

      if (!isStringEmptyOrNull(sSearch)) {
        sampleSubset = sampleService.getByPageAndSizeAndSearch(iDisplayStart, iDisplayLength, sSearch, sortCol, sSortDir);
        numMatches = sampleService.countBySearch(sSearch);
      } else {
        sampleSubset = sampleService.getByPageAndSize(iDisplayStart, iDisplayLength, sortCol, sSortDir);
        numMatches = numSamples;
      }
      List<SampleDto> sampleDtos = Dtos.asSampleDtos(sampleSubset);
      for (SampleDto sampleDto : sampleDtos) {
        sampleDto.writeUrls(uriBuilder);
      }

      DataTablesResponseDto<SampleDto> dtResponse = new DataTablesResponseDto<>();
      dtResponse.setITotalRecords(numSamples);
      dtResponse.setITotalDisplayRecords(numMatches);
      dtResponse.setAaData(sampleDtos);
      dtResponse.setSEcho(new Long(request.getParameter("sEcho")));
      return dtResponse;
    } else {
      throw new RestException("Malformed Request: must send parameters in request to endpoint /samples/dt");
    }
  }

  @RequestMapping(value = "/sample", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSample(@RequestBody SampleDto sampleDto, UriComponentsBuilder b) throws IOException {
    if (sampleDto == null) {
      log.error("Received null sampleDto from front end; cannot convert to Sample. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Sample", Status.BAD_REQUEST);
    }
    Long id = null;
    try {
      if (sampleDto instanceof SampleAliquotDto) {
        SampleAliquotDto dto = (SampleAliquotDto) sampleDto;
        if (dto.getParentId() != null) {
          // Pass
        } else if (dto.getSampleClassId() == null) {
          throw new RestException("No parent and no target sample class.", Status.BAD_REQUEST);
        } else {
          SampleClass sampleClass = sampleClassService.get(dto.getSampleClassId());
          if (sampleClass == null) {
            throw new RestException("Cannot find sample class: " + dto.getSampleClassId(), Status.BAD_REQUEST);
          }
          if (!sampleClass.getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)) {
            throw new RestException("Class and type mismatch.", Status.BAD_REQUEST);
          }
          SampleClass stockClass = sampleClassService.inferStockFromAliquot(sampleClass);
          dto.setStockClassId(stockClass.getId());
        }
      }
      Sample sample = Dtos.to(sampleDto);
      id = sampleService.create(sample);
    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating sample. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }
    UriComponents uriComponents = b.path("/sample/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSample(@PathVariable("id") Long id, @RequestBody SampleDto sampleDto) throws IOException {
    if (sampleDto == null) {
      log.error("Received null sampleDto from front end; cannot convert to Sample. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Sample", Status.BAD_REQUEST);
    }
    Sample sample = Dtos.to(sampleDto);
    sample.setId(id);
    sampleService.update(sample);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSample(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}