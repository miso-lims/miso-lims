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

import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/librarydesigns")
public class LibraryDesignRestController extends RestController {

  @Autowired
  private LibraryDesignService designService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibraryDesignDto create(@RequestBody LibraryDesignDto dto) throws IOException {
    return RestUtils.createObject("Library Design", dto, Dtos::to, designService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{designId}")
  public @ResponseBody LibraryDesignDto update(@PathVariable long designId, @RequestBody LibraryDesignDto dto) throws IOException {
    return RestUtils.updateObject("Library Design", designId, dto, Dtos::to, designService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Design", ids, designService);
  }

}
