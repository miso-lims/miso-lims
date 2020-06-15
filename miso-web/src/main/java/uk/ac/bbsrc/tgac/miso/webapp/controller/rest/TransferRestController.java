package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ListTransferViewService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
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
  private BoxService boxService;
  @Autowired
  private StorageLocationService storageLocationService;
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
    TransferDto updated = RestUtils.updateObject("Transfer", id, dto, Dtos::to, transferService, Dtos::asDto);

    Map<Long, Long> boxMoves = new HashMap<>();
    dto.getItems().forEach(item -> {
      if (item.getNewBoxLocationId() != null) {
        if (boxMoves.containsKey(item.getBoxId()) && !item.getNewBoxLocationId().equals(boxMoves.get(item.getBoxId()))) {
          throw new RestException("Multiple locations specified for the same box", Status.BAD_REQUEST);
        }
        boxMoves.put(item.getBoxId(), item.getNewBoxLocationId());
      }
    });
    for (Entry<Long, Long> entry : boxMoves.entrySet()) {
      Box box = boxService.get(entry.getKey());
      if (box == null) {
        throw new RestException("No box found with ID " + entry.getKey(), Status.BAD_REQUEST);
      }
      StorageLocation location = storageLocationService.get(entry.getValue());
      if (location == null) {
        throw new RestException("No location found with ID " + entry.getKey(), Status.BAD_REQUEST);
      }
      box.setStorageLocation(location);
      boxService.update(box);
    }

    return updated;
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Transfer", ids, transferService);
  }

}
