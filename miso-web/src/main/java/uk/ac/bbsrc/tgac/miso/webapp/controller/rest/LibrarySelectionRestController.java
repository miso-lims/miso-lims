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

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySelectionTypeDto;
import uk.ac.bbsrc.tgac.miso.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/libraryselections")
public class LibrarySelectionRestController extends RestController {

  @Autowired
  private LibrarySelectionService librarySelectionService;

  @Autowired
  private MenuController menuController;

  @PostMapping
  public @ResponseBody LibrarySelectionTypeDto create(@RequestBody LibrarySelectionTypeDto dto) throws IOException {
    return RestUtils.createObject("Library Selection Type", dto, Dtos::to, librarySelectionService, d -> {
      menuController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{stainId}")
  public @ResponseBody LibrarySelectionTypeDto update(@PathVariable long stainId, @RequestBody LibrarySelectionTypeDto dto)
      throws IOException {
    return RestUtils.updateObject("Library Selection Type", stainId, dto, Dtos::to, librarySelectionService, d -> {
      menuController.refreshConstants();
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
