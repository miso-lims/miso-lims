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
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/sops")
public class SopRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "SOP";

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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody SopDto create(@RequestBody SopDto dto) throws IOException {
    return RestUtils.createObject(TYPE_LABEL, dto, Dtos::to, sopService, Dtos::asDto);
  }

  @PutMapping("/{id}")
  public @ResponseBody SopDto update(@RequestBody SopDto dto, @PathVariable long id) throws IOException {
    return RestUtils.updateObject(TYPE_LABEL, id, dto, Dtos::to, sopService, Dtos::asDto);
  }

  @GetMapping(value = "/dt/category/{category}")
  public @ResponseBody DataTablesResponseDto<SopDto> dataTableByCategory(@PathVariable("category") String categoryName,
      HttpServletRequest request) throws IOException {

    SopCategory category;
    try {
      category = SopCategory.valueOf(categoryName);
    } catch (IllegalArgumentException e) {
      throw new RestException("Invalid SOP category", Status.BAD_REQUEST);
    }

    return datatable.get(request, advancedSearchParser, PaginationFilter.category(category));
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Sop.class, sopService, Dtos::asDto);
  }

  @PostMapping("/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, sopService);
  }

}
