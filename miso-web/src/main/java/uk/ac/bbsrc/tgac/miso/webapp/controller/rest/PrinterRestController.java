package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PrinterDto;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Controller
@RequestMapping("/rest/printer")

public class PrinterRestController extends RestController {
  private static final Pattern COMMA = Pattern.compile(",");
  private static final Logger log = LoggerFactory.getLogger(PrinterRestController.class);

  @Autowired
  private BoxService boxService;

  @Autowired
  private ContainerService containerService;

  @Autowired
  private LibraryDilutionService dilutionService;

  private final JQueryDataTableBackend<Printer, PrinterDto> jQueryBackend = new JQueryDataTableBackend<Printer, PrinterDto>() {

    @Override
    protected PrinterDto asDto(Printer model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<Printer> getSource() throws IOException {
      return printerService;
    }

  };

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private PrinterService printerService;

  @Autowired
  private SampleService sampleService;

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public PrinterDto create(@RequestBody PrinterDto dto) throws IOException {
    Printer printer = Dtos.to(dto);
    return get(printerService.create(printer));
  }

  @RequestMapping(value = "dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PrinterDto> dataTable(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(method = RequestMethod.DELETE, headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@RequestBody List<Long> printerIds) throws IOException {
    for (long id : printerIds) {
      Printer printer = printerService.get(id);
      if (printer != null) {
        printerService.remove(printer);
      }
    }
  }

  @RequestMapping(value = "/disable", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disable(@RequestBody List<Long> printerIds) throws IOException {
    setState(printerIds, false);
  }

  @RequestMapping(value = "/enable", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void enable(@RequestBody List<Long> printerIds) throws IOException {
    setState(printerIds, true);
  }

  @RequestMapping(value = "{printerId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody PrinterDto get(@PathVariable Long printerId) throws IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    return Dtos.asDto(printer);
  }

  @RequestMapping(method = RequestMethod.GET, headers = { "Content-type=application/json" })
  @ResponseBody
  public List<PrinterDto> list() throws IOException {
    return printerService.list(0, 0, true, "id").stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
  }

  private void setState(List<Long> printerIds, boolean state) {
    for (long id : printerIds) {
      try {
        Printer printer = printerService.get(id);
        if (printer != null) {
          printer.setEnabled(state);
          printerService.update(printer);
        }
      } catch (IOException e) {
        log.error("change printer state", e);
        throw new RestException("Cannot resolve printer with name: " + id + " : " + e.getMessage());
      }
    }
  }

  @RequestMapping(value = "{printerId}", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public long submit(@PathVariable("printerId") Long printerId, @RequestParam("type") String type, @RequestParam("ids") String ids)
      throws IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    WhineyFunction<Long, Barcodable> fetcher;

    switch (type) {
    case "box":
      fetcher = boxService::get;
      break;
    case "container":
      fetcher = containerService::get;
      break;
    case "dilution":
      fetcher = dilutionService::get;
      break;
    case "library":
      fetcher = libraryService::get;
      break;
    case "sample":
      fetcher = sampleService::get;
      break;
    default:
      throw new IllegalArgumentException("Unknown barcodeable type: " + type);
    }

    return COMMA.splitAsStream(ids)//
        .map(Long::parseLong)//
        .map(WhineyFunction.rethrow(fetcher))//
        .filter(printer::printBarcode)//
        .count();
  }
}
