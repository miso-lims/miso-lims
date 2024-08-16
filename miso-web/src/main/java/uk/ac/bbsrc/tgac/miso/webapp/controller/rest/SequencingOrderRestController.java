package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderSummaryViewService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingOrderDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse.PoolPickerEntry;

@Controller
@RequestMapping("/rest")
public class SequencingOrderRestController extends RestController {

  private static final String TYPE_LABEL = "Sequencing Order";

  @Autowired
  private SequencingOrderService sequencingOrderService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private SequencingOrderSummaryViewService sequencingOrderSummaryViewService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunPurposeService runPurposeService;
  @Autowired
  private SequencingContainerModelService containerModelService;
  @Autowired
  private IndexChecker indexChecker;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  private final JQueryDataTableBackend<SequencingOrderSummaryView, SequencingOrderCompletionDto> jQueryBackend =
      new JQueryDataTableBackend<>() {

        @Override
        protected PaginatedDataSource<SequencingOrderSummaryView> getSource() throws IOException {
          return sequencingOrderSummaryViewService;
        }

        @Override
        protected SequencingOrderCompletionDto asDto(SequencingOrderSummaryView model) {
          return Dtos.asDto(model, indexChecker);
        }
      };

  @GetMapping(value = "/pools/{id}/dt/completions", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getCompletionsByPool(@PathVariable("id") Long id,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.pool(id));
  }

  @GetMapping(value = "/sequencingorders/{id}", produces = {"application/json"})
  @ResponseBody
  public SequencingOrderDto get(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response)
      throws IOException {
    SequencingOrder result = sequencingOrderService.get(id);
    if (result == null) {
      throw new RestException("No sequencing order found with ID: " + id, Status.NOT_FOUND);
    } else {
      return Dtos.asDto(result, indexChecker);
    }
  }

  @PostMapping(value = "/sequencingorders", headers = {"Content-type=application/json"})
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public SequencingOrderDto create(@RequestBody SequencingOrderDto orderDto, UriComponentsBuilder b,
      HttpServletResponse response)
      throws IOException {
    SequencingOrder seqOrder = Dtos.to(orderDto);
    Long id = sequencingOrderService.create(seqOrder);
    SequencingOrder saved = sequencingOrderService.get(id);
    return Dtos.asDto(saved, indexChecker);
  }

  @GetMapping(value = "/sequencingorders/dt/completions/all/{platform}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletions(@PathVariable String platform,
      HttpServletRequest request)
      throws IOException {
    return jQueryBackend.get(request, advancedSearchParser,
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/sequencingorders/dt/completions/outstanding/{platform}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletionsOutstanding(@PathVariable String platform,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.fulfilled(false),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/sequencingorders/dt/completions/in-progress/{platform}", produces = {"application/json"})
  @ResponseBody
  public DataTablesResponseDto<SequencingOrderCompletionDto> getDtCompletionsInProgress(@PathVariable String platform,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.pending(),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @PutMapping(value = "/sequencingorders/{id}", headers = {"Content-type=application/json"})
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
  public void delete(@PathVariable(name = "id", required = true) long id, HttpServletResponse response)
      throws IOException {
    RestUtils.delete("Sequencing order", id, sequencingOrderService);
  }

  @PostMapping(value = "/sequencingorders/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Sequencing order", ids, sequencingOrderService);
  }

  @GetMapping(value = "/sequencingorders/picker/active", produces = {"application/json"})
  @ResponseBody
  public PoolPickerResponse getPickersByUnfulfilled(@RequestParam("platform") String platform) throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.fulfilled(false));
  }

  @GetMapping(value = "/sequencingorders/picker/chemistry", produces = {"application/json"})
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
    ppr.populate(sequencingOrderSummaryViewService, true, "lastUpdated", limit,
        this::orderTransform,
        filters);
    return ppr;
  }

  private PoolPickerEntry orderTransform(SequencingOrderSummaryView order) {
    PoolDto poolDto = Dtos.asDto(order.getPool(), indexChecker);
    SequencingOrderCompletionDto socDto = Dtos.asDto(order, indexChecker);
    return new PoolPickerEntry(poolDto,
        Collections.singletonList(socDto));
  }

  @GetMapping(value = "/sequencingorders/search")
  @ResponseBody
  public List<SequencingOrderDto> search(@RequestParam long poolId, @RequestParam long purposeId,
      @RequestParam(required = false) Long containerModelId, @RequestParam long parametersId,
      @RequestParam int partitions)
      throws IOException {
    Pool pool = getOrThrow(poolService, poolId, "Pool");
    RunPurpose purpose = getOrThrow(runPurposeService, purposeId, "Run purpose");
    SequencingContainerModel containerModel = containerModelId == null ? null
        : getOrThrow(containerModelService, containerModelId, "Container model");
    SequencingParameters parameters = getOrThrow(sequencingParametersService, parametersId, "Sequencing parameters");
    List<SequencingOrder> results =
        sequencingOrderService.listByAttributes(pool, purpose, containerModel, parameters, partitions);
    return results.stream().map(so -> Dtos.asDto(so, indexChecker)).collect(Collectors.toList());
  }

  private <T extends Identifiable> T getOrThrow(ProviderService<T> service, long id, String type) throws IOException {
    T object = service.get(id);
    if (object == null) {
      throw new RestException(String.format("%s with id %d not found", type, id), Status.BAD_REQUEST);
    }
    return object;
  }

  @PostMapping("/sequencingorders/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<SequencingOrderDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, sequencingOrderService);
  }

  @GetMapping("/sequencingorders/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, SequencingOrder.class, sequencingOrderService,
        order -> Dtos.asDto(order, indexChecker));
  }

}
