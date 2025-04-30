package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.service.StudyService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StudyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/studies")
public class StudyRestController extends AbstractRestController {

  @Autowired
  private StudyService studyService;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<Study, StudyDto> jQueryBackend = new JQueryDataTableBackend<Study, StudyDto>() {
    @Override
    protected StudyDto asDto(Study model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Study> getSource() throws IOException {
      return studyService;
    }
  };

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<StudyDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/project/{id}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<StudyDto> dataTableByProject(@PathVariable("id") Long id, HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.project(id));
  }

  @GetMapping(value = "/{studyId}")
  public @ResponseBody StudyDto get(@PathVariable long studyId) throws IOException {
    return RestUtils.getObject("Study", studyId, studyService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Study", ids, studyService);
  }

  @GetMapping(produces = "application/json")
  public @ResponseBody List<StudyDto> list() throws IOException {
    return studyService.list(0, 0, true, "id").stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping
  public @ResponseBody StudyDto create(@RequestBody StudyDto dto) throws IOException {
    return RestUtils.createObject("Study", dto, Dtos::to, studyService, Dtos::asDto);
  }

  @PutMapping("/{studyId}")
  public @ResponseBody StudyDto update(@PathVariable long studyId, @RequestBody StudyDto dto) throws IOException {
    return RestUtils.updateObject("Study", studyId, dto, Dtos::to, studyService, Dtos::asDto);
  }
}
