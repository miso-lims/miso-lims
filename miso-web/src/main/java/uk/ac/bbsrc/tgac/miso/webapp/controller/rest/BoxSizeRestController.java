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

import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.dto.BoxSizeDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/boxsizes")
public class BoxSizeRestController extends RestController {

  @Autowired
  private BoxSizeService boxSizeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody BoxSizeDto create(@RequestBody BoxSizeDto dto) throws IOException {
    return RestUtils.createObject("Box Size", dto, Dtos::to, boxSizeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{sizeId}")
  public @ResponseBody BoxSizeDto update(@PathVariable long sizeId, @RequestBody BoxSizeDto dto) throws IOException {
    return RestUtils.updateObject("Box Size", sizeId, dto, Dtos::to, boxSizeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Box Size", ids, boxSizeService);
  }

}
