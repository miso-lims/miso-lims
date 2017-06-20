package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;

@Controller
@RequestMapping("/rest/librarydilution")
public class LibraryDilutionRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryDilutionRestController.class);

  private final JQueryDataTableBackend<PoolableElementView, DilutionDto> jQueryBackend = new JQueryDataTableBackend<PoolableElementView, DilutionDto>() {
    @Override
    protected DilutionDto asDto(PoolableElementView model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<PoolableElementView> getSource() throws IOException {
      return poolableElementViewService;
    }
  };

  @Autowired
  private LibraryDilutionService dilutionService;

  @Autowired
  private PoolableElementViewService poolableElementViewService;

  @Autowired
  private PoolService poolService;

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DilutionDto getDilution(@PathVariable Long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    DilutionDto dilutionDto = Dtos.asMinimalDto(dilution);
    return dilutionDto;
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public DilutionDto createDilution(@RequestBody DilutionDto dilutionDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    if (dilutionDto == null) {
      log.error(
          "Received null dilutionDto from front end; cannot convert to Dilution. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Dilution", Status.BAD_REQUEST);
    }
    Long id = null;
    LibraryDilution dilution;
    try {
      dilution = Dtos.to(dilutionDto);
      id = dilutionService.create(dilution);
    } catch (ConstraintViolationException e) {
      log.error("Error while creating dilution", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      restException.addData("constraintName", e.getConstraintName());
      throw restException;
    }
    UriComponents uriComponents = b.path("/{id}").buildAndExpand(dilution.getId());
    response.setHeader("Location", uriComponents.toUri().toString());
    return getDilution(id);
  }

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public DilutionDto updateDilution(@PathVariable Long dilutionId, @RequestBody DilutionDto dilutionDto)
      throws IOException {
    if (dilutionDto == null) {
      log.error(
          "Received null dilutionDto from front end; cannot convert to Dilution. Something likely went wrong in the JS DTO conversion.");
      throw new RestException("Cannot convert null to Dilution", Status.BAD_REQUEST);
    }
    LibraryDilution dilution = Dtos.to(dilutionDto);
    dilution.setId(dilutionId);
    dilutionService.update(dilution);
    return getDilution(dilutionId);
  }

  @RequestMapping(value = "dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutions(HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutionsByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "dt/pool/{id}/available", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<DilutionDto> availableDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {

    final Pool pool = poolService.get(poolId);
    return jQueryBackend.get(request, response, null, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @RequestMapping(value = "dt/pool/{id}/included", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<DilutionDto> includedDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, null, PaginationFilter.pool(poolId));
  }

}
