package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunSampleService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunDto;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunSampleDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/arrayruns")
public class ArrayRunRestController extends AbstractRestController {

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private ArrayService arrayService;

  @Autowired
  private ArrayRunSampleService arrayRunSampleService;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  @Autowired
  private SampleService sampleService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  private final JQueryDataTableBackend<ArrayRun, ArrayRunDto> jQueryBackend =
      new JQueryDataTableBackend<ArrayRun, ArrayRunDto>() {

        @Override
        protected ArrayRunDto asDto(ArrayRun model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<ArrayRun> getSource() throws IOException {
          return arrayRunService;
        }

      };

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ArrayRunDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ArrayRunDto> dataTableByProject(@PathVariable("id") Long id, HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  @GetMapping(value = "/dt/requisition/{requisitionId}", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<ArrayRunDto> dataTableByRequisition(@PathVariable long requisitionId,
      HttpServletRequest request)
      throws IOException {

    // requisitioned
    List<Sample> samples = sampleService.list(0, 0, false, null,
        PaginationFilter.requisitionId(requisitionId)).stream()
        .collect(Collectors.toCollection(() -> new ArrayList<>()));

    // supplemental
    samples.addAll(sampleService.list(0, 0, false, null, PaginationFilter.supplementalToRequisitionId(requisitionId))
        .stream().collect(Collectors.toList()));

    List<Long> sampleIds = samples.stream().map(Sample::getId).collect(Collectors.toList());
    List<Long> familyTree = new ArrayList<Long>();

    // adds any current samples that are aliquots
    if (detailedSample) {
      for (Sample s : samples) {
        if (((DetailedSample) s).getSampleClass().getSampleCategory().equals(SampleAliquot.CATEGORY_NAME)) {
          familyTree.add(s.getId());
        }
      }
      // gets the derived aliquots from the samples
      List<Sample> aliquots = sampleService.getChildren(sampleIds, SampleAliquot.CATEGORY_NAME, requisitionId);
      familyTree.addAll(aliquots.stream().map(Sample::getId).collect(Collectors.toList()));
    } else {
      familyTree.addAll(sampleIds);
    }

    List<ArrayRun> arrayRuns = arrayRunService.listBySampleIds(familyTree);
    return jQueryBackend.get(request, advancedSearchParser,
        PaginationFilter.ids(arrayRuns.stream().map(ArrayRun::getId).toList()));
  }



  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody ArrayRunDto save(@RequestBody ArrayRunDto dto) throws IOException {
    return RestUtils.createObject("Array Run", dto, Dtos::to, arrayRunService, Dtos::asDto);
  }

  @GetMapping(value = "/{arrayRunId}/samples", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<ArrayRunSampleDto> listSamples(@PathVariable long arrayRunId,
      HttpServletRequest request) throws IOException {
    RestUtils.retrieve("Array Run", arrayRunId, arrayRunService);

    List<ArrayRunSampleDto> dtos = arrayRunSampleService.listByRunId(arrayRunId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    // Assign a temporary row ID for DataTables selection, ArrayRunSample does not have its own database
    // ID
    for (int i = 0; i < dtos.size(); i++) {
      dtos.get(i).setId((long) i);
    }
    int total = dtos.size();

    String search = request.getParameter("sSearch");
    if (search != null && !search.isBlank()) {
      String normalizedSearch = search.toLowerCase(Locale.ROOT);
      dtos = dtos.stream()
          .filter(dto -> containsIgnoreCase(dto.getPosition(), normalizedSearch)
              || containsIgnoreCase(dto.getSampleName(), normalizedSearch)
              || containsIgnoreCase(dto.getSampleAlias(), normalizedSearch)
              || containsIgnoreCase(dto.getQcNote(), normalizedSearch)
              || containsIgnoreCase(dto.getQcUserName(), normalizedSearch))
          .collect(Collectors.toList());
    }

    String sortColIndex = request.getParameter("iSortCol_0");
    String sortCol = sortColIndex == null ? null : request.getParameter("mDataProp_" + sortColIndex);
    Comparator<ArrayRunSampleDto> comparator = getArrayRunSampleComparator(sortCol);
    if (comparator != null) {
      dtos.sort("desc".equalsIgnoreCase(request.getParameter("sSortDir_0")) ? comparator.reversed() : comparator);
    }

    int filteredTotal = dtos.size();
    int start = parseRequiredInt("iDisplayStart", request.getParameter("iDisplayStart"));
    int length = parseRequiredInt("iDisplayLength", request.getParameter("iDisplayLength"));
    int fromIndex = Math.min(Math.max(start, 0), filteredTotal);
    int toIndex = length < 0 ? filteredTotal : Math.min(fromIndex + length, filteredTotal);

    DataTablesResponseDto<ArrayRunSampleDto> response = new DataTablesResponseDto<>();
    response.setITotalRecords((long) total);
    response.setITotalDisplayRecords((long) filteredTotal);
    response.setAaData(dtos.subList(fromIndex, toIndex));
    response.setSEcho((long) parseRequiredInt("sEcho", request.getParameter("sEcho")));
    return response;
  }

  @PutMapping(value = "/{arrayRunId}/samples")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void saveSamples(@PathVariable long arrayRunId, @RequestBody List<ArrayRunSampleDto> dtos) throws IOException {
    RestUtils.retrieve("Array Run", arrayRunId, arrayRunService);
    List<ArrayRunSample> samples = dtos.stream().map(Dtos::to).collect(Collectors.toList());
    arrayRunSampleService.save(samples);
  }

  @PutMapping(value = "/{arrayRunId}")
  public @ResponseBody ArrayRunDto update(@PathVariable(name = "arrayRunId", required = true) long arrayRunId,
      @RequestBody ArrayRunDto dto) throws IOException {
    return RestUtils.updateObject("Array Run", arrayRunId, dto, Dtos::to, arrayRunService, Dtos::asDto);
  }

  @GetMapping(value = "/array-search")
  public @ResponseBody List<ArrayDto> findArrays(@RequestParam(name = "q", required = true) String search)
      throws IOException {
    if (LimsUtils.isStringEmptyOrNull(search)) {
      return new ArrayList<>();
    }
    List<Array> arrays = arrayService.getArraysBySearch(search);
    return Dtos.asArrayDtos(arrays);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Array Run", ids, arrayRunService);
  }

  private static boolean containsIgnoreCase(String value, String search) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(search);
  }

  private static int parseRequiredInt(String name, String value) {
    if (value == null) {
      throw new IllegalArgumentException("Missing parameter: " + name);
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid parameter: " + name);
    }
  }

  private static Comparator<ArrayRunSampleDto> getArrayRunSampleComparator(String sortCol) {
    if (sortCol == null) {
      return Comparator.comparing(ArrayRunSampleDto::getPosition, Comparator.nullsLast(String::compareTo));
    }
    switch (sortCol) {
      case "position":
        return Comparator.comparing(ArrayRunSampleDto::getPosition, Comparator.nullsLast(String::compareTo));
      case "sampleName":
        return Comparator.comparing(ArrayRunSampleDto::getSampleName, Comparator.nullsLast(String::compareTo));
      case "sampleAlias":
        return Comparator.comparing(ArrayRunSampleDto::getSampleAlias, Comparator.nullsLast(String::compareTo));
      case "qcStatusId":
        return Comparator.comparing(ArrayRunSampleDto::getQcStatusId, Comparator.nullsLast(Long::compareTo));
      case "qcNote":
        return Comparator.comparing(ArrayRunSampleDto::getQcNote, Comparator.nullsLast(String::compareTo));
      default:
        return null;
    }
  }

}
