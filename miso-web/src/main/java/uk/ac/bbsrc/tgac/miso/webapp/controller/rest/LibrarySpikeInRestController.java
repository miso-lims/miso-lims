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

import uk.ac.bbsrc.tgac.miso.core.service.LibrarySpikeInService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySpikeInDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/libraryspikeins")
public class LibrarySpikeInRestController extends RestController {

  @Autowired
  private LibrarySpikeInService librarySpikeInService;

  @Autowired
  private ConstantsController constantsController;

  @PostMapping
  public @ResponseBody LibrarySpikeInDto create(@RequestBody LibrarySpikeInDto dto) throws IOException {
    return RestUtils.createObject("Library Spike-In", dto, Dtos::to, librarySpikeInService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PutMapping("/{spikeInId}")
  public @ResponseBody LibrarySpikeInDto update(@PathVariable long spikeInId, @RequestBody LibrarySpikeInDto dto) throws IOException {
    return RestUtils.updateObject("Library Spike-In", spikeInId, dto, Dtos::to, librarySpikeInService, d -> {
      constantsController.refreshConstants();
      return Dtos.asDto(d);
    });
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Library Spike-In", ids, librarySpikeInService);
  }

}
