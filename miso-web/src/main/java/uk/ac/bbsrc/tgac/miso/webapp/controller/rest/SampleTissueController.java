package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueDto;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;

@Controller
@RequestMapping("/rest/sample/")
@SessionAttributes("sampletissue")
public class SampleTissueController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(SampleTissueController.class);

  @Autowired
  private SampleTissueService sampleTissueService;

  private static SampleTissueDto writeUrls(SampleTissueDto sampleTissueDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sampleTissueDto.setUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/tissue/{id}").buildAndExpand(sampleTissueDto.getId()).toUriString());
    sampleTissueDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(sampleTissueDto.getCreatedById()).toUriString());
    sampleTissueDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(sampleTissueDto.getUpdatedById()).toUriString());
    return sampleTissueDto;
  }

  @RequestMapping(value = "/tissue/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleTissueDto getSampleTissue(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) {
    SampleTissue sampleTissue = getSampleTissueService().get(id);
    if (sampleTissue == null) {
      throw new RestException("No sample tissue found with ID: " + id, Status.NOT_FOUND);
    } else {
      SampleTissueDto dto = Dtos.asDto(sampleTissue);
      writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  @RequestMapping(value = "/tissues", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<SampleTissueDto> getSampleTissues(UriComponentsBuilder uriBuilder) {
    Set<SampleTissue> sampleTissues = getSampleTissueService().getAll();
    if (sampleTissues.isEmpty()) {
      throw new RestException("No sample tissues found", Status.NOT_FOUND);
    } else {
      Set<SampleTissueDto> sampleTissueDtos = Dtos.asSampleTissueDtos(sampleTissues);
      for (SampleTissueDto sampleTissueDto : sampleTissueDtos) {
        writeUrls(sampleTissueDto, uriBuilder);
      }
      return sampleTissueDtos;
    }
  }

  @RequestMapping(value = "/tissue", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSampleTissue(@RequestBody SampleTissueDto sampletissueDto, UriComponentsBuilder uriBuilder)
      throws IOException {
    SampleTissue sampleTissue = Dtos.to(sampletissueDto);
    Long id = getSampleTissueService().create(sampleTissue);
    UriComponents uriComponents = uriBuilder.path("/sample/tissue/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/tissue/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSampleTissue(@PathVariable("id") Long id, @RequestBody SampleTissueDto sampleTissueDto,
      UriComponentsBuilder uriBuilder) throws IOException {
    SampleTissue sampleTissue = Dtos.to(sampleTissueDto);
    sampleTissue.setSampleTissueId(id);
    getSampleTissueService().update(sampleTissue);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/tissue/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSampleTissue(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    getSampleTissueService().delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  public SampleTissueService getSampleTissueService() {
    return sampleTissueService;
  }

  public void setSampleTissueService(SampleTissueService sampleTissueService) {
    this.sampleTissueService = sampleTissueService;
  }
}
