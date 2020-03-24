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

import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunPurposeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/runpurposes")
public class RunPurposeRestController extends RestController {

  @Autowired
  private RunPurposeService runPurposeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody RunPurposeDto create(@RequestBody RunPurposeDto dto) throws IOException {
    return RestUtils.createObject("Run Purpose", dto, Dtos::to, runPurposeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{purposeId}")
  public @ResponseBody RunPurposeDto update(@PathVariable long purposeId, @RequestBody RunPurposeDto dto) throws IOException {
    return RestUtils.updateObject("Run Purpose", purposeId, dto, Dtos::to, runPurposeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Run Purpose", ids, runPurposeService);
  }

}
