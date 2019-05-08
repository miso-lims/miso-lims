package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest/labs")
public class LabRestController extends RestController {

  @Autowired
  private LabService labService;

  @Autowired
  private MenuController menuController;

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public LabDto getLab(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = labService.get(id);
    if (lab == null) {
      throw new RestException("No lab found with ID: " + id, Status.NOT_FOUND);
    } else {
      LabDto dto = Dtos.asDto(lab);
      return dto;
    }
  }

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public Set<LabDto> getLabs(UriComponentsBuilder uriBuilder) throws IOException {
    Set<Lab> labs = labService.getAll();
    Set<LabDto> labDtos = Dtos.asLabDtos(labs);
    return labDtos;
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto createLab(@RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    Long id = labService.create(lab, labDto.getInstituteId());
    menuController.refreshConstants();
    return getLab(id, uriBuilder);
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto updateLab(@PathVariable("id") Long id, @RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    lab.setId(id);
    labService.update(lab, labDto.getInstituteId());
    menuController.refreshConstants();
    return getLab(id, uriBuilder);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteLab(@PathVariable(name = "id", required = true) long id) throws IOException {
    Lab lab = labService.get(id);
    if (lab == null) {
      throw new RestException("Lab " + id + " not found", Status.NOT_FOUND);
    }
    labService.delete(lab);
    menuController.refreshConstants();
  }

}
