package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.OrderPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse.PoolPickerEntry;

@Controller
@RequestMapping("/rest")
public class SequencingOrderRestController extends RestController {

  private final JQueryDataTableBackend<SequencingOrderCompletion, SequencingOrderCompletionDto> jQueryBackend = new JQueryDataTableBackend<SequencingOrderCompletion, SequencingOrderCompletionDto>() {

    @Override
    protected PaginatedDataSource<SequencingOrderCompletion> getSource() throws IOException {
      return sequencingOrderCompletionService;
    }

    @Override
    protected SequencingOrderCompletionDto asDto(SequencingOrderCompletion model) {
      return Dtos.asDto(model);
    }
  };

  @Autowired
  private SequencingOrderService sequencingOrderService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private SequencingOrderCompletionService sequencingOrderCompletionService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private OrderPurposeService orderPurposeService;
  public static int ERROR_EDIT_DISTANCE;
  private void setErrorEditDistance(@Value("${miso.error.edit.distance:2}") int errorEditDistance) {
    ERROR_EDIT_DISTANCE = errorEditDistance;
  }

  public static int WARNING_EDIT_DISTANCE;

  private void setWarningEditDistance(@Value("${miso.warning.edit.distance:3}") int warningEditDistance) {
    WARNING_EDIT_DISTANCE = warningEditDistance;
  }

  @GetMapping(value = "/pools/{id}/dt/completions", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getCompletionsByPool(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.pool(id));
  }

  @GetMapping(value = "/sequencingorders/{id}", produces = { "application/json" })
  @ResponseBody
  public SequencingOrderDto get(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    SequencingOrder result = sequencingOrderService.get(id);
    if (result == null) {
      throw new RestException("No sequencing order found with ID: " + id, Status.NOT_FOUND);
    } else {
      return Dtos.asDto(result, ERROR_EDIT_DISTANCE, WARNING_EDIT_DISTANCE);
    }
  }

  @PostMapping(value = "/sequencingorders", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public SequencingOrderDto create(@RequestBody SequencingOrderDto orderDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    SequencingOrder seqOrder = Dtos.to(orderDto);
    Long id = sequencingOrderService.create(seqOrder);
    SequencingOrder saved = sequencingOrderService.get(id);
    return Dtos.asDto(saved, ERROR_EDIT_DISTANCE, WARNING_EDIT_DISTANCE);
  }
  
  @GetMapping(value = "/sequencingorders/dt/completions/all/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletions(@PathVariable String platform, UriComponentsBuilder uriBuilder,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/sequencingorders/dt/completions/outstanding/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletionsOutstanding(@PathVariable String platform,
      UriComponentsBuilder uriBuilder, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.fulfilled(false),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/sequencingorders/dt/completions/in-progress/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletionsInProgress(@PathVariable String platform,
      UriComponentsBuilder uriBuilder, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.pending(),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @PutMapping(value = "/sequencingorders/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void update(@PathVariable("id") Long id, @RequestBody SequencingOrderDto dto,
      HttpServletResponse response) throws IOException {
    SequencingOrder seqOrder = sequencingOrderService.get(id);
    if (seqOrder == null) {
      throw new RestException("No sequencing order found with ID: " + id, Status.NOT_FOUND);
    }
    seqOrder.setPartitions(dto.getPartitions());
    SequencingParameters parameters = sequencingParametersService.get(dto.getParameters().getId());
    if (parameters == null) {
      throw new RestException("No sequencing parameters found with ID: " + dto.getParameters(), Status.BAD_REQUEST);
    }
    seqOrder.setSequencingParameters(parameters);
    sequencingOrderService.update(seqOrder);
  }

  @DeleteMapping(value = "/sequencingorders/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    SequencingOrder order = sequencingOrderService.get(id);
    if (order == null) {
      throw new RestException("Sequencing Order " + id + " not found", Status.NOT_FOUND);
    }
    sequencingOrderService.delete(order);
  }

  @GetMapping(value = "/sequencingorders/picker/active", produces = { "application/json" })
  @ResponseBody
  public PoolPickerResponse getPickersByUnfulfilled(@RequestParam("platform") String platform) throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.fulfilled(false));
  }

  @GetMapping(value = "/sequencingorders/picker/chemistry", produces = { "application/json" })
  @ResponseBody
  public PoolPickerResponse getPickersByChemistry(@RequestParam("platform") String platform,
      @RequestParam("seqParamsId") Long paramsId,
      @RequestParam("fulfilled") boolean fulfilled) throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.fulfilled(fulfilled),
        PaginationFilter.sequencingParameters(paramsId));
  }

  private PoolPickerResponse getPoolPickerWithFilters(Integer limit, PaginationFilter... filters) throws IOException {
    PoolPickerResponse ppr = new PoolPickerResponse();
    ppr.populate(sequencingOrderCompletionService, true, "lastUpdated", limit, SequencingOrderRestController::orderTransform,
        filters);
    return ppr;
  }

  private static PoolPickerEntry orderTransform(SequencingOrderCompletion order) {
    return new PoolPickerEntry(Dtos.asDto(order.getPool(), true, false, ERROR_EDIT_DISTANCE, WARNING_EDIT_DISTANCE),
        Collections.singletonList(Dtos.asDto(order)));
  }

  @GetMapping(value = "/sequencingorders/search")
  @ResponseBody
  public List<SequencingOrderDto> search(@RequestParam long poolId, @RequestParam long purposeId, @RequestParam long parametersId,
      @RequestParam int partitions) throws IOException {
    Pool pool = getOrThrow(poolService, poolId, "Pool");
    OrderPurpose purpose = getOrThrow(orderPurposeService, purposeId, "Order purpose");
    SequencingParameters parameters = getOrThrow(sequencingParametersService, parametersId, "Sequencing parameters");
    List<SequencingOrder> results = sequencingOrderService.listByAttributes(pool, purpose, parameters, partitions);
    return results.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  private <T extends Identifiable> T getOrThrow(ProviderService<T> service, long id, String type) throws IOException {
    T object = service.get(id);
    if (object == null) {
      throw new RestException(String.format("%s with id %d not found", type, id), Status.BAD_REQUEST);
    }
    return object;
  }

}
