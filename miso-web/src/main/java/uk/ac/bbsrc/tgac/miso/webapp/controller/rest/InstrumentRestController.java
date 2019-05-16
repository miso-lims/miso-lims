package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentDto;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;

@Controller
@RequestMapping("/rest/instruments")
public class InstrumentRestController extends RestController {
  private final JQueryDataTableBackend<Instrument, InstrumentDto> jQueryBackend = new JQueryDataTableBackend<Instrument, InstrumentDto>() {
    @Override
    protected InstrumentDto asDto(Instrument model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Instrument> getSource() throws IOException {
      return instrumentService;
    }
  };

  @Autowired
  private InstrumentService instrumentService;

  public void setLibraryService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @GetMapping(value = "/{instrumentId}", produces = "application/json")
  @ResponseBody
  public InstrumentDto getById(@PathVariable Long instrumentId) throws IOException {
    return RestUtils.getObject("Instrument", instrumentId, instrumentService, Dtos::asDto);
  }

  @GetMapping(produces = "application/json")
  @ResponseBody
  public List<InstrumentDto> listAll() throws IOException {
    return instrumentService.list().stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping(produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public InstrumentDto create(@RequestBody InstrumentDto instrumentDto) throws IOException {
    return RestUtils.createObject("Instrument", instrumentDto, Dtos::to, instrumentService, Dtos::asDto);
  }

  @PutMapping("/{instrumentId}")
  public @ResponseBody InstrumentDto update(@PathVariable long instrumentId, @RequestBody InstrumentDto dto) throws IOException {
    return RestUtils.updateObject("Instrument", instrumentId, dto, Dtos::to, instrumentService, Dtos::asDto);
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<InstrumentDto> datatable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/dt/instrument-type/{type}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<InstrumentDto> datatableByInstrumentType(@PathVariable("type") String type, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    InstrumentType instrumentType = InstrumentType.valueOf(type);
    if (instrumentType == null) {
      throw new RestException("Invalid instrument type.", Status.BAD_REQUEST);
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.instrumentType(instrumentType));
  }

}
