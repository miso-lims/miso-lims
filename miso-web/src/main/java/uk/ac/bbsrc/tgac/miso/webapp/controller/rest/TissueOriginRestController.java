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

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueOriginDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/tissueorigins")
public class TissueOriginRestController extends AbstractRestController {

  @Autowired
  private TissueOriginService tissueOriginService;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<TissueOriginDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate("Tissue Origin", dtos, Dtos::to, tissueOriginService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<TissueOriginDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate("Tissue Origin", dtos, Dtos::to, tissueOriginService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, TissueOrigin.class, tissueOriginService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Tissue Origin", ids, tissueOriginService);
    constantsController.refreshConstants();
  }

}
