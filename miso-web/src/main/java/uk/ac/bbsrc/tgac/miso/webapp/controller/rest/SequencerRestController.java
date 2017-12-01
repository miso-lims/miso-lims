package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SequencerDto;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;

@Controller
@RequestMapping("/rest/sequencer")
public class SequencerRestController extends RestController {
  private final JQueryDataTableBackend<SequencerReference, SequencerDto> jQueryBackend = new JQueryDataTableBackend<SequencerReference, SequencerDto>() {
    @Override
    protected SequencerDto asDto(SequencerReference model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<SequencerReference> getSource() throws IOException {
      return sequencerService;
    }
  };

  @Autowired
  private SequencerReferenceService sequencerService;

  public void setLibraryService(SequencerReferenceService sequencerService) {
    this.sequencerService = sequencerService;
  }

  @RequestMapping(value = "/{sequencerId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public SequencerDto getById(@PathVariable Long sequencerId) throws IOException {
    SequencerReference r = sequencerService.get(sequencerId);
    if (r == null) {
      throw new RestException("No sequencer found with ID: " + sequencerId, Status.NOT_FOUND);
    }
    SequencerDto dto = Dtos.asDto(r);
    return dto;
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public List<SequencerDto> listAll() throws IOException {
    return sequencerService.list().stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  private static final Logger log = LoggerFactory.getLogger(SequencerRestController.class);

  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public SequencerDto create(@RequestBody SequencerDto sequencerDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    if (sequencerDto == null) {
      throw new RestException("Cannot convert null to sequencer", Status.BAD_REQUEST);
    }
    Long id = null;
    try {
      SequencerReference sequencer = Dtos.to(sequencerDto);
      id = sequencerService.create(sequencer);
    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating library. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }
    SequencerDto created = getById(id);
    UriComponents uriComponents = b.path("/sequencer/{id}").buildAndExpand(id);
    response.setHeader("Location", uriComponents.toUri().toString());
    return created;
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<SequencerDto> datatable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

}
