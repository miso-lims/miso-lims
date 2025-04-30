package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.PrinterService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelElement;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PrinterDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/printers")

public class PrinterRestController extends AbstractRestController {
  public static class BoxPositionPrintRequest {
    private long boxId;
    private int copies;
    private Set<String> positions;
    private String sortOrder;

    public long getBoxId() {
      return boxId;
    }

    public int getCopies() {
      return copies;
    }

    public Set<String> getPositions() {
      return positions;
    }

    public String getSortOrder() {
      return sortOrder;
    }

    public void setBoxId(long boxId) {
      this.boxId = boxId;
    }

    public void setCopies(int copies) {
      this.copies = copies;
    }

    public void setPositions(Set<String> positions) {
      this.positions = positions;
    }

    public void setSortOrder(String sortOrder) {
      this.sortOrder = sortOrder;
    }
  }

  public static class BoxPrintRequest {
    private List<Long> boxes;
    private int copies;

    public List<Long> getBoxes() {
      return boxes;
    }

    public int getCopies() {
      return copies;
    }

    public void setBoxes(List<Long> boxes) {
      this.boxes = boxes;
    }

    public void setCopies(int copies) {
      this.copies = copies;
    }
  }

  public static class DuplicateRequest {
    private double height;
    private String name;
    private double width;

    public double getHeight() {
      return height;
    }

    public String getName() {
      return name;
    }

    public double getWidth() {
      return width;
    }

    public void setHeight(double height) {
      this.height = height;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setWidth(double width) {
      this.width = width;
    }
  }
  public static class PrintRequest {
    private int copies;
    private List<Long> ids;
    private long printerId;
    private String type;

    public int getCopies() {
      return copies;
    }

    public List<Long> getIds() {
      return ids;
    }

    public long getPrinterId() {
      return printerId;
    }

    public String getType() {
      return type;
    }

    public void setCopies(int copies) {
      this.copies = copies;
    }

    public void setIds(List<Long> ids) {
      this.ids = ids;
    }

    public void setPrinterId(long printerId) {
      this.printerId = printerId;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(PrinterRestController.class);

  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private BoxService boxService;
  @Autowired
  private ContainerService containerService;

  private final JQueryDataTableBackend<Printer, PrinterDto> jQueryBackend =
      new JQueryDataTableBackend<Printer, PrinterDto>() {

        @Override
        protected PrinterDto asDto(Printer model) {
          return Dtos.asDto(model, getObjectMapper());
        }

        @Override
        protected PaginatedDataSource<Printer> getSource() throws IOException {
          return printerService;
        }

      };

  @Autowired
  private LibraryAliquotService libraryAliquotService;

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private PrinterService printerService;

  @Autowired
  private SampleService sampleService;

  @PostMapping(value = "{printerId}/boxcontents", headers = {"Content-type=application/json"})
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

  @PostMapping(value = "{printerId}/boxpositions", headers = {"Content-type=application/json"})
  @ResponseBody
  public long boxPositions(@PathVariable("printerId") Long printerId, @RequestBody BoxPositionPrintRequest request)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    Box box = boxService.get(request.getBoxId());
    Comparator<Entry<String, BoxPosition>> comparator;
    if ("column".equalsIgnoreCase(request.getSortOrder())) {
      comparator = Comparator.comparing((Entry<String, BoxPosition> e) -> {
        return BoxUtils.getColumnNumber(e.getKey());
      }).thenComparing(e -> BoxUtils.getRowNumber(e.getKey()));
    } else {
      comparator = Comparator.comparing(Entry::getKey);
    }
    return printer.printBarcode(user, request.getCopies(),
        box.getBoxPositions().entrySet().stream()
            .filter(e -> request.getPositions().contains(e.getKey()))
            .sorted(comparator)
            .map(WhineyFunction.rethrow(this::loadBarcodable)));
  }

  @PostMapping(headers = {"Content-type=application/json"})
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public PrinterDto create(@RequestBody PrinterDto dto) throws IOException {
    Printer printer = Dtos.to(dto, getObjectMapper());
    return get(printerService.create(printer));
  }

  @GetMapping(value = "dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<PrinterDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @PostMapping(value = "/bulk-delete", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@RequestBody List<Long> printerIds) throws IOException {
    RestUtils.bulkDelete("Printer", printerIds, printerService);
  }

  @PutMapping(value = "/disable", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disable(@RequestBody List<Long> printerIds) throws IOException {
    setState(printerIds, false);
  }

  @PostMapping(value = "{printerId}/duplicate", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void duplicate(@PathVariable("printerId") Long printerId, @RequestBody DuplicateRequest request)
      throws JsonParseException, JsonMappingException, IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    printer.setId(0);
    printer.setName(request.getName());
    printer.setWidth(request.getWidth());
    printer.setHeight(request.getHeight());
    printerService.create(printer);
  }

  @PutMapping(value = "/enable", headers = {"Content-type=application/json"})
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
    return Dtos.asDto(printer, getObjectMapper());
  }

  @GetMapping(value = "{printerId}/layout", headers = {"Content-type=application/json"})
  @ResponseBody
  public List<LabelElement> getLayout(@PathVariable("printerId") Long printerId)
      throws JsonParseException, JsonMappingException, IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    return printer.parseLayout();
  }

  @GetMapping(headers = {"Content-type=application/json"})
  @ResponseBody
  public List<PrinterDto> list() throws IOException {
    return printerService.list(0, 0, true, "id").stream()
        .map(printer -> Dtos.asDto(printer, getObjectMapper()))
        .collect(Collectors.toList());
  }

  private Barcodable loadBarcodable(Entry<String, BoxPosition> e) throws IOException {
    long id = e.getValue().getBoxableId().getTargetId();
    switch (e.getValue().getBoxableId().getTargetType()) {
      case LIBRARY_ALIQUOT:
        return libraryAliquotService.get(id);
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

  @PutMapping(value = "{printerId}/layout", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void setLayout(@PathVariable("printerId") Long printerId, @RequestBody List<LabelElement> layout)
      throws JsonParseException, JsonMappingException, IOException {
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    printer.changeLayout(layout);
    printerService.update(printer);
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

  @PostMapping(value = "{printerId}", headers = {"Content-type=application/json"})
  @ResponseBody
  public long submit(@PathVariable("printerId") Long printerId, @RequestBody PrintRequest request)
      throws IOException {
    User user = authorizationManager.getCurrentUser();
    Printer printer = printerService.get(printerId);
    if (printer == null) {
      throw new RestException("No printer found with ID: " + printerId, Status.NOT_FOUND);
    }
    WhineyFunction<Long, Barcodable> fetcher;

    switch (request.getType()) {
      case "box":
        fetcher = boxService::get;
        break;
      case "container":
        fetcher = containerService::get;
        break;
      case "libraryaliquot":
        fetcher = libraryAliquotService::get;
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
        throw new IllegalArgumentException("Unknown barcodeable type: " + request.getType());
    }

    return printer.printBarcode(user, request.getCopies(), request.getIds().stream()//
        .map(WhineyFunction.rethrow(fetcher))//
        .sorted(Comparator.comparing(Barcodable::getLabelText)));
  }
}
