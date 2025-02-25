package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

@Controller
@RequestMapping("/rest/assays")
public class AssayRestController extends AbstractRestController {

  private static final String TYPE_LABEL = "Assay";

  @Autowired
  private AssayService assayService;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody AssayDto create(@RequestBody AssayDto dto) throws IOException {
    AssayDto created = RestUtils.createObject(TYPE_LABEL, dto, AssayDto::to, assayService, AssayDto::from);
    constantsController.refreshConstants();
    return created;
  }

  @PutMapping("/{id}")
  public @ResponseBody AssayDto update(@RequestBody AssayDto dto, @PathVariable long id) throws IOException {
    AssayDto updated = RestUtils.updateObject(TYPE_LABEL, id, dto, AssayDto::to, assayService, AssayDto::from);
    constantsController.refreshConstants();
    return updated;
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(TYPE_LABEL, ids, assayService);
    constantsController.refreshConstants();
  }

}
