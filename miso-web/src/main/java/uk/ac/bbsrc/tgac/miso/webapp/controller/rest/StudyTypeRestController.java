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

import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StudyTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/studytypes")
public class StudyTypeRestController extends RestController {

  @Autowired
  private StudyTypeService studyTypeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody StudyTypeDto create(@RequestBody StudyTypeDto dto) throws IOException {
    return RestUtils.createObject("Study Type", dto, Dtos::to, studyTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody StudyTypeDto update(@PathVariable long typeId, @RequestBody StudyTypeDto dto) throws IOException {
    return RestUtils.updateObject("Study Type", typeId, dto, Dtos::to, studyTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Study Type", ids, studyTypeService);
  }

}
