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

import jakarta.servlet.http.HttpServletRequest;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentModelDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/instrumentmodels")
public class InstrumentModelRestController extends RestController {
  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<InstrumentModel, InstrumentModelDto> jQueryBackend =
      new JQueryDataTableBackend<InstrumentModel, InstrumentModelDto>() {

        @Override
        protected InstrumentModelDto asDto(InstrumentModel model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<InstrumentModel> getSource() throws IOException {
          return instrumentModelService;
        }
      };

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<InstrumentModelDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @PostMapping
  public @ResponseBody InstrumentModelDto create(@RequestBody InstrumentModelDto dto) throws IOException {
    return RestUtils.createObject("Instrument Models", dto, Dtos::to, instrumentModelService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{id}")
  public @ResponseBody InstrumentModelDto update(@PathVariable("id") Long id, @RequestBody InstrumentModelDto dto)
      throws IOException {
    return RestUtils.updateObject("Instrument Models", id, dto, Dtos::to, instrumentModelService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Instrument Models", ids, instrumentModelService);
  }

}
