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

import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySelectionTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/libraryselections")
public class LibrarySelectionRestController extends RestController {

  @Autowired
  private LibrarySelectionService librarySelectionService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibrarySelectionTypeDto create(@RequestBody LibrarySelectionTypeDto dto) throws IOException {
    return RestUtils.createObject("Library Selection Type", dto, Dtos::to, librarySelectionService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody LibrarySelectionTypeDto update(@PathVariable long typeId, @RequestBody LibrarySelectionTypeDto dto)
      throws IOException {
    return RestUtils.updateObject("Library Selection Type", typeId, dto, Dtos::to, librarySelectionService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Selection Type", ids, librarySelectionService);
  }

}
