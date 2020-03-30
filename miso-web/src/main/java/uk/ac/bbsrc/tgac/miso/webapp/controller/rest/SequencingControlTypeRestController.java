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

import uk.ac.bbsrc.tgac.miso.core.service.SequencingControlTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingControlTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/sequencingcontroltypes")
public class SequencingControlTypeRestController extends RestController {

  private static final String typeLabel = "Sequencing Control Type";

  @Autowired
  private SequencingControlTypeService sequencingControlTypeService;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody SequencingControlTypeDto create(@RequestBody SequencingControlTypeDto dto) throws IOException {
    SequencingControlTypeDto saved = RestUtils.createObject(typeLabel, dto, Dtos::to, sequencingControlTypeService,
        Dtos::asDto);
    constantsController.refreshConstants();
    return saved;
  }

  @PutMapping("/{typeId}")
  public @ResponseBody SequencingControlTypeDto update(@PathVariable long typeId, @RequestBody SequencingControlTypeDto dto)
      throws IOException {
    SequencingControlTypeDto saved = RestUtils.updateObject(typeLabel, typeId, dto, Dtos::to, sequencingControlTypeService,
        Dtos::asDto);
    constantsController.refreshConstants();
    return saved;
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete(typeLabel, ids, sequencingControlTypeService);
  }

}
