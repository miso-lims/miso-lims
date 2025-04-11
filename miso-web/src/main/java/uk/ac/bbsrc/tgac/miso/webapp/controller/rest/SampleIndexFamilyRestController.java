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

import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.dto.SampleIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/sampleindexfamilies")
public class SampleIndexFamilyRestController extends AbstractRestController {

  @Autowired
  private SampleIndexFamilyService indexFamilyService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody SampleIndexFamilyDto create(@RequestBody SampleIndexFamilyDto dto) throws IOException {
    return RestUtils.createObject("Sample Index Family", dto, SampleIndexFamilyDto::to, indexFamilyService, d -> {
      constantsController.refreshConstants();
      return SampleIndexFamilyDto.from(d);
    });
  }

  @PutMapping("/{familyId}")
  public @ResponseBody SampleIndexFamilyDto update(@PathVariable long familyId, @RequestBody SampleIndexFamilyDto dto)
      throws IOException {
    return RestUtils.updateObject("Sample Index Family", familyId, dto, SampleIndexFamilyDto::to, indexFamilyService,
        d -> {
          constantsController.refreshConstants();
          return SampleIndexFamilyDto.from(d);
        });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Sample Index Family", ids, indexFamilyService);
    constantsController.refreshConstants();
  }

}
