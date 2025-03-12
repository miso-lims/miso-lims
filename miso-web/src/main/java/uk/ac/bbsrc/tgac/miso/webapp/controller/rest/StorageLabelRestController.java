package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

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

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLabelService;
import uk.ac.bbsrc.tgac.miso.dto.StorageLabelDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/storagelabels")
public class StorageLabelRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Storage Label";

  @Autowired
  private StorageLabelService storageLabelService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<StorageLabelDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, StorageLabelDto::to, storageLabelService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<StorageLabelDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, StorageLabelDto::to, storageLabelService);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, StorageLabel.class, storageLabelService, StorageLabelDto::from);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, storageLabelService);
  }

}
