package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.dto.ChangeLogDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/arrays")
public class ArrayRestController extends AbstractRestController {

  private static final String ERROR_NOTFOUND = "Array not found";

  @Autowired
  private ArrayService arrayService;

  @Autowired
  private SampleService sampleService;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

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

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ArrayDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody ArrayDto save(@RequestBody ArrayDto dto) throws IOException {
    return RestUtils.createObject("Array", dto, Dtos::to, arrayService, Dtos::asDto);
  }

  @PutMapping(value = "/{arrayId}")
  public @ResponseBody ArrayDto update(@PathVariable(name = "arrayId", required = true) long arrayId,
      @RequestBody ArrayDto dto)
      throws IOException {
    return RestUtils.updateObject("Array", arrayId, dto, WhineyFunction.rethrow(d -> {
      Array existing = arrayService.get(arrayId);
      Array array = Dtos.to(d);
      // Can't update samples in this way
      array.setSamples(existing.getSamples());
      return array;
    }), arrayService, Dtos::asDto);
  }

  @DeleteMapping(value = "/{arrayId}/positions/{position}")
  public @ResponseBody ArrayDto removeSample(@PathVariable(name = "arrayId", required = true) long arrayId,
      @PathVariable(name = "position", required = true) String position) throws IOException {
    Array array = getArrayOrThrow(arrayId);
    validatePositions(array, Collections.singletonList(position), Status.NOT_FOUND);
    if (array.getSample(position) == null) {
      // already empty - do nothing
      return Dtos.asDto(array);
    }

    array.setSample(position, null);
    arrayService.update(array);

    Array saved = arrayService.get(arrayId);
    return Dtos.asDto(saved);
  }

  @PutMapping(value = "/{arrayId}/positions/{position}")
  public @ResponseBody ArrayDto addSample(@PathVariable(name = "arrayId", required = true) long arrayId,
      @PathVariable(name = "position", required = true) String position,
      @RequestParam(name = "sampleId", required = true) long sampleId) throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    } else if (!array.isPositionValid(position)) {
      throw new RestException("Invalid array position", Status.NOT_FOUND);
    }

    Sample sample = sampleService.get(sampleId);
    if (sample == null) {
      throw new RestException("Sample not found", Status.BAD_REQUEST);
    }

    array.setSample(position, sample);
    arrayService.update(array);

    Array saved = arrayService.get(arrayId);
    return Dtos.asDto(saved);
  }

  public static class BulkUpdateRequestItem {
    private String position;
    private String searchString;

    public String getPosition() {
      return position;
    }

    public void setPosition(String position) {
      this.position = position;
    }

    public String getSearchString() {
      return searchString;
    }

    public void setSearchString(String searchString) {
      this.searchString = searchString;
    }
  }

  @PutMapping(value = "/{arrayId}/positions")
  public @ResponseBody ArrayDto bulkUpdatePositions(@PathVariable(name = "arrayId", required = true) long arrayId,
      @RequestBody(required = true) List<BulkUpdateRequestItem> items) throws IOException {
    Array array = getArrayOrThrow(arrayId);
    ValidationResult validation = new ValidationResult();

    for (BulkUpdateRequestItem item : items) {
      if (!array.isPositionValid(item.getPosition())) {
        validation.addError(new ValidationError("Invalid array position: " + item.getPosition()));
        continue;
      }
      if (LimsUtils.isStringEmptyOrNull(item.getSearchString())) {
        array.setSample(item.getPosition(), null);
        continue;
      }

      List<Sample> searchResults = arrayService.getArrayableSamplesBySearch(item.getSearchString());
      if (searchResults == null || searchResults.isEmpty()) {
        validation.addError(new ValidationError(
            "No sample found by searching '" + item.getSearchString() + "' for position " + item.getPosition()));
      } else if (searchResults.size() > 1) {
        validation.addError(new ValidationError("Multiple samples matched search '" + item.getSearchString()
            + "' for position " + item.getPosition()));
      } else {
        array.setSample(item.getPosition(), searchResults.get(0));
      }
    }

    validation.throwIfInvalid();
    arrayService.update(array);
    return Dtos.asDto(arrayService.get(arrayId));
  }

  @GetMapping(value = "/sample-search")
  public @ResponseBody List<SampleDto> findSamples(@RequestParam(name = "q", required = true) String search)
      throws IOException {
    if (LimsUtils.isStringEmptyOrNull(search)) {
      return new ArrayList<>();
    }
    List<Sample> samples = arrayService.getArrayableSamplesBySearch(search);
    return Dtos.asSampleDtos(samples, false);
  }

  @GetMapping(value = "/{arrayId}/changelog")
  public @ResponseBody List<ChangeLogDto> getChangelog(@PathVariable(name = "arrayId", required = true) long arrayId)
      throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    }
    return array.getChangeLog().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Array", ids, arrayService);
  }

  private Array getArrayOrThrow(long arrayId) throws IOException {
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new RestException(ERROR_NOTFOUND, Status.NOT_FOUND);
    }
    return array;
  }

  private void validatePositions(Array array, List<String> positions, Status invalidStatus) {
    if (positions == null || positions.isEmpty()) {
      throw new RestException("No array positions selected", Status.BAD_REQUEST);
    }
    List<String> invalidPositions = positions.stream()
        .filter(position -> !array.isPositionValid(position))
        .collect(Collectors.toList());
    if (!invalidPositions.isEmpty()) {
      throw new RestException("Invalid array position", invalidStatus);
    }
  }

}
