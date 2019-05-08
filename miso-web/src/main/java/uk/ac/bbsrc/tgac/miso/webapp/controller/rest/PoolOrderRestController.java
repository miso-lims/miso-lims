package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

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

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderCompletionDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse;
import uk.ac.bbsrc.tgac.miso.webapp.util.PoolPickerResponse.PoolPickerEntry;

@Controller
@RequestMapping("/rest")
public class PoolOrderRestController extends RestController {

  private final JQueryDataTableBackend<PoolOrderCompletion, PoolOrderCompletionDto> jQueryBackend = new JQueryDataTableBackend<PoolOrderCompletion, PoolOrderCompletionDto>() {

    @Override
    protected PaginatedDataSource<PoolOrderCompletion> getSource() throws IOException {
      return poolOrderCompletionService;
    }

    @Override
    protected PoolOrderCompletionDto asDto(PoolOrderCompletion model) {
      return Dtos.asDto(model);
    }
  };

  protected static final Logger log = LoggerFactory.getLogger(PoolOrderRestController.class);

  @Autowired
  private PoolOrderService poolOrderService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  @GetMapping(value = "/pools/{id}/orders", produces = { "application/json" })
  @ResponseBody
  public Set<PoolOrderDto> getOrdersByPool(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    Set<PoolOrderDto> dtos = Dtos.asPoolOrderDtos(poolOrderService.getByPool(id));
    return dtos;
  }

  @GetMapping(value = "/pools/{id}/dt/completions", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<PoolOrderCompletionDto> getCompletionsByPool(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.pool(id));
  }

  @GetMapping(value = "/poolorders/{id}", produces = { "application/json" })
  @ResponseBody
  public PoolOrderDto getPoolOrder(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    PoolOrder result = poolOrderService.get(id);
    if (result == null) {
      throw new RestException("No pool order found with ID: " + id, Status.NOT_FOUND);
    } else {
      return Dtos.asDto(result);
    }
  }

  @PostMapping(value = "/poolorders", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(code = HttpStatus.CREATED)
  public PoolOrderDto createPoolOrder(@RequestBody PoolOrderDto poolOrderDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    PoolOrder poolOrder = Dtos.to(poolOrderDto);
    Long id = poolOrderService.create(poolOrder);
    PoolOrder saved = poolOrderService.get(id);
    return Dtos.asDto(saved);
  }
  
  @GetMapping(value = "/poolorders/dt/completions/all/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<PoolOrderCompletionDto> getDtCompletions(@PathVariable String platform, UriComponentsBuilder uriBuilder,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/poolorders/dt/completions/active/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<PoolOrderCompletionDto> getDtCompletionsUnfulfilled(@PathVariable String platform,
      UriComponentsBuilder uriBuilder, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.fulfilled(false),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @GetMapping(value = "/poolorders/dt/completions/pending/{platform}", produces = { "application/json" })
  @ResponseBody
  public DataTablesResponseDto<PoolOrderCompletionDto> getDtCompletionsPending(@PathVariable String platform,
      UriComponentsBuilder uriBuilder, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.pending(),
        PaginationFilter.platformType(PlatformType.valueOf(platform)));
  }

  @PutMapping(value = "/poolorders/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void updatePoolOrder(@PathVariable("id") Long id, @RequestBody PoolOrderDto poolOrderDto,
      HttpServletResponse response) throws IOException {
    PoolOrder poolOrder = poolOrderService.get(id);
    if (poolOrder == null) {
      throw new RestException("No pool order found with ID: " + id, Status.NOT_FOUND);
    }
    poolOrder.setPartitions(poolOrderDto.getPartitions());
    SequencingParameters parameters = sequencingParametersService.get(poolOrderDto.getParameters().getId());
    if (parameters == null) {
      throw new RestException("No sequencing parameters found with ID: " + poolOrderDto.getParameters(), Status.BAD_REQUEST);
    }
    poolOrder.setSequencingParameters(parameters);
    poolOrderService.update(poolOrder);
  }

  @DeleteMapping(value = "/poolorders/{id}")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePoolOrder(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    PoolOrder order = poolOrderService.get(id);
    if (order == null) {
      throw new RestException("Pool Order " + id + " not found", Status.NOT_FOUND);
    }
    poolOrderService.delete(order);
  }

  @GetMapping(value = "/poolorders/picker/active", produces = { "application/json" })
  @ResponseBody
  public PoolPickerResponse getPickersByUnfulfilled(@RequestParam("platform") String platform) throws IOException {
    return getPoolPickerWithFilters(100,
        PaginationFilter.platformType(PlatformType.valueOf(platform)),
        PaginationFilter.fulfilled(false));
  }

  @GetMapping(value = "/poolorders/picker/chemistry", produces = { "application/json" })
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
    ppr.populate(poolOrderCompletionService, true, "lastUpdated", limit, PoolOrderRestController::orderTransform, filters);
    return ppr;
  }

  private static PoolPickerEntry orderTransform(PoolOrderCompletion order) {
    return new PoolPickerEntry(Dtos.asDto(order.getPool(), true, false), Collections.singletonList(Dtos.asDto(order)));
  }
}
