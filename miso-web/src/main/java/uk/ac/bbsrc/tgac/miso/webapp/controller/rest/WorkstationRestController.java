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

import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.WorkstationDto;

@Controller
@RequestMapping("/rest/workstations")
public class WorkstationRestController extends RestController {

  @Autowired
  private WorkstationService workstationService;

  @PostMapping
  public @ResponseBody WorkstationDto create(@RequestBody WorkstationDto dto) throws IOException {
    return RestUtils.createObject("Workstation", dto, Dtos::to, workstationService, Dtos::asDto);
  }

  @PutMapping("/{typeId}")
  public @ResponseBody WorkstationDto update(@PathVariable long typeId, @RequestBody WorkstationDto dto) throws IOException {
    return RestUtils.updateObject("Workstation", typeId, dto, Dtos::to, workstationService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Workstation", ids, workstationService);
  }

}
