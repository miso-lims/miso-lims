package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;

@Controller
@RequestMapping("/rest/librarydilution")
public class LibraryDilutionRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryDilutionRestController.class);

  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

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

}
