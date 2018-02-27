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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstituteDto;
import uk.ac.bbsrc.tgac.miso.service.InstituteService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("institute")
public class InstituteController extends RestController {
  
  protected static final Logger log = LoggerFactory.getLogger(InstituteController.class);
  
  @Autowired
  private InstituteService instituteService;
  
  private static InstituteDto writeUrls(InstituteDto instituteDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    instituteDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/institute/{id}")
        .buildAndExpand(instituteDto.getId()).toUriString());
    instituteDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(instituteDto.getCreatedById()).toUriString());
    instituteDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(instituteDto.getUpdatedById()).toUriString());
    return instituteDto;
  }
  
  @RequestMapping(value = "/institute/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public InstituteDto getInstitute(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    Institute institute = instituteService.get(id);
    if (institute == null) {
      throw new RestException("No institute found with ID: " + id,Status.NOT_FOUND);
    } else {
      InstituteDto dto = Dtos.asDto(institute);
      writeUrls(dto, uriBuilder);
      return dto;
    }
  }
  
  @RequestMapping(value = "/institutes", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<InstituteDto> getInstitutes(UriComponentsBuilder uriBuilder) throws IOException {
    Set<Institute> institutes = instituteService.getAll();
    Set<InstituteDto> instituteDtos = Dtos.asInstituteDtos(institutes);
    for (InstituteDto instituteDto : instituteDtos) {
      writeUrls(instituteDto, uriBuilder);
    }
    return instituteDtos;
  }
  
  @RequestMapping(value = "/institute", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public InstituteDto createInstitute(@RequestBody InstituteDto instituteDto, UriComponentsBuilder uriBuilder) throws IOException {
    Institute institute = Dtos.to(instituteDto);
    Long id = instituteService.create(institute);
    return getInstitute(id, uriBuilder);
  }
  
  @RequestMapping(value = "/institute/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public InstituteDto updateInstitute(@PathVariable("id") Long id, @RequestBody InstituteDto instituteDto,
      UriComponentsBuilder uriBuilder) throws IOException {
    Institute institute = Dtos.to(instituteDto);
    institute.setId(id);
    instituteService.update(institute);
    return getInstitute(id, uriBuilder);
  }
  
  @RequestMapping(value = "/institute/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteInstitute(@PathVariable(name = "id", required = true) long id, HttpServletResponse response) throws IOException {
    Institute institute = instituteService.get(id);
    if (institute == null) {
      throw new RestException("Institute " + id + " not found", Status.NOT_FOUND);
    }
    instituteService.delete(institute);
  }
  
}
