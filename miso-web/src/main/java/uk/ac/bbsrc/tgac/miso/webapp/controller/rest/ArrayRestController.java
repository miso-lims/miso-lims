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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.dto.ChangeLogDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Controller
@RequestMapping("/rest/arrays")
public class ArrayRestController extends RestController {

  private static final String ERROR_NOTFOUND = "Array not found";

  @Autowired
  private ArrayService arrayService;

  @Autowired
  private SampleService sampleService;

  private final JQueryDataTableBackend<Array, ArrayDto> jQueryBackend = new JQueryDataTableBackend<Array, ArrayDto>() {

    @Override
    protected ArrayDto asDto(Array model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Array> getSource() throws IOException {
      return arrayService;
    }

  };

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ArrayDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody ArrayDto save(@RequestBody ArrayDto dto) throws IOException {
    return doSave(dto);
  }

  @RequestMapping(value = "/{arrayId}", method = RequestMethod.PUT)
  public @ResponseBody ArrayDto update(@PathVariable(name = "arrayId", required = true) long arrayId, @RequestBody ArrayDto dto)
      throws IOException {
    if (dto.getId().longValue() != arrayId) {
      throw new RestException("Array ID mismatch", Status.BAD_REQUEST);
    }
    Array existing = arrayService.get(arrayId);
    if (existing == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    }
    return doSave(dto);
  }

  public ArrayDto doSave(ArrayDto dto) throws IOException {
    Array array = Dtos.to(dto);
    long savedId = arrayService.save(array);
    Array saved = arrayService.get(savedId);
    return Dtos.asDto(saved);
  }

  @RequestMapping(value = "/{arrayId}/positions/{position}", method = RequestMethod.DELETE)
  public @ResponseBody ArrayDto removeSample(@PathVariable(name = "arrayId", required = true) long arrayId,
      @PathVariable(name = "position", required = true) String position) throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    } else if (!array.isPositionValid(position)) {
      throw new RestException("Invalid array position", Status.BAD_REQUEST);
    }
    if (array.getSample(position) == null) {
      // already empty - do nothing
      return Dtos.asDto(array);
    }

    array.setSample(position, null);
    arrayService.save(array);

    Array saved = arrayService.get(arrayId);
    return Dtos.asDto(saved);
  }

  @RequestMapping(value = "/{arrayId}/positions/{position}", method = RequestMethod.PUT)
  public @ResponseBody ArrayDto addSample(@PathVariable(name = "arrayId", required = true) long arrayId,
      @PathVariable(name = "position", required = true) String position,
      @RequestParam(name = "sampleId", required = true) long sampleId) throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    } else if (!array.isPositionValid(position)) {
      throw new RestException("Invalid array position", Status.BAD_REQUEST);
    }
    Sample sample = sampleService.get(sampleId);
    if (sample == null) {
      throw new RestException("Sample not found", Status.BAD_REQUEST);
    }

    array.setSample(position, sample);
    arrayService.save(array);

    Array saved = arrayService.get(arrayId);
    return Dtos.asDto(saved);
  }

  @RequestMapping(value = "/sample-search", method = RequestMethod.GET)
  public @ResponseBody List<SampleDto> findSamples(@RequestParam(name = "q", required = true) String search) throws IOException {
    if (LimsUtils.isStringEmptyOrNull(search)) {
      return new ArrayList<>();
    }
    List<Sample> samples = arrayService.getArrayableSamplesBySearch(search);
    return Dtos.asSampleDtos(samples, false);
  }

  @RequestMapping(value = "/{arrayId}/changelog", method = RequestMethod.GET)
  public @ResponseBody List<ChangeLogDto> getChangelog(@PathVariable(name = "arrayId", required = true) long arrayId) throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    }
    return array.getChangeLog().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

}
