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

import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/libraryindexfamilies")
public class LibraryIndexFamilyRestController extends AbstractRestController {

  @Autowired
  private LibraryIndexFamilyService indexFamilyService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibraryIndexFamilyDto create(@RequestBody LibraryIndexFamilyDto dto) throws IOException {
    return RestUtils.createObject("Library Index Family", dto, Dtos::to, indexFamilyService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{familyId}")
  public @ResponseBody LibraryIndexFamilyDto update(@PathVariable long familyId, @RequestBody LibraryIndexFamilyDto dto)
      throws IOException {
    return RestUtils.updateObject("Library Index Family", familyId, dto, Dtos::to, indexFamilyService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Index Family", ids, indexFamilyService);
    constantsController.refreshConstants();
  }

}
