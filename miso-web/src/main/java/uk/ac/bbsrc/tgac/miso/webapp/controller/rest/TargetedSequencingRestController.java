package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TargetedSequencingDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/targetedsequencings")
public class TargetedSequencingRestController extends RestController {

  private static final String TYPE_LABEL = "Targeted Sequencing";

  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private AdvancedSearchParser advancedSearchParser;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  private final JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto> jQueryBackend = new JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto>() {
    @Override
    protected TargetedSequencingDto asDto(TargetedSequencing model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<TargetedSequencing> getSource() throws IOException {
      return targetedSequencingService;
    }
  };

  @GetMapping(value = "/dt/kit/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<TargetedSequencingDto> availableTargetedSequencings(@PathVariable("id") Long kitDescriptorId,
      HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser, new PaginationFilter[0]);
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody
  ObjectNode bulkCreateAsync(@RequestBody List<TargetedSequencingDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, targetedSequencingService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<TargetedSequencingDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, targetedSequencingService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, TargetedSequencing.class, targetedSequencingService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, targetedSequencingService);
    constantsController.refreshConstants();
  }

}
