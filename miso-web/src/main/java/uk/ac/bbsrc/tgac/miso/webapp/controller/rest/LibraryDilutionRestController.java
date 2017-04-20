package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;

@Controller
@RequestMapping("/rest/librarydilution")
public class LibraryDilutionRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryDilutionRestController.class);

  private final JQueryDataTableBackend<LibraryDilution, DilutionDto> jQueryBackend = new JQueryDataTableBackend<LibraryDilution, DilutionDto>() {
    @Override
    protected DilutionDto asDto(LibraryDilution model, UriComponentsBuilder builder) {
      DilutionDto dto = Dtos.asDto(model);
      if (dto.getLibrary() != null) {
        dto.getLibrary().writeUrls(builder);
      }
      return dto;
    }

    @Override
    protected PaginatedDataSource<LibraryDilution> getSource() throws IOException {
      return dilutionService;
    }
  };

  static class SelectRowDto {
    public String name;
    public Double concentration;
    public String library;
    public String sample;
    public String indices;
    public String lowquality;
    public Long id;
    public String lastModified;
  }

  private final JQueryDataTableBackend<LibraryDilution, SelectRowDto> jQueryBackendSelect = new JQueryDataTableBackend<LibraryDilution, SelectRowDto>() {

    @Override
    protected PaginatedDataSource<LibraryDilution> getSource() throws IOException {
      return dilutionService;
    }

    @Override
    protected SelectRowDto asDto(LibraryDilution dil, UriComponentsBuilder builder) {
      SelectRowDto dto = new SelectRowDto();
      dto.id = dil.getId();
      dto.name = dil.getName();
      dto.concentration = dil.getConcentration();
      dto.library = String.format("<a href='/miso/library/%d'>%s (%s)</a>", dil.getLibrary().getId(), dil.getLibrary().getAlias(),
          dil.getLibrary().getName());
      dto.sample = String.format("<a href='/miso/sample/%d'>%s (%s)</a>", dil.getLibrary().getSample().getId(),
          dil.getLibrary().getSample().getAlias(), dil.getLibrary().getSample().getName());
      StringBuilder indices = new StringBuilder();
      for (final Index index : dil.getLibrary().getIndices()) {
        indices.append(index.getPosition());
        indices.append(": ");
        indices.append(index.getLabel());
        indices.append("<br/>");
      }
      dto.indices = indices.toString();
      dto.lowquality = dil.getLibrary().isLowQuality() ? "&#9888;" : "";
      dto.lastModified = Dtos.dateFormatter.print(new DateTime(dil.getLastModified()));
      return dto;
    }
  };

  @Autowired
  private LibraryDilutionService dilutionService;

  @Autowired
  private RequestManager requestManager;

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DilutionDto getDilution(@PathVariable Long dilutionId) throws IOException {
    LibraryDilution dilution = dilutionService.get(dilutionId);
    DilutionDto dilutionDto = Dtos.asDto(dilution);
    return dilutionDto;
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createDilution(@RequestBody DilutionDto dilutionDto, UriComponentsBuilder b) throws IOException {
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
    UriComponents uriComponents = b.path("/library/{id}").buildAndExpand(dilution.getLibrary().getId());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    headers.set("Id", id.toString());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "dt/project/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<DilutionDto> getDilutionsByProject(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response, UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.project(id));
  }

  @RequestMapping(value = "dt/pool/{id}/available", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<SelectRowDto> availableDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {

    final Pool pool = requestManager.getPoolById(poolId);
    return jQueryBackendSelect.get(request, response, null, PaginationFilter.platformType(pool.getPlatformType()));
  }

  @RequestMapping(value = "dt/pool/{id}/included", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody DataTablesResponseDto<SelectRowDto> includedDilutions(@PathVariable("id") Long poolId, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackendSelect.get(request, response, null, PaginationFilter.pool(poolId));
  }

}
