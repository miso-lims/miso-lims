package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/poolorders")
public class PoolOrderRestController extends RestController {

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  public static class IndexResponseDto {
    private Set<String> duplicateIndices;
    private Set<String> nearDuplicateIndices;

    public Set<String> getDuplicateIndices() {
      return duplicateIndices;
    }

    public void setDuplicateIndices(Set<String> duplicateIndices) {
      this.duplicateIndices = duplicateIndices;
    }

    public Set<String> getNearDuplicateIndices() {
      return nearDuplicateIndices;
    }

    public void setNearDuplicateIndices(Set<String> nearDuplicateIndices) {
      this.nearDuplicateIndices = nearDuplicateIndices;
    }
  }

  private final JQueryDataTableBackend<PoolOrder, PoolOrderDto> jQueryBackend = new JQueryDataTableBackend<>() {

    @Override
    protected PaginatedDataSource<PoolOrder> getSource() throws IOException {
      return poolOrderService;
    }

    @Override
    protected PoolOrderDto asDto(PoolOrder model) {
      return Dtos.asDto(model, indexChecker);
    }
  };
  @Autowired
  private IndexChecker indexChecker;

  @Autowired
  private PoolOrderService poolOrderService;

  @Autowired
  private LibraryAliquotService libraryAliquotService;

  @PostMapping
  public @ResponseBody PoolOrderDto create(@RequestBody PoolOrderDto dto) throws IOException {
    return RestUtils.createObject("Pool Order", dto, Dtos::to, poolOrderService, Dtos::asDto);
  }

  @PutMapping("/{orderId}")
  public @ResponseBody PoolOrderDto update(@PathVariable long orderId, @RequestBody PoolOrderDto dto)
      throws IOException {
    return RestUtils.updateObject("Pool Order", orderId, dto, Dtos::to, poolOrderService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Pool Order", ids, poolOrderService);
  }

  @PostMapping(value = "/indexchecker")
  @ResponseBody
  public IndexResponseDto indexChecker(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<LibraryAliquot> aliquots =
        ids.stream().map(WhineyFunction.rethrow(libraryAliquotService::get)).collect(Collectors.toList());
    IndexResponseDto response = new IndexResponseDto();
    List<Library> libraries = aliquots.stream().map(LibraryAliquot::getLibrary).collect(Collectors.toList());
    response.setDuplicateIndices(indexChecker.getDuplicateIndicesSequences(libraries));
    response.setNearDuplicateIndices(indexChecker.getNearDuplicateIndicesSequences(libraries));
    return response;
  }

  @GetMapping("/dt/{status}")
  public @ResponseBody DataTablesResponseDto<PoolOrderDto> list(@PathVariable String status, HttpServletRequest request)
      throws IOException {
    switch (PoolOrder.Status.get(status)) {
      case OUTSTANDING:
        return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.draft(false),
            PaginationFilter.fulfilled(false));
      case FULFILLED:
        return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.fulfilled(true));
      case DRAFT:
        return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.draft(true),
            PaginationFilter.fulfilled(false));
      default:
        throw new RestException("Unknown pool order status: " + status, Status.BAD_REQUEST);
    }
  }

}
