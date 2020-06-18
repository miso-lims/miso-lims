package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/sops")
public class SopRestController {

  @Autowired
  private SopService sopService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;

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
  public @ResponseBody List<SopDto> bulkCreate(@RequestBody List<SopDto> dtos) throws IOException {
    return RestUtils.bulkCreate("SOP", dtos, Dtos::to, sopService, Dtos::asDto);
  }

  @PutMapping("/bulk")
  public @ResponseBody List<SopDto> bulkUpdate(@RequestBody List<SopDto> dtos) throws IOException {
    return RestUtils.bulkUpdate("SOP", dtos, Dtos::to, sopService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public @ResponseBody void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("SOP", ids, sopService);
  }

}
