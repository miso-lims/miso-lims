package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableCategoryService;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableCategoryDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@RestController
@RequestMapping("/rest/deliverablecategories")
public class DeliverableCategoryRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Deliverable";

  @Autowired
  private DeliverableCategoryService deliverableCategoryService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ObjectNode bulkCreateAsync(@RequestBody List<DeliverableCategoryDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, DeliverableCategoryDto::to,
        deliverableCategoryService);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ObjectNode bulkUpdateAsync(@RequestBody List<DeliverableCategoryDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, DeliverableCategoryDto::to,
        deliverableCategoryService);
  }

  @GetMapping("/bulk/{uuid}")
  public ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, DeliverableCategory.class, deliverableCategoryService,
        DeliverableCategoryDto::from);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, deliverableCategoryService);
  }

}
