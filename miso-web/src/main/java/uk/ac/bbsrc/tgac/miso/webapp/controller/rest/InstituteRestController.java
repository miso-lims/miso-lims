package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.service.InstituteService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstituteDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;

@Controller
@RequestMapping("/rest/institutes")
public class InstituteRestController extends RestController {
  
  protected static final Logger log = LoggerFactory.getLogger(InstituteRestController.class);
  
  @Autowired
  private InstituteService instituteService;
  
  @Autowired
  private ConstantsController constantsController;
  
  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public InstituteDto getInstitute(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    Institute institute = instituteService.get(id);
    if (institute == null) {
      throw new RestException("No institute found with ID: " + id,Status.NOT_FOUND);
    } else {
      InstituteDto dto = Dtos.asDto(institute);
      return dto;
    }
  }
  
  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public Set<InstituteDto> getInstitutes(UriComponentsBuilder uriBuilder) throws IOException {
    List<Institute> institutes = instituteService.list();
    Set<InstituteDto> instituteDtos = Dtos.asInstituteDtos(institutes);
    return instituteDtos;
  }
  
  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseBody
  public InstituteDto createInstitute(@RequestBody InstituteDto instituteDto, UriComponentsBuilder uriBuilder) throws IOException {
    Institute institute = Dtos.to(instituteDto);
    Long id = instituteService.create(institute);
    constantsController.refreshConstants();
    return getInstitute(id, uriBuilder);
  }
  
  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  public InstituteDto updateInstitute(@PathVariable("id") Long id, @RequestBody InstituteDto instituteDto, UriComponentsBuilder uriBuilder)
      throws IOException {
    Institute institute = Dtos.to(instituteDto);
    institute.setId(id);
    instituteService.update(institute);
    constantsController.refreshConstants();
    return getInstitute(id, uriBuilder);
  }
  
  @PostMapping(value = "/bulk-delete")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteInstitute(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Institute", ids, instituteService);
    constantsController.refreshConstants();
  }
  
}
