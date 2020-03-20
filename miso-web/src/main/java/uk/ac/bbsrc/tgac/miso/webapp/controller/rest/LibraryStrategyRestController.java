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

import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryStrategyTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/librarystrategies")
public class LibraryStrategyRestController extends RestController {

  @Autowired
  private LibraryStrategyService libraryStrategyService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibraryStrategyTypeDto create(@RequestBody LibraryStrategyTypeDto dto) throws IOException {
    return RestUtils.createObject("Library Strategy Type", dto, Dtos::to, libraryStrategyService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody LibraryStrategyTypeDto update(@PathVariable long typeId, @RequestBody LibraryStrategyTypeDto dto)
      throws IOException {
    return RestUtils.updateObject("Library Strategy Type", typeId, dto, Dtos::to, libraryStrategyService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Strategy Type", ids, libraryStrategyService);
  }

}
