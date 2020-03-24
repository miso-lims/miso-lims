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

import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.dto.BoxUseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/boxuses")
public class BoxUseRestController extends RestController {

  @Autowired
  private BoxUseService boxUseService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody BoxUseDto create(@RequestBody BoxUseDto dto) throws IOException {
    return RestUtils.createObject("Box Use", dto, Dtos::to, boxUseService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{useId}")
  public @ResponseBody BoxUseDto update(@PathVariable long useId, @RequestBody BoxUseDto dto) throws IOException {
    return RestUtils.updateObject("Box Use", useId, dto, Dtos::to, boxUseService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Box Use", ids, boxUseService);
  }

}
