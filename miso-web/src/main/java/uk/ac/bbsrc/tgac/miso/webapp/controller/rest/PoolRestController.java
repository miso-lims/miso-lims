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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

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

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.LibraryAliquotSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.spreadsheet.PoolSpreadSheets;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.ListPoolViewService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderSummaryViewService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.util.IlluminaExperiment;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyConsumer;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse.PoolPickerEntry;

/**
 * A controller to handle all REST requests for Pools
 * 
 * Created by IntelliJ IDEA. User: bianx Date: 17-Aug-2011 Time: 11:34:04
 */
@Controller
@RequestMapping("/rest/pools")
public class PoolRestController extends RestController {
  @Autowired
  private IndexChecker indexChecker;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  public static class PoolChangeRequest {
    private List<Long> add;
    private List<Long> remove;

    public List<Long> getAdd() {
      return add;
    }

    public List<Long> getRemove() {
      return remove;
    }

    public void setAdd(List<Long> add) {
      this.add = add;
    }

    public void setRemove(List<Long> remove) {
      this.remove = remove;
    }
  }

  public static class SampleSheetRequest {
    private String customIndexPrimer;
    private String customRead1Primer;
    private String customRead2Primer;
    private String experimentType;
    private String genomeFolder;
    private List<Long> poolIds;
    private long sequencingParametersId;

    public String getCustomIndexPrimer() {
      return customIndexPrimer;
    }

    public void setCustomRead1Primer(String customRead1Primer) {
      this.customRead1Primer = customRead1Primer;
    }

    public String getCustomRead2Primer() {
      return customRead2Primer;
    }

    public String getExperimentType() {
      return experimentType;
    }

    public String getGenomeFolder() {
      return genomeFolder;
    }

    public List<Long> getPoolIds() {
      return poolIds;
    }

    public long getSequencingParametersId() {
      return sequencingParametersId;
    }

    public void setCustomIndexPrimer(String customIndexPrimer) {
      this.customIndexPrimer = customIndexPrimer;
    }

    public String getCustomRead1Primer() {
      return customRead1Primer;
    }

    public void setCustomRead2Primer(String customRead2Primer) {
      this.customRead2Primer = customRead2Primer;
    }

    public void setExperimentType(String experimentType) {
      this.experimentType = experimentType;
    }

    public void setGenomeFolder(String genomeFolder) {
      this.genomeFolder = genomeFolder;
    }

    public void setPoolIds(List<Long> poolIds) {
      this.poolIds = poolIds;
    }

    public void setSequencingParametersId(long sequencingParametersId) {
      this.sequencingParametersId = sequencingParametersId;
    }

  }

  private final JQueryDataTableBackend<ListPoolView, PoolDto> jQueryBackend = new JQueryDataTableBackend<ListPoolView, PoolDto>() {

    @Override
    protected PoolDto asDto(ListPoolView model) {
      return Dtos.asDto(model, indexChecker);
    }

    @Override
    protected PaginatedDataSource<ListPoolView> getSource() throws IOException {
      return listPoolViewService;
    }

  };

  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private ListPoolViewService listPoolViewService;
  @Autowired
  private RunService runService;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private SequencingOrderSummaryViewService sequencingOrderCompletionService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @GetMapping(value = "{poolId}", produces = "application/json")
  public @ResponseBody PoolDto getPoolById(@PathVariable long poolId) throws IOException {
    return RestUtils.getObject("Pool", poolId, poolService, this::makePoolDto);
  }

  private PoolDto makePoolDto(Pool pool) {
    return Dtos.asDto(pool, true, false, indexChecker);
  }

  private PoolDto makeEmptyPoolDto(Pool pool) {
    return Dtos.asDto(pool, false, false, indexChecker);
  }

  @GetMapping(value = "{poolId}/runs", produces = "application/json")
  public @ResponseBody List<RunDto> getRunsByPoolId(@PathVariable Long poolId) throws IOException {
    Collection<Run> rr = runService.listByPoolId(poolId);
    return Dtos.asRunDtos(rr);
  }

  @PostMapping(produces = "application/json")
  @ResponseBody
  public PoolDto createPool(@RequestBody PoolDto dto)
      throws IOException {
    return RestUtils.createObject("Pool", dto, d -> {
      Pool pool = Dtos.to(d);
      if (pool.getVolume() == null && !pool.getPoolContents().isEmpty() && pool.getPoolContents().stream()
          .map(PoolElement::getPoolableElementView)
          .allMatch(view -> view.getAliquotVolumeUsed() != null)) {
        pool.setVolume(pool.getPoolContents().stream()
            .map(PoolElement::getPoolableElementView)
            .map(PoolableElementView::getAliquotVolumeUsed)
            .reduce(BigDecimal.ZERO, (result, item) -> result.add(item)));
      }
      return pool;
    }, poolService, this::makePoolDto);
  }

  @PutMapping(value = "{poolId}", produces = "application/json")
  @ResponseBody
  public PoolDto updatePool(@PathVariable long poolId, @RequestBody PoolDto dto) throws IOException {
    return RestUtils.updateObject("Pool", poolId, dto, Dtos::to, poolService, this::makePoolDto);
  }

  @PutMapping(value = "/{poolId}/contents", produces = "application/json")
  public @ResponseBody PoolDto changePoolContents(@PathVariable Long poolId, @RequestBody PoolChangeRequest request) throws IOException {
    Pool pool = poolService.get(poolId);
    Stream<PoolElement> originalMinusRemoved = pool.getPoolContents().stream()
        .filter(element -> !request.remove.contains(element.getPoolableElementView().getAliquotId()));
    Stream<PoolElement> added = poolableElementViewService.list(request.add).stream()
        .map(view -> new PoolElement(pool, view));
    pool.setPoolElements(Stream.concat(originalMinusRemoved, added).collect(Collectors.toSet()));
    poolService.update(pool);
    return makePoolDto(poolService.get(poolId));
  }

  @PutMapping(value = "/{poolId}/proportions")
  public @ResponseBody PoolDto changeProportions(@PathVariable long poolId, @RequestBody Map<String, Integer> proportions)
      throws IOException {
    if (proportions == null || proportions.isEmpty()) {
      throw new RestException("Proportions missing", Status.BAD_REQUEST);
    }
    Pool pool = poolService.get(poolId);
    if (pool == null) {
      throw new RestException("Pool not found", Status.NOT_FOUND);
    }
    for (Entry<String, Integer> entry : proportions.entrySet()) {
      if (entry.getValue() == null || entry.getValue() < 1) {
        throw new RestException("Invalid proportion: " + entry.getValue(), Status.BAD_REQUEST);
      }
      PoolElement poolElement = pool.getPoolContents().stream()
          .filter(pd -> pd.getPoolableElementView().getAliquotName().equals(entry.getKey()))
          .findFirst()
          .orElseThrow(() -> new RestException("Invalid library aliquot name: " + entry.getKey(), Status.BAD_REQUEST));
      poolElement.setProportion(entry.getValue());
    }
    poolService.update(pool);
    return makePoolDto(poolService.get(poolId));
  }

  public static class AssignPoolDto {
    private List<Long> partitionIds;
    private String concentration;
    private ConcentrationUnit units;

    public List<Long> getPartitionIds() {
      return partitionIds;
    }

    public void setPartitionIds(List<Long> partitionIds) {
      this.partitionIds = partitionIds;
    }

    public String getConcentration() {
      return concentration;
    }

    public void setConcentration(String concentration) {
      this.concentration = concentration;
    }

    public ConcentrationUnit getUnits() {
      return units;
    }

    public void setUnits(ConcentrationUnit units) {
      this.units = units;
    }
  }

  @PostMapping(value = "/{poolId}/assign", produces = "application/json")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void assignPool(@PathVariable Long poolId, @RequestBody AssignPoolDto request) throws IOException {
    Pool pool = poolId == 0 ? null : poolService.get(poolId);

    // Determine if this pool transition is allowed for this experiment. If removing a pool, it strictly isn't. If the new pool contains the
    // same library as the experiment, it's fine.
    Predicate<Experiment> isTransitionValid = pool == null ? experiment -> false
        : experiment -> pool.getPoolContents().stream().map(pd -> pd.getPoolableElementView().getLibraryId())
            .anyMatch(id -> id == experiment.getLibrary().getId());

    request.getPartitionIds().stream()//
        .map(WhineyFunction.rethrow(containerService::getPartition))//
        .peek(WhineyConsumer.rethrow(partition -> {
          for (RunPosition runPos : partition.getSequencerPartitionContainer().getRunPositions()) {
            Run run = runPos.getRun();
            // Check that we aren't going to hose any existing experiments through this reassignment
            boolean relatedExperimentsOkay = experimentService.listAllByRunId(run.getId()).stream()//
                .flatMap(experiment -> experiment.getRunPartitions().stream())//
                .filter(rp -> rp.getRun().getId() == run.getId() && rp.getPartition().getId() == partition.getId())//
                .map(RunPartition::getExperiment)//
                .allMatch(isTransitionValid);
            if (!relatedExperimentsOkay) {
              throw new RestException(
                  String.format("%s %d is used in an experiment.",
                      partition.getSequencerPartitionContainer().getModel().getPlatformType().getPartitionName(),
                      partition.getPartitionNumber()),
                  Status.BAD_REQUEST);
            }
          }
          if (pool != null && partition.getSequencerPartitionContainer().getModel().getPlatformType() != pool.getPlatformType()) {
            throw new RestException(
                String.format("%s %d in %s is not compatible with pool %s.",
                    partition.getSequencerPartitionContainer().getModel().getPlatformType().getPartitionName(),
                    partition.getPartitionNumber(), partition.getSequencerPartitionContainer().getIdentificationBarcode(), pool.getName()),
                Status.BAD_REQUEST);
          }
          partition.setPool(pool);
          if (request.getConcentration() != null) {
            partition.setLoadingConcentration(new BigDecimal(request.getConcentration()));
            partition.setLoadingConcentrationUnits(request.getUnits());
          } else {
            partition.setLoadingConcentration(null);
            partition.setLoadingConcentrationUnits(null);
          }
        }))//
        .forEach(WhineyConsumer.rethrow(containerService::update));
    if (pool != null) {
      poolService.update(pool);
    }
  }

  @GetMapping(value = "platform/{platform}", produces = "application/json")
  @ResponseBody
  public List<PoolDto> getPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    if (PlatformType.getKeys().contains(platform)) {
      Collection<Pool> pools = new ArrayList<>();
      PlatformType platformType = PlatformType.get(platform);
      pools = poolService.listByPlatform(platformType);
      return serializePools(pools, uriBuilder);
    } else {
      throw new RestException("Request must specify a platform");
    }
  }

  @GetMapping(value = "dt/platform/{platform}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PoolDto> getDTPoolsByPlatform(@PathVariable("platform") String platform, HttpServletRequest request)
      throws IOException {
    PlatformType platformType = PlatformType.valueOf(platform);
    if (platformType == null) {
      throw new RestException("Invalid platform type.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.platformType(platformType));
  }

  @GetMapping(value = "dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PoolDto> getDTPoolsByProject(@PathVariable("id") Long id, HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  public List<PoolDto> serializePools(Collection<Pool> pools, UriComponentsBuilder uriBuilder)
      throws IOException {
    List<PoolDto> poolDtos = pools.stream().map(this::makeEmptyPoolDto)
        .collect(Collectors.toList());
    return poolDtos;
  }

  @GetMapping(value = "/picker/search")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform, @RequestParam("query") String query)
      throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.query(query));
  }

  @GetMapping(value = "/picker/recent")
  @ResponseBody
  public PoolPickerResponse getPickersBySearch(@RequestParam("platform") String platform)
      throws IOException {
    return getPoolPickerWithFilters(20,
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  private PoolPickerResponse getPoolPickerWithFilters(Integer limit, PaginationFilter... filters) throws IOException {
    PoolPickerResponse ppr = new PoolPickerResponse();
    ppr.populate(poolService, false, "lastModified", limit, this::poolTransform, filters);
    return ppr;
  }

  private PoolPickerEntry poolTransform(Pool pool) throws IOException {
    List<SequencingOrderCompletionDto> completions = sequencingOrderCompletionService.listByPoolId(pool.getId()).stream()
        .map(oc -> Dtos.asDto(oc, indexChecker)).collect(Collectors.toList());
    return new PoolPickerEntry(makePoolDto(pool), completions);
  }

  @PostMapping(value = "/query", produces = { "application/json" })
  @ResponseBody
  public List<PoolDto> getPoolsInBulk(@RequestBody List<String> names, HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return PaginationFilter.bulkSearch(names, poolService, this::makeEmptyPoolDto,
        message -> new RestException(message, Status.BAD_REQUEST));
  }

  @GetMapping(value = "/search")
  public @ResponseBody List<PoolDto> search(@RequestParam("q") String search) throws IOException {
    return poolService.listBySearch(search).stream().map(pool -> Dtos.asDto(pool, true, false, indexChecker)).collect(Collectors.toList());
  }

  @PostMapping(value = "/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(request, poolService::get, detailedSample, PoolSpreadSheets::valueOf, response);
  }

  @PostMapping(value = "/contents/spreadsheet")
  @ResponseBody
  public HttpEntity<byte[]> getContentsSpreadsheet(@RequestBody SpreadsheetRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) {
    return MisoWebUtils.generateSpreadsheet(request, request.getIds().stream()//
        .flatMap(
            WhineyFunction.rethrow(id -> poolService.get(id).getPoolContents().stream()))//
        .map(pe -> pe.getPoolableElementView().getAliquot()),
        detailedSample,
        LibraryAliquotSpreadSheets::valueOf, response);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<Pool> pools = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("Cannot delete null pool", Status.BAD_REQUEST);
      }
      Pool pool = poolService.get(id);
      if (pool == null) {
        throw new RestException("Pool " + id + " not found", Status.BAD_REQUEST);
      }
      pools.add(pool);
    }
    poolService.bulkDelete(pools);
  }

  private static Stream<Sample> getSamples(Pool pool) {
    return pool.getPoolContents().stream().map(pd -> pd.getPoolableElementView().getSample());
  }

  private final RelationFinder<Pool> parentFinder = (new RelationFinder<Pool>() {

    @Override
    protected Pool fetch(long id) throws IOException {
      return poolService.get(id);
    }
  })
      .add(new RelationFinder.ParentSampleAdapter<>(SampleIdentity.CATEGORY_NAME, SampleIdentity.class, PoolRestController::getSamples))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissue.CATEGORY_NAME, SampleTissue.class, PoolRestController::getSamples))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleTissueProcessing.CATEGORY_NAME, SampleTissueProcessing.class,
          PoolRestController::getSamples))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleStock.CATEGORY_NAME, SampleStock.class, PoolRestController::getSamples))//
      .add(new RelationFinder.ParentSampleAdapter<>(SampleAliquot.CATEGORY_NAME, SampleAliquot.class, PoolRestController::getSamples))//
      .add(new RelationFinder.RelationAdapter<Pool, Sample, SampleDto>("Sample") {

        @Override
        public SampleDto asDto(Sample model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Sample> find(Pool model, Consumer<String> emitError) {
          return getSamples(model);
        }

      })
      .add(new RelationFinder.RelationAdapter<Pool, Library, LibraryDto>("Library") {

        @Override
        public LibraryDto asDto(Library model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<Library> find(Pool model, Consumer<String> emitError) {
          return model.getPoolContents().stream()
              .map(WhineyFunction.rethrow(pd -> libraryService.get(pd.getPoolableElementView().getLibraryId())));
        }
      })
      .add(new RelationFinder.RelationAdapter<Pool, LibraryAliquot, LibraryAliquotDto>("Library Aliquot") {

        @Override
        public LibraryAliquotDto asDto(LibraryAliquot model) {
          return Dtos.asDto(model, false);
        }

        @Override
        public Stream<LibraryAliquot> find(Pool model, Consumer<String> emitError) {
          return model.getPoolContents().stream()
              .map(WhineyFunction.rethrow(pd -> libraryAliquotService.get(pd.getPoolableElementView().getAliquotId())));
        }
      });

  @PostMapping(value = "/parents/{category}")
  @ResponseBody
  public HttpEntity<byte[]> getParents(@PathVariable("category") String category, @RequestBody List<Long> ids, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
    return parentFinder.list(ids, category);
  }

  @PostMapping(value = "/samplesheet")
  @ResponseBody
  public HttpEntity<byte[]> samplesheet(@RequestBody SampleSheetRequest request,
      HttpServletRequest httpRequest,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    IlluminaExperiment experiment = IlluminaExperiment.valueOf(request.getExperimentType());
    SequencingParameters parameters = sequencingParametersService.get(request.getSequencingParametersId());
    List<Pool> pools = request.getPoolIds().stream().map(WhineyFunction.rethrow(poolService::get)).collect(Collectors.toList());
    response.setHeader("Content-Disposition", String.format("attachment; filename=%s-%s.csv", experiment.name(),
        pools.stream().map(Pool::getAlias).collect(Collectors.joining("-"))));
    return new HttpEntity<>(experiment
        .makeSampleSheet(request.getGenomeFolder(), parameters, request.getCustomRead1Primer(), request.getCustomIndexPrimer(),
            request.getCustomRead2Primer(),
            pools)
        .getBytes(StandardCharsets.UTF_8));
  }

}
