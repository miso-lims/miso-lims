package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;

@Controller
@RequestMapping("/rest")

public class BoxRestController extends RestController {
  @Autowired
  private BoxService boxService;

  private final JQueryDataTableBackend<Box, BoxDto> jQueryBackend = new JQueryDataTableBackend<Box, BoxDto>() {
    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Box> getSource() throws IOException {
      return boxService;
    }
  };

  @RequestMapping(value = "/box/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/box/dt/use/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTableByUse(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.boxUse(id));
  }

  @RequestMapping(value = "/boxes/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Box> jsonRest() throws IOException {
    return boxService.list(0, 0, true, "id");
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

}
