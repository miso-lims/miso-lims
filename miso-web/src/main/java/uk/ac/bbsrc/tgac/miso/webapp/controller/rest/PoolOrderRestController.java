package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

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

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;

@Controller
@RequestMapping("/rest/poolorders")
public class PoolOrderRestController extends RestController {

  private final JQueryDataTableBackend<PoolOrder, PoolOrderDto> jQueryBackend = new JQueryDataTableBackend<PoolOrder, PoolOrderDto>() {

    @Override
    protected PaginatedDataSource<PoolOrder> getSource() throws IOException {
      return poolOrderService;
    }

    @Override
    protected PoolOrderDto asDto(PoolOrder model) {
      return Dtos.asDto(model);
    }
  };

  @Autowired
  private PoolOrderService poolOrderService;

  @PostMapping
  public @ResponseBody PoolOrderDto create(@RequestBody PoolOrderDto dto) throws IOException {
    return RestUtils.createObject("Pool Order", dto, Dtos::to, poolOrderService, Dtos::asDto);
  }

  @PutMapping("/{orderId}")
  public @ResponseBody PoolOrderDto update(@PathVariable long orderId, @RequestBody PoolOrderDto dto) throws IOException {
    return RestUtils.updateObject("Pool Order", orderId, dto, Dtos::to, poolOrderService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Pool Order", ids, poolOrderService);
  }

  @GetMapping("/dt/{status}")
  public @ResponseBody DataTablesResponseDto<PoolOrderDto> list(@PathVariable String status, UriComponentsBuilder uriBuilder,
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    switch (PoolOrder.Status.get(status)) {
    case OUTSTANDING:
      return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.draft(false), PaginationFilter.fulfilled(false));
    case FULFILLED:
      return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.fulfilled(true));
    case DRAFT:
      return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.draft(true), PaginationFilter.fulfilled(false));
    default:
      throw new RestException("Unknown pool order status: " + status, Status.BAD_REQUEST);
    }
  }

}
