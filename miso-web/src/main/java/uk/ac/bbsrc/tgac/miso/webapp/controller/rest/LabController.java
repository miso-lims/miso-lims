package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.service.LabService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("lab")
public class LabController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(LabController.class);

  @Autowired
  private LabService labService;

  private static LabDto writeUrls(LabDto labDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    labDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/lab/{id}")
        .buildAndExpand(labDto.getId()).toUriString());
    labDto.setInstituteUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/institute/{id}")
        .buildAndExpand(labDto.getInstituteId()).toUriString());
    labDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(labDto.getCreatedById()).toUriString());
    labDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(labDto.getUpdatedById()).toUriString());
    return labDto;
  }

  @GetMapping(value = "/lab/{id}", produces = { "application/json" })
  @ResponseBody
  public LabDto getLab(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = labService.get(id);
    if (lab == null) {
      throw new RestException("No lab found with ID: " + id, Status.NOT_FOUND);
    } else {
      LabDto dto = Dtos.asDto(lab);
      writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  @GetMapping(value = "/labs", produces = { "application/json" })
  @ResponseBody
  public Set<LabDto> getLabs(UriComponentsBuilder uriBuilder) throws IOException {
    Set<Lab> labs = labService.getAll();
    Set<LabDto> labDtos = Dtos.asLabDtos(labs);
    for (LabDto labDto : labDtos) {
      writeUrls(labDto, uriBuilder);
    }
    return labDtos;
  }

  @PostMapping(value = "/lab", headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto createLab(@RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    Long id = labService.create(lab, labDto.getInstituteId());
    return getLab(id, uriBuilder);
  }

  @PutMapping(value = "/lab/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public LabDto updateLab(@PathVariable("id") Long id, @RequestBody LabDto labDto,
      UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    lab.setId(id);
    labService.update(lab, labDto.getInstituteId());
    return getLab(id, uriBuilder);
  }

  @DeleteMapping(value = "/lab/{id}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteLab(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    Lab lab = labService.get(id);
    if (lab == null) {
      throw new RestException("Lab " + id + " not found", Status.NOT_FOUND);
    }
    labService.delete(lab);
  }

}
