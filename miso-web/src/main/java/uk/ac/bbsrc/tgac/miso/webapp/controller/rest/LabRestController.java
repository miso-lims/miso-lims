package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/labs")
public class LabRestController extends RestController {

  private static final String TYPE_LABEL = "Lab";

  @Autowired
  private LabService labService;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody
  ObjectNode bulkCreateAsync(@RequestBody List<LabDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, labService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<LabDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, labService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, Lab.class, labService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteLab(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, labService);
    constantsController.refreshConstants();
  }

}
