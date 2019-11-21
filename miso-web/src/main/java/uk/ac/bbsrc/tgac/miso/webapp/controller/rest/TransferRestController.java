package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ListTransferViewService;
import uk.ac.bbsrc.tgac.miso.core.service.TransferService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.TransferType;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ListTransferViewDto;
import uk.ac.bbsrc.tgac.miso.dto.TransferDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/transfers")
public class TransferRestController extends RestController {

  @Autowired
  private TransferService transferService;
  @Autowired
  private ListTransferViewService listTransferViewService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<ListTransferView, ListTransferViewDto> jQueryBackend = new JQueryDataTableBackend<ListTransferView, ListTransferViewDto>() {

    @Override
    protected ListTransferViewDto asDto(ListTransferView model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<ListTransferView> getSource() throws IOException {
      return listTransferViewService;
    }

  };

  @GetMapping("/dt/pending")
  public @ResponseBody DataTablesResponseDto<ListTransferViewDto> listPendingDatatable(HttpServletRequest request) throws IOException {
    Set<Group> groups = authorizationManager.getCurrentUser().getGroups();
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.pending(), PaginationFilter.recipientGroups(groups));
  }

  @GetMapping("/dt/{transferType}")
  public @ResponseBody DataTablesResponseDto<ListTransferViewDto> listAllDatatable(@PathVariable String transferType,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser,
        PaginationFilter.transferType(TransferType.valueOf(transferType.toUpperCase())));
  }

  @PostMapping
  public @ResponseBody TransferDto create(@RequestBody TransferDto dto) throws IOException {
    return RestUtils.createObject("Transfer", dto, Dtos::to, transferService, Dtos::asDto);
  }

  @PutMapping("/{id}")
  public @ResponseBody TransferDto update(@RequestBody TransferDto dto, @PathVariable long id) throws IOException {
    return RestUtils.updateObject("Transfer", id, dto, Dtos::to, transferService, Dtos::asDto);
  }

}
