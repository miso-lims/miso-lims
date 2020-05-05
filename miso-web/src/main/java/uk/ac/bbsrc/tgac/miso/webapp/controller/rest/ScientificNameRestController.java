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

import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ScientificNameDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/scientificnames")
public class ScientificNameRestController {

  @Autowired
  private ScientificNameService scientificNameService;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody ScientificNameDto create(@RequestBody ScientificNameDto dto) throws IOException {
    ScientificNameDto saved = RestUtils.createObject("Scientific Name", dto, Dtos::to, scientificNameService, Dtos::asDto);
    constantsController.refreshConstants();
    return saved;
  }

  @PutMapping("/{id}")
  public @ResponseBody ScientificNameDto update(@PathVariable long id, @RequestBody ScientificNameDto dto) throws IOException {
    ScientificNameDto saved = RestUtils.updateObject("Scientific Name", id, dto, Dtos::to, scientificNameService, Dtos::asDto);
    constantsController.refreshConstants();
    return saved;
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Scientific Name", ids, scientificNameService);
  }

}
