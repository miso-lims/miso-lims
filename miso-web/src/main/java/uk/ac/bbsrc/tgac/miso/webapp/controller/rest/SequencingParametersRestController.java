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

import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/sequencingparameters")
public class SequencingParametersRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Sequencing Parameters";

  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private AsyncOperationManager asyncOperationManager;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkCreateAsync(@RequestBody List<SequencingParametersDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, sequencingParametersService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<SequencingParametersDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, sequencingParametersService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, SequencingParameters.class, sequencingParametersService,
        Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, sequencingParametersService);
    constantsController.refreshConstants();
  }

}
