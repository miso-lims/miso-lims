/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.map.ObjectMapper;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.TagBarcodeService;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.ProjectSampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.SampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

/**
 * A controller to handle all REST requests for Libraries
 * 
 * @author Rob Davey
 * @date 16-Aug-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/library")
@SessionAttributes("library")
public class LibraryRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private LibraryAdditionalInfoService libraryAdditionalInfoService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private TagBarcodeService tagBarcodeFamilyService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{libraryId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String getLibraryById(@PathVariable Long libraryId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Library l = requestManager.getLibraryById(libraryId);
    if (l == null) {
      throw new RestException("No library found with ID: " + libraryId, Status.NOT_FOUND);
    }
    mapper.getSerializationConfig().addMixInAnnotations(Project.class, ProjectSampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(l);
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String listAllLibraries() throws IOException {
    Collection<Library> libraries = requestManager.listAllLibraries();
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(Project.class, ProjectSampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(libraries);
  }

  private Long populateAndSaveLibraryFromDto(LibraryDto libraryDto, Library library, boolean create) throws IOException {
    User user = authorizationManager.getCurrentUser();
    library.setLastModifier(user);
    library.setLibrarySelectionType(requestManager.getLibrarySelectionTypeById(libraryDto.getLibrarySelectionTypeId()));
    library.setLibraryStrategyType(requestManager.getLibraryStrategyTypeById(libraryDto.getLibraryStrategyTypeId()));
    library.setLibraryType(requestManager.getLibraryTypeById(libraryDto.getLibraryTypeId()));
    List<TagBarcode> tagBarcodes = new ArrayList<>();
    if (libraryDto.getTagBarcodeIndex1Id() != null) {
      tagBarcodes.add(tagBarcodeFamilyService.getTagBarcodeById(libraryDto.getTagBarcodeIndex1Id()));
    }
    if (libraryDto.getTagBarcodeIndex2Id() != null) {
      tagBarcodes.add(tagBarcodeFamilyService.getTagBarcodeById(libraryDto.getTagBarcodeIndex2Id()));
    }
    library.setTagBarcodes(tagBarcodes);
    library.setLibraryAdditionalInfo(Dtos.to(libraryDto.getLibraryAdditionalInfo()));
    library.getLibraryAdditionalInfo().setCreatedBy(user);
    library.getLibraryAdditionalInfo().setUpdatedBy(user);
    return requestManager.saveLibrary(library);
  }

  @RequestMapping(method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createLibrary(@RequestBody LibraryDto libraryDto, UriComponentsBuilder b) throws IOException {
    Long id = null;
    try {
      Library library = Dtos.to(libraryDto);
      library.setCreationDate(new Date());
      library.setSample(requestManager.getSampleById(libraryDto.getParentSampleId()));
      id = populateAndSaveLibraryFromDto(libraryDto, library, true);
    } catch (ConstraintViolationException | IllegalArgumentException e) {
      log.error("Error while creating library. ", e);
      RestException restException = new RestException(e.getMessage(), Status.BAD_REQUEST);
      if (e instanceof ConstraintViolationException) {
        restException.addData("constraintName", ((ConstraintViolationException) e).getConstraintName());
      }
      throw restException;
    }
    UriComponents uriComponents = b.path("/library/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updateLibrary(@PathVariable("id") Long id, @RequestBody LibraryDto libraryDto) throws IOException {
    Library library = requestManager.getLibraryById(id);
    if (library == null) {
      throw new RestException("No such library.", Status.NOT_FOUND);
    }
    library = Dtos.to(libraryDto, library);
    populateAndSaveLibraryFromDto(libraryDto, library, false);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<LibraryDto> getLibraries(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    if (request.getParameterMap().size() > 0) {
      Long numLibraries = Long.valueOf(requestManager.countLibraries());
      // get request params from DataTables
      Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
      Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
      String sSearch = request.getParameter("sSearch");
      String sSortDir = request.getParameter("sSortDir_0");
      String sortColIndex = request.getParameter("iSortCol_0");
      String sortCol = request.getParameter("mDataProp_" + sortColIndex);

      // get requested subset of libraries
      Collection<Library> librarySubset;
      Long numMatches;

      if (!isStringEmptyOrNull(sSearch)) {
        librarySubset = requestManager.getLibrariesByPageSizeSearch(iDisplayStart, iDisplayLength, sSearch, sSortDir, sortCol);
        numMatches = Long.valueOf(requestManager.countLibrariesBySearch(sSearch));
      } else {
        librarySubset = requestManager.getLibrariesByPageAndSize(iDisplayStart, iDisplayLength, sSortDir, sortCol);
        numMatches = numLibraries;
      }
      List<LibraryDto> libraryDtos = Dtos.asLibraryDtos(librarySubset);
      for (LibraryDto libraryDto : libraryDtos) {
        libraryDto.writeUrls(uriBuilder);
      }

      DataTablesResponseDto<LibraryDto> dtResponse = new DataTablesResponseDto<LibraryDto>();
      dtResponse.setITotalRecords(numLibraries);
      dtResponse.setITotalDisplayRecords(numMatches);
      dtResponse.setAaData(libraryDtos);
      dtResponse.setSEcho(new Long(request.getParameter("sEcho")));
      return dtResponse;
    } else {
      throw new RestException("Request must specify DataTables parameters.");
    }
  }
}
