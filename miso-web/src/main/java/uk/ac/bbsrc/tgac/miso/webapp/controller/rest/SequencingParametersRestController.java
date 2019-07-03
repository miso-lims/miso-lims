package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

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

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;

@Controller
@RequestMapping("/rest/sequencingparameters")
public class SequencingParametersRestController extends RestController {
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private InstrumentModelService platformService;

  @GetMapping(value = "/{id}", produces = { "application/json" })
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

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public List<SequencingParametersDto> getSequencingParametersAlll(UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    List<SequencingParametersDto> dtos = Dtos.asSequencingParametersDtos(sequencingParametersService.getAll());
    for (SequencingParametersDto dto : dtos) {
      writeUrls(dto, uriBuilder);
    }
    return dtos;
  }

  @PostMapping(headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SequencingParametersDto createSequencingParameters(@RequestBody SequencingParametersDto sequencingParamtersDto,
      UriComponentsBuilder b,
      HttpServletResponse response) throws IOException {
    InstrumentModel platform = platformService.get(sequencingParamtersDto.getInstrumentModel().getId());
    if (platform == null) {
      throw new RestException("No platform found with ID: " + sequencingParamtersDto.getInstrumentModel().getId(), Status.BAD_REQUEST);
    }
    SequencingParameters sp = new SequencingParameters();
    sp.setName(sequencingParamtersDto.getName());
    sp.setInstrumentModel(platform);
    Long id = sequencingParametersService.create(sp);
    return Dtos.asDto(sequencingParametersService.get(id));
  }

  @PutMapping(value = "/{id}", headers = { "Content-type=application/json" })
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public SequencingParametersDto updateSequencingParameters(@PathVariable("id") Long id, @RequestBody SequencingParametersDto spDto,
      HttpServletResponse response) throws IOException {
    SequencingParameters sequencingParameters = sequencingParametersService.get(id);
    if (sequencingParameters == null) {
      throw new RestException("No sequencing parameters found with ID: " + id, Status.NOT_FOUND);
    }
    sequencingParameters.setName(spDto.getName());
    sequencingParametersService.update(sequencingParameters);
    return Dtos.asDto(sequencingParametersService.get(id));
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
