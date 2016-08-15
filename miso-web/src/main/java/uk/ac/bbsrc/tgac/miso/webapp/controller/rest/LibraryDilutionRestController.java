package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Date;

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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.DilutionDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/rest/librarydilution")
public class LibraryDilutionRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryDilutionRestController.class);

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{dilutionId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DilutionDto getDilution(@PathVariable Long dilutionId) throws IOException {
    LibraryDilution dilution = requestManager.getLibraryDilutionById(dilutionId);
    DilutionDto dilutionDto = Dtos.asDto(dilution);
    return dilutionDto;
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createDilution(@RequestBody DilutionDto dilutionDto, UriComponentsBuilder b) throws IOException {
    Long id = null;
    LibraryDilution dilution;
    try {
      dilution = Dtos.to(dilutionDto);
      dilution.setLibrary(requestManager.getLibraryById(dilutionDto.getLibrary().getId()));
      id = populateAndSaveDilutionFromDto(dilutionDto, dilution, true);
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

  private Long populateAndSaveDilutionFromDto(DilutionDto dilutionDto, LibraryDilution dilution, boolean create) throws IOException {
    User user = authorizationManager.getCurrentUser();
    dilution.setDilutionCreator(user.getFullName());
    if (dilutionDto.getTargetedResequencingId() != null) {
      dilution.setTargetedResequencing(requestManager.getTargetedResequencingById(dilutionDto.getTargetedResequencingId()));
    }
    if (create) {
      dilution.setCreationDate(new Date());
    }
    Long id = requestManager.saveLibraryDilution(dilution);
    return id;
  }

}
