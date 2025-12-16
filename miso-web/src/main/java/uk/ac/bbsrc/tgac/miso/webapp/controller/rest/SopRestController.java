package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

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

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.SopField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.dto.SopFieldDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/sops")
public class SopRestController extends AbstractRestController {

  @Autowired
  private SopService sopService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  private final JQueryDataTableBackend<Sop, SopDto> datatable = new JQueryDataTableBackend<Sop, SopDto>() {

    @Override
    protected SopDto asDto(Sop model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Sop> getSource() throws IOException {
      return sopService;
    }
  };

  // ADDED: GET single SOP with fields
  @GetMapping(value = "/{id}", produces = "application/json")
  @ResponseBody
  public SopDto getSop(@PathVariable("id") long id) throws IOException {
    Sop sop = sopService.get(id);
    if (sop == null) {
      throw new RestException("SOP not found", Status.NOT_FOUND);
    }
    return Dtos.asDto(sop);
  }

  // ADDED: POST create new SOP with fields
  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SopDto create(@RequestBody SopDto dto) throws IOException {
    Sop sop = Dtos.to(dto);
    long savedId = sopService.create(sop);
    return Dtos.asDto(sopService.get(savedId));
  }

  // ADDED: PUT update existing SOP with fields
  @PutMapping(value = "/{id}", produces = "application/json")
  @ResponseBody
  public SopDto update(@PathVariable("id") long id, @RequestBody SopDto dto) throws IOException {
    Sop existing = sopService.get(id);
    if (existing == null) {
      throw new RestException("SOP not found", Status.NOT_FOUND);
    }

    // Update basic fields
    existing.setAlias(dto.getAlias());
    existing.setVersion(dto.getVersion());
    existing.setCategory(SopCategory.valueOf(dto.getCategory()));
    existing.setUrl(dto.getUrl());
    existing.setArchived(dto.isArchived());

    // Clear and re-add fields
    existing.getSopFields().clear();
    if (dto.getFields() != null && !dto.getFields().isEmpty()) {
      for (SopFieldDto fieldDto : dto.getFields()) {
        SopField field = Dtos.to(fieldDto);
        existing.addSopField(field);
      }
    }

    sopService.update(existing);
    return Dtos.asDto(sopService.get(id));
  }

  @GetMapping(value = "/dt/category/{category}")
  public @ResponseBody DataTablesResponseDto<SopDto> dataTableByCategory(@PathVariable("category") String categoryName,
      HttpServletRequest request)
      throws IOException {
    SopCategory category = null;
    try {
      category = SopCategory.valueOf(categoryName);
    } catch (IllegalArgumentException e) {
      throw new RestException("Invalid SOP category", Status.BAD_REQUEST);
    }
    return datatable.get(request, advancedSearchParser, PaginationFilter.category(category));
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<SopDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate("SOP", dtos, Dtos::to, sopService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<SopDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate("SOP", dtos, Dtos::to, sopService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Sop.class, sopService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("SOP", ids, sopService);
  }

}
