package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
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
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/printer")

public class PrinterRestController extends RestController {
  private static final Pattern COMMA = Pattern.compile(",");
  private static final Logger log = LoggerFactory.getLogger(PrinterRestController.class);

  @Autowired
  private AuthorizationManager authorizationManager;

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
  private PoolService poolService;

  @Autowired
  private PrinterService printerService;

  @Autowired
  private SampleService sampleService;

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public PrinterDto create(@RequestBody PrinterDto dto) throws IOException {
    Printer printer = Dtos.to(dto);
    return get(printerService.create(printer));
  }

  @GetMapping(value = "dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PrinterDto> dataTable(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @DeleteMapping(headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@RequestBody List<Long> printerIds) throws IOException {
    for (long id : printerIds) {
      Printer printer = printerService.get(id);
      if (printer != null) {
        printerService.delete(printer);
      }
    }
  }

  @PutMapping(value = "/disable", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disable(@RequestBody List<Long> printerIds) throws IOException {
    setState(printerIds, false);
  }

  @PutMapping(value = "/enable", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void enable(@RequestBody List<Long> printerIds) throws IOException {
    setState(printerIds, true);
  }

  @GetMapping(value = "{printerId}", produces = "application/json")
  public @ResponseBody PrinterDto get(@PathVariable Long printerId) throws IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    return Dtos.asDto(printer);
  }

  @GetMapping(headers = { "Content-type=application/json" })
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

  @PostMapping(value = "{printerId}", headers = { "Content-type=application/json" })
  @ResponseBody
  public long submit(@PathVariable("printerId") Long printerId, @RequestParam("type") String type, @RequestParam("ids") String ids,
      @RequestParam("copies") int copies)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
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
    case "pool":
      fetcher = poolService::get;
      break;
    default:
      throw new IllegalArgumentException("Unknown barcodeable type: " + type);
    }

    return printer.printBarcode(user, copies, COMMA.splitAsStream(ids)//
        .map(Long::parseLong)//
        .map(WhineyFunction.rethrow(fetcher))//
        .sorted(Comparator.comparing(Barcodable::getAlias)));
  }

  public static class BoxPrintRequest {
    private List<Long> boxes;
    private int copies;

    public int getCopies() {
      return copies;
    }

    public void setCopies(int copies) {
      this.copies = copies;
    }

    public List<Long> getBoxes() {
      return boxes;
    }

    public void setBoxes(List<Long> boxes) {
      this.boxes = boxes;
    }
  }

  private Barcodable loadBarcodable(Entry<String, BoxPosition> e) throws IOException {
    long id = e.getValue().getBoxableId().getTargetId();
    switch (e.getValue().getBoxableId().getTargetType()) {
    case DILUTION:
      return dilutionService.get(id);
    case LIBRARY:
      return libraryService.get(id);
    case SAMPLE:
      return sampleService.get(id);
    case POOL:
      return poolService.get(id);
    default:
      throw new IllegalArgumentException("Unknown barcodeable type: " + e.getValue().getBoxableId().getTargetType());
    }
  }

  @PostMapping(value = "{printerId}/boxcontents", headers = { "Content-type=application/json" })
  @ResponseBody
  public long boxContents(@PathVariable("printerId") Long printerId, @RequestBody BoxPrintRequest request)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    return printer.printBarcode(user, request.getCopies(),
        request.getBoxes().stream()//
            .map(WhineyFunction.rethrow(boxService::get))//
            .sorted(Comparator.comparing(Box::getAlias))//
            .flatMap(b -> b.getBoxPositions().entrySet().stream()//
                .sorted(Comparator.comparing(Entry::getKey))//
            .map(WhineyFunction.rethrow(this::loadBarcodable))));
  }

  public static class BoxPositionPrintRequest {
    private long boxId;
    private Set<String> positions;
    private int copies;

    public int getCopies() {
      return copies;
    }

    public void setCopies(int copies) {
      this.copies = copies;
    }

    public long getBoxId() {
      return boxId;
    }

    public void setBoxId(long boxId) {
      this.boxId = boxId;
    }

    public Set<String> getPositions() {
      return positions;
    }

    public void setPositions(Set<String> positions) {
      this.positions = positions;
    }
  }

  @PostMapping(value = "{printerId}/boxpositions", headers = { "Content-type=application/json" })
  @ResponseBody
  public long boxPositions(@PathVariable("printerId") Long printerId, @RequestBody BoxPositionPrintRequest request)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    Box box = boxService.get(request.getBoxId());
    return printer.printBarcode(user, request.getCopies(),
        box.getBoxPositions().entrySet().stream()//
            .filter(e -> request.getPositions().contains(e.getKey()))//
            .sorted(Comparator.comparing(Entry::getKey))//
            .map(WhineyFunction.rethrow(this::loadBarcodable)));
  }
}
