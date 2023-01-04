package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StudyTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AsyncOperationManager;

@Controller
@RequestMapping("/rest/studytypes")
public class StudyTypeRestController extends RestController {

  private static final String TYPE_LABEL = "Study Type";

  @Autowired
  private StudyTypeService studyTypeService;
  @Autowired
  private ConstantsController constantsController;
  @Autowired
  private AsyncOperationManager asyncOperationManager;

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody
  ObjectNode bulkCreateAsync(@RequestBody List<StudyTypeDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkCreate(TYPE_LABEL, dtos, Dtos::to, studyTypeService, true);
  }

  @PutMapping("/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public @ResponseBody ObjectNode bulkUpdateAsync(@RequestBody List<StudyTypeDto> dtos) throws IOException {
    return asyncOperationManager.startAsyncBulkUpdate(TYPE_LABEL, dtos, Dtos::to, studyTypeService, true);
  }

  @GetMapping("/bulk/{uuid}")
  public @ResponseBody ObjectNode getProgress(@PathVariable String uuid) throws Exception {
    return asyncOperationManager.getAsyncProgress(uuid, StudyType.class, studyTypeService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, studyTypeService);
  }

}
