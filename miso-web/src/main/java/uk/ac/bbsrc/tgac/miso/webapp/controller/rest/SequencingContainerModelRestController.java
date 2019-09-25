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

import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingContainerModelDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/containermodels")
public class SequencingContainerModelRestController extends RestController {

  @Autowired
  private SequencingContainerModelService containerModelService;

  @Autowired
  private MenuController menuController;

  @PostMapping
  public @ResponseBody SequencingContainerModelDto create(@RequestBody SequencingContainerModelDto dto) throws IOException {
    return RestUtils.createObject("Container Model", dto, Dtos::to, containerModelService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{modelId}")
  public @ResponseBody SequencingContainerModelDto update(@PathVariable long modelId, @RequestBody SequencingContainerModelDto dto)
      throws IOException {
    return RestUtils.updateObject("Container Model", modelId, dto, Dtos::to, containerModelService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Container Model", ids, containerModelService);
  }

}
