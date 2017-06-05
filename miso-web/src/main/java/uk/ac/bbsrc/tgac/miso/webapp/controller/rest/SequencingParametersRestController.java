package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingParametersImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("sequencingparameters")
public class SequencingParametersRestController extends RestController {
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private PlatformService platformService;

  @RequestMapping(value = "/sequencingparameters/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SequencingParametersDto getSequencingParameters(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    SequencingParameters result = sequencingParametersService.get(id);
    if (result == null) {
      throw new RestException("No sequencing parameters found with ID: " + id, Status.NOT_FOUND);
    } else {
      return writeUrls(Dtos.asDto(result), uriBuilder);
    }
  }

  @RequestMapping(value = "/sequencingparameters", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public List<SequencingParametersDto> getSequencingParametersAlll(UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    List<SequencingParametersDto> dtos = Dtos.asSequencingParametersDtos(sequencingParametersService.getAll());
    for (SequencingParametersDto dto : dtos) {
      writeUrls(dto, uriBuilder);
    }
    return dtos;
  }

  @RequestMapping(value = "/sequencingparameters", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createSequencingParameters(@RequestBody SequencingParametersDto sequencingParamtersDto, UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    Platform platform = platformService.get(sequencingParamtersDto.getPlatform().getId());
    if (platform == null) {
      throw new RestException("No platform found with ID: " + sequencingParamtersDto.getPlatform().getId(), Status.BAD_REQUEST);
    }
    SequencingParameters sp = new SequencingParametersImpl();
    sp.setName(sequencingParamtersDto.getName());
    sp.setPlatform(platform);
    Long id = sequencingParametersService.create(sp);
    UriComponents uriComponents = b.path("/poolorder/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/sequencingparameters/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateSequencingParameters(@PathVariable("id") Long id, @RequestBody SequencingParametersDto spDto,
      HttpServletResponse response) throws IOException {
    SequencingParameters sequencingParameters = sequencingParametersService.get(id);
    if (sequencingParameters == null) {
      throw new RestException("No sequencing parameters found with ID: " + id, Status.NOT_FOUND);
    }
    sequencingParameters.setName(spDto.getName());
    sequencingParametersService.update(sequencingParameters);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sequencingparameters/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deleteSequencingParameters(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    sequencingParametersService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private static SequencingParametersDto writeUrls(SequencingParametersDto sequencingParametersDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    sequencingParametersDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sequencingparameters/{id}")
        .buildAndExpand(sequencingParametersDto.getId()).toUriString());
    sequencingParametersDto.setCreatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sequencingParametersDto.getCreatedById()).toUriString());
    sequencingParametersDto.setUpdatedByUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}")
        .buildAndExpand(sequencingParametersDto.getUpdatedById()).toUriString());
    return sequencingParametersDto;
  }
}
