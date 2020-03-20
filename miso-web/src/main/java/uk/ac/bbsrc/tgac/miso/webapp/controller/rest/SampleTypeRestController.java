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

import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/sampletypes")
public class SampleTypeRestController extends RestController {

  @Autowired
  private SampleTypeService sampleTypeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody SampleTypeDto create(@RequestBody SampleTypeDto dto) throws IOException {
    return RestUtils.createObject("Sample Type", dto, Dtos::to, sampleTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody SampleTypeDto update(@PathVariable long typeId, @RequestBody SampleTypeDto dto) throws IOException {
    return RestUtils.updateObject("Sample Type", typeId, dto, Dtos::to, sampleTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Sample type", ids, sampleTypeService);
  }

}
