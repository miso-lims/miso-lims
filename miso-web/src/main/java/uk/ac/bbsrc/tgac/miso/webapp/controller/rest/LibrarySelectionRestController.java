package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySelectionTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/libraryselections")
public class LibrarySelectionRestController extends RestController {

  private static final String TYPE_LABEL = "Library Selection Type";

  @Autowired
  private LibrarySelectionService librarySelectionService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody
  ObjectNode bulkCreateAsync(@RequestBody List<LibrarySelectionTypeDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, librarySelectionService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<LibrarySelectionTypeDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, librarySelectionService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, LibrarySelectionType.class, librarySelectionService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, librarySelectionService);
    constantsController.refreshConstants();
  }

}
