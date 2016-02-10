package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.service.LabService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("lab")
public class LabController extends RestController {
  
  @Autowired
  private LabService labService;
  
  private static LabDto writeUrls(LabDto labDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    labDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/lab/{id}")
        .buildAndExpand(labDto.getId()).toUriString());
    labDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(labDto.getCreatedById()).toUriString());
    labDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(labDto.getUpdatedById()).toUriString());
    return labDto;
  }
  
  @RequestMapping(value = "/lab/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public LabDto getLab(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = labService.get(id);
    if (lab == null) {
      throw new RestException("No lab found with ID: " + id,Status.NOT_FOUND);
    } else {
      LabDto dto = Dtos.asDto(lab);
      writeUrls(dto, uriBuilder);
      return dto;
    }
  }
  
  @RequestMapping(value = "/labs", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<LabDto> getLabs(UriComponentsBuilder uriBuilder) throws IOException {
    Set<Lab> labs = labService.getAll();
    if (labs.isEmpty()) {
      throw new RestException("No labs found", Status.NOT_FOUND);
    } else {
      Set<LabDto> labDtos = Dtos.asLabDtos(labs);
      for (LabDto labDto : labDtos) {
        writeUrls(labDto, uriBuilder);
      }
      return labDtos;
    }
  }
  
  @RequestMapping(value = "/lab", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createLab(@RequestBody LabDto labDto, UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    Long id = labService.create(lab);
    UriComponents uriComponents = uriBuilder.path("/lab/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
  
  @RequestMapping(value = "/lab/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateLab(@PathVariable("id") Long id, @RequestBody LabDto labDto, 
      UriComponentsBuilder uriBuilder) throws IOException {
    Lab lab = Dtos.to(labDto);
    lab.setId(id);
    labService.update(lab);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @RequestMapping(value = "/lab/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSamplePurpose(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    labService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
