package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TargetedSequencingDto;

@Controller
@RequestMapping("/rest/targetedsequencings")
public class TargetedSequencingRestController extends RestController {

  @Autowired
  private TargetedSequencingService targetedSequencingService;

  private final JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto> jQueryBackend = new JQueryDataTableBackend<TargetedSequencing, TargetedSequencingDto>() {
    @Override
    protected TargetedSequencingDto asDto(TargetedSequencing model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<TargetedSequencing> getSource() throws IOException {
      return targetedSequencingService;
    }
  };

  @GetMapping(value = "/{id}", produces = { "application/json" })
  @ResponseBody
  public TargetedSequencingDto getTargetedSequencing(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    TargetedSequencing targetedSequencing = targetedSequencingService.get(id);
    if (targetedSequencing == null) {
      throw new RestException("No targeted sequencing found with id: " + id, Status.NOT_FOUND);
    } else {
      return Dtos.asDto(targetedSequencing);
    }
  }

  @GetMapping(value = "/", produces = { "application/json" })
  @ResponseBody
  public Set<TargetedSequencingDto> getTargetedSequencings(HttpServletResponse response) throws IOException {
    Set<TargetedSequencing> targetedSequencings = (Set<TargetedSequencing>) targetedSequencingService.list();
    return Dtos.asTargetedSequencingDtos(targetedSequencings);
  }

  @GetMapping(value = "/dt/kit/{id}/available", produces = "application/json")
  public @ResponseBody DataTablesResponseDto<TargetedSequencingDto> availableTargetedSequencings(@PathVariable("id") Long kitDescriptorId,
      HttpServletRequest request, HttpServletResponse response, UriComponentsBuilder builder) throws IOException {
    return jQueryBackend.get(request, response, builder, new PaginationFilter[0]);
  }
}
