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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
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

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  private final JQueryDataTableBackend<Sample, SampleDto> jQueryBackend = new JQueryDataTableBackend<Sample, SampleDto>() {

    @Override
    protected SampleDto asDto(Sample model) {
      return Dtos.asMinimalDto(model);
    }

    @Override
    protected PaginatedDataSource<Sample> getSource() throws IOException {
      return sampleService;
    }
  };

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleDto getSample(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder)
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
    List<Sample> samples = sampleService.list();
    // return all samples
    List<SampleDto> sampleDtos = Dtos.asSampleDtos(samples, true);
    for (SampleDto sampleDto : sampleDtos) {
      sampleDto.writeUrls(uriBuilder);
    }
    return sampleDtos;
  }

  @RequestMapping(value = "/samples/dt", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamples(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/samples/dt/project/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamplesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "/sample", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SampleDto createSample(@RequestBody SampleDto sampleDto, UriComponentsBuilder b, HttpServletResponse response) throws IOException {
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
          // infer parent stock class
          SampleClass sampleClass = sampleClassService.get(dto.getSampleClassId());
          if (sampleClass == null) {
            throw new RestException("Cannot find sample class: " + dto.getSampleClassId(), Status.BAD_REQUEST);
          }
          if (!sampleClass.getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)) {
            throw new RestException("Class and type mismatch.", Status.BAD_REQUEST);
          }
          SampleClass stockClass = sampleClassService.inferStockFromAliquot(sampleClass);
          dto.setStockClassId(stockClass.getId());

          // infer grandparent tissue class
          SampleClass tissueClass = sampleClassService.inferTissueFromStock(stockClass);
          dto.setParentTissueSampleClassId(tissueClass.getId());
        }
      } else if (sampleDto instanceof SampleStockDto) {
        DetailedSampleDto dto = (DetailedSampleDto) sampleDto;
        if (dto.getParentId() != null) {
          // Pass
        } else if (dto.getSampleClassId() == null) {
          throw new RestException("No parent and no target sample class.", Status.BAD_REQUEST);
        } else {
          // infer parent tissue class
          SampleClass sampleClass = sampleClassService.get(dto.getSampleClassId());
          if (sampleClass == null) {
            throw new RestException("Cannot find sample class: " + dto.getSampleClassId(), Status.BAD_REQUEST);
          }
          if (!sampleClass.getSampleCategory().equals(SampleStock.CATEGORY_NAME)) {
            throw new RestException("Class and type mismatch.", Status.BAD_REQUEST);
          }
          SampleClass tissueClass = sampleClassService.inferTissueFromStock(sampleClass);
          dto.setParentTissueSampleClassId(tissueClass.getId());
        }
      }
      Sample sample = Dtos.to(sampleDto);
      id = sampleService.create(sample);

      addAnyQCs(sampleDto, sample);

    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating sample. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }

    SampleDto created = Dtos.asDto(sampleService.get(id));
    UriComponents uriComponents = b.path("/sample/{id}").buildAndExpand(id);
    created.setUrl(uriComponents.toUri().toString());
    response.setHeader("Location", uriComponents.toUri().toString());
    return created;
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public SampleDto updateSample(@PathVariable("id") Long id, @RequestBody SampleDto sampleDto, UriComponentsBuilder b) throws IOException {
    if (sampleDto == null) {
      log.error("Received null sampleDto from front end; cannot convert to Sample. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Sample", Status.BAD_REQUEST);
    }
    Sample sample = Dtos.to(sampleDto);
    sample.setId(id);
    sampleService.update(sample);
    addAnyQCs(sampleDto, sample);
    return getSample(id, b);
  }

  @RequestMapping(value = "/sample/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSample(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sampleService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * 
   * @param identitiesSearches
   *          String externalNames or Identity aliases
   * @param response
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/identities", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public JSONObject getIdentitiesBySearch(@RequestBody JSONObject json,
      HttpServletResponse response) throws IOException {
    if (json.getString("identitiesSearches").length() == 0) {
      throw new RestException("Must give search terms to look up identities", Status.BAD_REQUEST);
    }
    Integer requestCounter = (Integer) json.get("requestCounter");
    Set<SampleDto> matchingIdentities = new HashSet<>();
    String searchTerms = json.getString("identitiesSearches");
    for (String term : SampleIdentityImpl.getSetFromString(searchTerms.replaceAll(";", ","))) {
      Collection<SampleIdentity> matches = sampleService.getIdentitiesByExternalNameOrAlias(term);
      for (SampleIdentity identity : matches) {
        matchingIdentities.add(Dtos.asDto(identity));
      }
    }
    JSONObject allIdentities = new JSONObject();
    allIdentities.put("requestCounter", requestCounter);
    allIdentities.put("matchingIdentities", matchingIdentities);
    return allIdentities;
  }

  private void addAnyQCs(SampleDto sampleDto, Sample sample) throws IOException {
    if (sampleDto.getQcRin() != null) {
      SampleQC qc = new SampleQCImpl();
      qc.setQcType(sampleService.getSampleQcTypeByName("RIN"));
      qc.setResults(sampleDto.getQcRin());
      sampleService.addQc(sample, qc);
    }
    if (sampleDto.getQcDv200() != null) {
      SampleQC qc = new SampleQCImpl();
      qc.setQcType(sampleService.getSampleQcTypeByName("DV200"));
      qc.setResults(sampleDto.getQcDv200());
      sampleService.addQc(sample, qc);
    }
  }

}