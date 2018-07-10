package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
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

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunDto;
import uk.ac.bbsrc.tgac.miso.dto.ChangeLogDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.service.ArrayService;

@Controller
@RequestMapping("/rest/arrayruns")
public class ArrayRunRestController extends RestController {

  @Autowired
  private ArrayRunService arrayRunService;

  @Autowired
  private ArrayService arrayService;

  private final JQueryDataTableBackend<ArrayRun, ArrayRunDto> jQueryBackend = new JQueryDataTableBackend<ArrayRun, ArrayRunDto>() {

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
  public DataTablesResponseDto<ArrayRunDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ArrayRunDto> dataTableByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody ArrayRunDto save(@RequestBody ArrayRunDto dto) throws IOException {
    return doSave(dto);
  }

  @PutMapping(value = "/{arrayRunId}")
  public @ResponseBody ArrayRunDto update(@PathVariable(name = "arrayRunId", required = true) long arrayRunId,
      @RequestBody ArrayRunDto dto) throws IOException {
    if (dto.getId().longValue() != arrayRunId) {
      throw new RestException("Array Run ID mismatch", Status.BAD_REQUEST);
    }
    ArrayRun existing = arrayRunService.get(arrayRunId);
    if (existing == null) {
      throw new RestException("Array Run not found", Status.NOT_FOUND);
    }
    return doSave(dto);
  }

  public ArrayRunDto doSave(ArrayRunDto dto) throws IOException {
    ArrayRun run = Dtos.to(dto);
    long savedId = arrayRunService.save(run);
    ArrayRun saved = arrayRunService.get(savedId);
    return Dtos.asDto(saved);
  }

  @GetMapping(value = "/array-search")
  public @ResponseBody List<ArrayDto> findArrays(@RequestParam(name = "q", required = true) String search) throws IOException {
    if (LimsUtils.isStringEmptyOrNull(search)) {
      return new ArrayList<>();
    }
    List<Array> arrays = arrayService.getArraysBySearch(search);
    return Dtos.asArrayDtos(arrays);
  }

  @GetMapping(value = "/{arrayRunId}/changelog")
  public @ResponseBody List<ChangeLogDto> getChangelog(@PathVariable(name = "arrayRunId", required = true) long arrayRunId)
      throws IOException {
    ArrayRun run = arrayRunService.get(arrayRunId);
    if (run == null) {
      throw new RestException("Array Run not found", Status.NOT_FOUND);
    }
    return run.getChangeLog().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

}
