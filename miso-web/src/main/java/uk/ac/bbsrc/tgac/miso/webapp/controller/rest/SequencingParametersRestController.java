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

import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/sequencingparameters")
public class SequencingParametersRestController extends RestController {
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody SequencingParametersDto create(@RequestBody SequencingParametersDto dto) throws IOException {
    return RestUtils.createObject("Sequencing Parameters", dto, Dtos::to, sequencingParametersService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{id}")
  public @ResponseBody SequencingParametersDto update(@PathVariable("id") Long id, @RequestBody SequencingParametersDto dto)
      throws IOException {
    return RestUtils.updateObject("Sequencing Parameters", id, dto, Dtos::to, sequencingParametersService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Sequencing Parameters", ids, sequencingParametersService);
  }

}
