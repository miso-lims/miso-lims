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
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/labs")
public class LabRestController extends RestController {

  @Autowired
  private LabService labService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto createLab(@RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    LabDto saved = RestUtils.createObject("Lab", labDto, Dtos::to, labService, Dtos::asDto);
    constantsController.refreshConstants();
    return saved;
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto updateLab(@PathVariable("id") Long id, @RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    LabDto updated = RestUtils.updateObject("Lab", id, labDto, Dtos::to, labService, Dtos::asDto);
    constantsController.refreshConstants();
    return updated;
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteLab(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Lab", ids, labService);
    constantsController.refreshConstants();
  }

}
