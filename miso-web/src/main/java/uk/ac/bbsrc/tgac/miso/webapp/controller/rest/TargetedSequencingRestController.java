package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TargetedSequencingDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/targetedsequencings")
public class TargetedSequencingRestController extends RestController {

  @Autowired
  private TargetedSequencingService targetedSequencingService;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  @Autowired
  private ConstantsController constantsController;

  private final JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto> jQueryBackend = new JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto>() {
    @Override
    protected TargetedSequencingDto asDto(TargetedSequencing model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<TargetedSequencing> getSource() throws IOException {
      return targetedSequencingService;
    }
  };

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public TargetedSequencingDto getTargetedSequencing(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    TargetedSequencing targetedSequencing = targetedSequencingService.get(id);
    if (targetedSequencing == null) {
      throw new RestException("No targeted sequencing found with id: " + id, Status.NOT_FOUND);
    } else {
      return Dtos.asDto(targetedSequencing);
    }
  }

  @GetMapping(value = "/", produces = { "application/json" })
  @ResponseBody
  public Set<TargetedSequencingDto> getTargetedSequencings(HttpServletResponse response) throws IOException {
    return Dtos.asTargetedSequencingDtos(targetedSequencingService.list());
  }

  @GetMapping(value = "/dt/kit/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<TargetedSequencingDto> availableTargetedSequencings(@PathVariable("id") Long kitDescriptorId,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, new PaginationFilter[0]);
  }

  @PostMapping
  public @ResponseBody TargetedSequencingDto create(@RequestBody TargetedSequencingDto dto) throws IOException {
    return RestUtils.createObject("Targeted Sequencing", dto, Dtos::to, targetedSequencingService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{targetedSequencingId}")
  public @ResponseBody TargetedSequencingDto update(@PathVariable long targetedSequencingId, @RequestBody TargetedSequencingDto dto)
      throws IOException {
    return RestUtils.updateObject("Targeted Sequencing", targetedSequencingId, dto, Dtos::to, targetedSequencingService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Targeted Sequencing", ids, targetedSequencingService);
  }

}
