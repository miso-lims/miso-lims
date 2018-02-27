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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleLCMTubeDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueProcessingDto;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/sample")
@SessionAttributes("sample")
public class SampleRestController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleRestController.class);

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

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json" })
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

  @RequestMapping(method = RequestMethod.GET, produces = { "application/json" })
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

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamples(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/dt/project/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamplesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "/dt/project/{id}/arrayed", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTArrayedSamplesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id), PaginationFilter.arrayed(true));
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SampleDto createSample(@RequestBody SampleDto sampleDto, UriComponentsBuilder b, HttpServletResponse response) throws IOException {
    if (sampleDto == null) {
      log.error("Received null sampleDto from front end; cannot convert to Sample. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Sample", Status.BAD_REQUEST);
    }
    Long id = null;
    try {
      Sample sample = buildHierarchy(sampleDto);
      id = sampleService.create(sample);
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

  /**
   * Converts the DTO to a Sample, complete with parents. Parent SampleClasses are inferred where necessary
   * 
   * @param sampleDto
   * @return
   */
  public Sample buildHierarchy(SampleDto sampleDto) {
    if (sampleDto instanceof SampleAliquotDto) {
      SampleAliquotDto dto = (SampleAliquotDto) sampleDto;
      // Some hierarchies have two Aliquot levels
      dto.setParentAliquotClassId(inferIntermediateSampleClassId(dto, dto.getSampleClassId(), SampleAliquot.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME, true));
      Long topAliquotClassId = dto.getParentAliquotClassId() == null ? dto.getSampleClassId() : dto.getParentAliquotClassId();
      dto.setStockClassId(inferIntermediateSampleClassId(dto, topAliquotClassId, SampleAliquot.CATEGORY_NAME, SampleStock.CATEGORY_NAME,
          false));
      // infer grandparent tissue class
      dto.setParentTissueSampleClassId(inferIntermediateSampleClassId(dto, dto.getStockClassId(),
          SampleStock.CATEGORY_NAME, SampleTissue.CATEGORY_NAME, false));
    } else if (sampleDto instanceof SampleStockDto) {
      DetailedSampleDto dto = (DetailedSampleDto) sampleDto;
      dto.setParentTissueSampleClassId(
          inferIntermediateSampleClassId(dto, dto.getSampleClassId(), SampleStock.CATEGORY_NAME,
              SampleTissue.CATEGORY_NAME, false));
    } else if (sampleDto instanceof SampleTissueProcessingDto) {
      DetailedSampleDto dto = (DetailedSampleDto) sampleDto;
      Long topProcessingClassId = dto.getSampleClassId();
      if (sampleDto instanceof SampleLCMTubeDto) {
        SampleLCMTubeDto lcmDto = (SampleLCMTubeDto) dto;
        // Some hierarchies have two Tissue Processing levels
        lcmDto.setParentSlideClassId(inferIntermediateSampleClassId(dto, dto.getSampleClassId(), SampleTissueProcessing.CATEGORY_NAME,
            SampleTissueProcessing.CATEGORY_NAME, true));
        if (lcmDto.getParentSlideClassId() != null) {
          topProcessingClassId = lcmDto.getParentSlideClassId();
        }
      }
      dto.setParentTissueSampleClassId(
          inferIntermediateSampleClassId(dto, topProcessingClassId, SampleTissueProcessing.CATEGORY_NAME,
              SampleTissue.CATEGORY_NAME, false));
    }
    return Dtos.to(sampleDto);
  }

  private Long inferIntermediateSampleClassId(DetailedSampleDto dto, Long childClassId,
      String childClassCategory, String parentCategory, boolean nullOk) {
    if (dto.getParentId() != null) {
      return null;
    }
    if (childClassId == null) {
      throw new RestException("No parent and no target sample class.", Status.BAD_REQUEST);
    }
    // infer parent class
    SampleClass parentClass = sampleClassService.inferParentFromChild(childClassId, childClassCategory, parentCategory);
    if (parentClass == null && !nullOk) {
      throw new IllegalStateException(String.format("%s class with id %d has no %s parents", childClassCategory, childClassId,
          parentCategory));
    }
    return parentClass == null ? null : parentClass.getId();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
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
    return getSample(id, b);
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
  public Set<SampleDto> getIdentitiesBySearch(@RequestBody com.fasterxml.jackson.databind.JsonNode json,
      HttpServletResponse response) throws IOException {
    if (json.get("identitiesSearches").asText().length() == 0) {
      throw new RestException("Must give search terms to look up identities", Status.BAD_REQUEST);
    }
    Set<Sample> uniqueIdentities = new HashSet<>();
    String searchTerms = json.get("identitiesSearches").asText();
    for (String term : SampleIdentityImpl.getSetFromString(searchTerms.replaceAll(";", ","))) {
      Collection<SampleIdentity> matches = sampleService.getIdentitiesByExternalNameOrAlias(term);
      for (SampleIdentity identity : matches) {
        uniqueIdentities.add(identity);
      }
    }
    return uniqueIdentities.stream().map(Dtos::asDto).collect(Collectors.toSet());
  }

  @RequestMapping(value = "/query", method = RequestMethod.POST, produces = { "application/json" })
  @ResponseBody
  public List<SampleDto> getSamplesInBulk(@RequestBody List<String> names, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, sampleService, Dtos::asDto, message -> new RestException(message, Status.BAD_REQUEST));
  }

  @RequestMapping(value = "/spreadsheet", method = RequestMethod.GET)
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(HttpServletRequest request, HttpServletResponse response, UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(sampleService::get, SampleSpreadSheets::valueOf, request, response);
  }

  @RequestMapping(value = "/parents/{category}", method = RequestMethod.POST)
  @ResponseBody
  public List<SampleDto> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) {
    Class<? extends DetailedSample> targetClass;
    switch (category) {
    case SampleIdentity.CATEGORY_NAME:
      targetClass = SampleIdentity.class;
      break;
    case SampleTissue.CATEGORY_NAME:
      targetClass = SampleTissue.class;
      break;
    case SampleTissueProcessing.CATEGORY_NAME:
      targetClass = SampleTissueProcessing.class;
      break;
    case SampleStock.CATEGORY_NAME:
      targetClass = SampleStock.class;
      break;
    case SampleAliquot.CATEGORY_NAME:
      targetClass = SampleAliquot.class;
      break;
    default:
      throw new RestException(String.format("No such category %s.", category), Status.NOT_FOUND);
    }
    return ids.stream()//
        .map(WhineyFunction.rethrow(sampleService::get)).map(sample -> {
          if (sample instanceof DetailedSample) {
            DetailedSample parent = LimsUtils.getParent(targetClass, (DetailedSample) sample);
            if (parent == null) {
              throw new RestException(String.format("%s (%s) has no %s.", sample.getName(), sample.getAlias(), category),
                  Status.BAD_REQUEST);
            }
            return parent;
          } else {
            return null;
          }
        })//
        .collect(Collectors.groupingBy(Sample::getId)).values().stream()//
        .map(l -> l.get(0))//
        .map(Dtos::asDto)//
        .collect(Collectors.toList());
  }

  @RequestMapping(value = "/bulk-delete", method = RequestMethod.POST)
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<Sample> samples = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null sample", Status.BAD_REQUEST);
      }
      Sample sample = sampleService.get(id);
      if (sample == null) {
        throw new RestException("Sample " + id + " not found", Status.BAD_REQUEST);
      }
      samples.add(sample);
    }
    sampleService.bulkDelete(samples);
  }

}