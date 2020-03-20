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

import uk.ac.bbsrc.tgac.miso.core.service.LibraryTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/librarytypes")
public class LibraryTypeRestController extends RestController {

  @Autowired
  private LibraryTypeService libraryTypeService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibraryTypeDto create(@RequestBody LibraryTypeDto dto) throws IOException {
    return RestUtils.createObject("Library Type", dto, Dtos::to, libraryTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{typeId}")
  public @ResponseBody LibraryTypeDto update(@PathVariable long typeId, @RequestBody LibraryTypeDto dto) throws IOException {
    return RestUtils.updateObject("Library Type", typeId, dto, Dtos::to, libraryTypeService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Type", ids, libraryTypeService);
  }

}
