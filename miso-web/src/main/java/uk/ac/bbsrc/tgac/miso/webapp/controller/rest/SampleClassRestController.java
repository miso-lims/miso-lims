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

import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/sampleclasses")
public class SampleClassRestController extends AbstractRestController {

  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private ConstantsController constantsController;

  @PostMapping(headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SampleClassDto createSampleClass(@RequestBody SampleClassDto sampleClassDto) throws IOException {
    return RestUtils.createObject("Sample Class", sampleClassDto, Dtos::to, sampleClassService, sampleClass -> {
      SampleClassDto dto = Dtos.asDto(sampleClass);
      constantsController.refreshConstants();
      return dto;
    });
  }

  @PutMapping(value = "/{id}", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public SampleClassDto updateSampleClass(@PathVariable("id") long id, @RequestBody SampleClassDto sampleClassDto)
      throws IOException {
    return RestUtils.updateObject("Sample Class", id, sampleClassDto, Dtos::to, sampleClassService, sampleClass -> {
      SampleClassDto dto = Dtos.asDto(sampleClass);
      constantsController.refreshConstants();
      return dto;
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Sample Class", ids, sampleClassService);
    constantsController.refreshConstants();
  }

}
