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

import uk.ac.bbsrc.tgac.miso.core.service.ArrayModelService;
import uk.ac.bbsrc.tgac.miso.dto.ArrayModelDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/arraymodels")
public class ArrayModelRestController extends RestController {

  @Autowired
  private ArrayModelService arrayModelService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody ArrayModelDto create(@RequestBody ArrayModelDto dto) throws IOException {
    return RestUtils.createObject("Array Model", dto, Dtos::to, arrayModelService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{modelId}")
  public @ResponseBody ArrayModelDto update(@PathVariable long modelId, @RequestBody ArrayModelDto dto) throws IOException {
    return RestUtils.updateObject("Array Model", modelId, dto, Dtos::to, arrayModelService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Array Model", ids, arrayModelService);
  }

}
