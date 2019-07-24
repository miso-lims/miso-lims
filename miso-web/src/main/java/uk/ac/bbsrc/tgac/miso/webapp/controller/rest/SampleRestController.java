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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.SampleSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DetailedSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleLCMTubeDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleStockDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueProcessingDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

@Controller
@RequestMapping("/rest/samples")
public class SampleRestController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleRestController.class);

  @Autowired
  private SampleService sampleService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private ProjectService projectService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  @Value("${miso.error.edit.distance:2}")
  public int errorEditDistance;
  @Value("${miso.warning.edit.distance:3}")
  public int warningEditDistance;

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

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public SampleDto getSample(@PathVariable long id) throws IOException {
    return RestUtils.getObject("Sample", id, sampleService, sam -> Dtos.asDto(sam, false));
  }

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public List<SampleDto> getSamples(UriComponentsBuilder uriBuilder) throws IOException {
    List<Sample> samples = sampleService.list();
    // return all samples
    List<SampleDto> sampleDtos = Dtos.asSampleDtos(samples, true);
    return sampleDtos;
  }

  @GetMapping(value = "/dt", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamples(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/project/{id}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTSamplesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @GetMapping(value = "/dt/project/{id}/arrayed", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SampleDto> getDTArrayedSamplesByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id), PaginationFilter.arrayed(true));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody SampleDto createSample(@RequestBody SampleDto sampleDto)
      throws IOException {
    return RestUtils.createObject("Sample", sampleDto, WhineyFunction.rethrow(this::buildHierarchy), sampleService,
        sam -> Dtos.asDto(sam, false));
  }

  /**
   * Converts the DTO to a Sample, complete with parents. Parent SampleClasses are inferred where necessary
   * 
   * @param sampleDto
   * @return
   * @throws IOException
   */
  public Sample buildHierarchy(SampleDto sampleDto) throws IOException {
    if (sampleDto instanceof SampleAliquotDto) {
      SampleAliquotDto dto = (SampleAliquotDto) sampleDto;
      // Some hierarchies have two Aliquot levels
      dto.setParentAliquotClassId(inferIntermediateSampleClassId(dto, dto.getSampleClassId(), SampleAliquot.CATEGORY_NAME,
          SampleAliquot.CATEGORY_NAME, true));
      Long topAliquotClassId = dto.getParentAliquotClassId() == null ? dto.getSampleClassId() : dto.getParentAliquotClassId();
      dto.setStockClassId(
          inferIntermediateSampleClassId(dto, topAliquotClassId, SampleAliquot.CATEGORY_NAME, SampleStock.CATEGORY_NAME, false));
      if (dto.getParentId() == null) {
        // infer tissue processing class if necessary
        SampleClass processingClass = sampleClassService.getRequiredTissueProcessingClass(dto.getStockClassId());
        if (processingClass != null) {
          dto.setTissueProcessingClassId(processingClass.getId());
          // infer tissue class
          dto.setParentTissueSampleClassId(inferIntermediateSampleClassId(dto, dto.getTissueProcessingClassId(),
              SampleTissueProcessing.CATEGORY_NAME, SampleTissue.CATEGORY_NAME, false));
        } else {
          // infer tissue class
          dto.setParentTissueSampleClassId(inferIntermediateSampleClassId(dto, dto.getStockClassId(),
              SampleStock.CATEGORY_NAME, SampleTissue.CATEGORY_NAME, false));
        }
      }
    } else if (sampleDto instanceof SampleStockDto) {
      // infer tissue processing class if necessary
      SampleStockDto dto = (SampleStockDto) sampleDto;
      SampleClass processingClass = sampleClassService.getRequiredTissueProcessingClass(dto.getSampleClassId());
      if (processingClass != null) {
        dto.setTissueProcessingClassId(processingClass.getId());
        // infer tissue class
        dto.setParentTissueSampleClassId(
            inferIntermediateSampleClassId(dto, dto.getTissueProcessingClassId(), SampleTissueProcessing.CATEGORY_NAME,
                SampleTissue.CATEGORY_NAME, false));
      } else {
        // infer tissue class
        dto.setParentTissueSampleClassId(
            inferIntermediateSampleClassId(dto, dto.getSampleClassId(), SampleStock.CATEGORY_NAME,
                SampleTissue.CATEGORY_NAME, false));
      }
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

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody SampleDto updateSample(@PathVariable long id, @RequestBody SampleDto sampleDto) throws IOException {
    return RestUtils.updateObject("Sample", id, sampleDto, Dtos::to, sampleService, sam -> Dtos.asDto(sam, false));
  }

  /**
   * Given a list of external name search terms, returns a list of identities which match those search terms, in the
   * same order as the original list. Search can be scoped by project, and can be for an exact match or for a partial match.
   * 
   * @param exactMatch whether a distinct external name of an existing identity needs to exactly match the given search term.
   * @param json a list of search terms and possibly the project shortName
   * @return a list of maps between search terms and matching identities
   */
  @PostMapping(value = "/identitiesLookup", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<Map<String, Set<SampleDto>>> getIdentitiesBySearch(@RequestParam boolean exactMatch,
      @RequestBody com.fasterxml.jackson.databind.JsonNode json,
      HttpServletResponse response) throws IOException {
    final JsonNode searchTerms = json.get("identitiesSearches");
    final String project = (json.get("project") == null ? "" : json.get("project").asText());
    if (!searchTerms.isArray() || searchTerms.size() == 0) {
      throw new RestException("Please provide external name or alias for identity lookup", Status.BAD_REQUEST);
    }
    List<Map<String, Set<SampleDto>>> identitiesBySearchTerm = new ArrayList<>();
    for (int i = 0; i < searchTerms.size(); i++) {
      JsonNode term = searchTerms.get(i);
      Set<SampleDto> uniqueIdentities = getSamplesForIdentityString(term.asText(), project, exactMatch);
      Map<String, Set<SampleDto>> found = new HashMap<>();
      found.put(term.asText(), uniqueIdentities);
      identitiesBySearchTerm.add(i, found);
    }
    return identitiesBySearchTerm;
  }

  private Set<SampleDto> getSamplesForIdentityString(String identityIdentifier, String project, boolean exactMatch)
      throws IOException {
    Collection<SampleIdentity> matches = new HashSet<>();
    Project selected = null;
    selected = projectService.getProjectByShortName(project);
    if (selected != null) {
      matches = sampleService.getIdentitiesByExternalNameOrAliasAndProject(identityIdentifier, selected.getId(), exactMatch);
    } else {
      matches = sampleService.getIdentitiesByExternalNameOrAliasAndProject(identityIdentifier, null, exactMatch);
    }
    return matches.stream().map(identity -> Dtos.asDto(identity, false)).collect(Collectors.toSet());
  }

  @PostMapping(value = "/query", produces = { "application/json" })
  @ResponseBody
  public List<SampleDto> getSamplesInBulk(@RequestBody List<String> names) {
    return PaginationFilter.bulkSearch(names, sampleService, sam -> Dtos.asDto(sam, false),
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @PostMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response) {
    return MisoWebUtils.generateSpreadsheet(request, sampleService::get, SampleSpreadSheets::valueOf, response);
  }

  private final RelationFinder<Sample> parentFinder = (new RelationFinder<Sample>() {

    @Override
    protected Sample fetch(long id) throws IOException {
      return sampleService.get(id);
    }
  })//
      .add(RelationFinder.parent(SampleIdentity.CATEGORY_NAME, SampleIdentity.class))//
      .add(RelationFinder.parent(SampleTissue.CATEGORY_NAME, SampleTissue.class))//
      .add(RelationFinder.parent(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class))//
      .add(RelationFinder.parent(SampleStock.CATEGORY_NAME, SampleStock.class))//
      .add(RelationFinder.parent(SampleAliquot.CATEGORY_NAME, SampleAliquot.class));

  @PostMapping(value = "/parents/{category}")
  @ResponseBody
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids)
      throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  private final RelationFinder<Sample> childFinder = (new RelationFinder<Sample>() {

    @Override
    protected Sample fetch(long id) throws IOException {
      return sampleService.get(id);
    }
  })
      .add(RelationFinder.child(SampleIdentity.CATEGORY_NAME, SampleIdentity.class))//
      .add(RelationFinder.child(SampleTissue.CATEGORY_NAME, SampleTissue.class))//
      .add(RelationFinder.child(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class))//
      .add(RelationFinder.child(SampleStock.CATEGORY_NAME, SampleStock.class))//
      .add(RelationFinder.child(SampleAliquot.CATEGORY_NAME, SampleAliquot.class))
      
      .add(new RelationFinder.RelationAdapter<Sample, Library, LibraryDto>("Library") {

        @Override
        public LibraryDto asDto(Library model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Library> find(Sample model, Consumer<String> emitError) {
          Set<Library> children = RelationFinder.ChildrenSampleAdapter.searchChildrenLibraries((DetailedSample) model)
              .collect(Collectors.toSet());
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      })

      .add(new RelationFinder.RelationAdapter<Sample, LibraryAliquot, LibraryAliquotDto>("Library Aliquot") {

        @Override
        public LibraryAliquotDto asDto(LibraryAliquot model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<LibraryAliquot> find(Sample model, Consumer<String> emitError) {
          Set<LibraryAliquot> children = RelationFinder.ChildrenSampleAdapter.searchChildrenLibraries((DetailedSample) model)
              .flatMap(library -> library.getLibraryAliquots().stream()).collect(Collectors.toSet());
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      })

      .add(new RelationFinder.RelationAdapter<Sample, Pool, PoolDto>("Pool") {

        @Override
        public PoolDto asDto(Pool model) {
          return Dtos.asDto(model, false, false, errorEditDistance, warningEditDistance);
        }

        @Override
        public Stream<Pool> find(Sample model, Consumer<String> emitError) {
          Set<Pool> children = RelationFinder.ChildrenSampleAdapter.searchChildrenLibraries((DetailedSample) model)
              .flatMap(library -> library.getLibraryAliquots().stream().flatMap(aliquot -> aliquot.getPools().stream()))
              .collect(Collectors.toSet());
          if (children.isEmpty()) {
            emitError.accept(String.format("%s (%s) has no %s.", model.getName(), model.getAlias(), category()));
            return Stream.empty();
          }
          return children.stream();
        }
      });

  @PostMapping(value = "/children/{category}")
  @ResponseBody
  public HttpEntity<byte[]> getChildren(@PathVariable("category") String category, @RequestBody List<Long> ids)
      throws JsonProcessingException {
    return childFinder.list(ids, category);
  }

  @PostMapping(value = "/bulk-delete")
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