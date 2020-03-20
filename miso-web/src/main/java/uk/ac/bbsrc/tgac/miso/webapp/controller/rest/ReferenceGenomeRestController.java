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

import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ReferenceGenomeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/referencegenomes")
public class ReferenceGenomeRestController extends RestController {

  @Autowired
  private ReferenceGenomeService referenceGenomeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody ReferenceGenomeDto create(@RequestBody ReferenceGenomeDto dto) throws IOException {
    return RestUtils.createObject("Reference Genome", dto, Dtos::to, referenceGenomeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{referenceId}")
  public @ResponseBody ReferenceGenomeDto update(@PathVariable long referenceId, @RequestBody ReferenceGenomeDto dto) throws IOException {
    return RestUtils.updateObject("Reference Genome", referenceId, dto, Dtos::to, referenceGenomeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Reference Genome", ids, referenceGenomeService);
  }


}
