package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDeletionDao;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;
import java.io.BufferedReader;
import java.io.FileReader;


import javassist.bytecode.ExceptionTable;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.LibraryRestController.FindRelatedRequest;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class LibraryRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraries";
  private static final Class<LibraryImpl> entityClass = LibraryImpl.class;
  private static final List<Integer> allIds =
      Arrays.asList(1, 204, 205, 206, 304, 305, 306, 504, 505, 600, 601, 602, 603,
          604, 700, 701, 801, 802, 803, 804, 805, 806, 807, 901, 2201, 100001, 100002, 100003, 100004, 100005,
          100006, 100007, 100008, 110001, 110002, 110003, 110004, 110005, 120001, 120002, 200001, 200002);


  private HibernateDeletionDao sut = new HibernateDeletionDao();


  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1).andExpect(jsonPath("$.name").value("LIB1"))
        .andExpect(jsonPath("$.alias").value("TEST_0001_Bn_R_PE_300_WG"));
  }

  private List<LibraryDto> makeCreateDtos() {
    LibraryDto lib1 = new LibraryDto();
    lib1.setName("LIB1");
    lib1.setSample(Dtos.asMinimalDto(currentSession().get(SampleImpl.class, 1)));
    lib1.setPaired(false);
    lib1.setDiscarded(false);
    lib1.setLowQuality(false);
    lib1.setUmis(false);
    lib1.setSopId(3L);
    lib1.setKitLot("KITLOTONE");
    lib1.setPlatformType("ILLUMINA");

    LibraryDto lib2 = new LibraryDto();
    lib2.setSample(Dtos.asMinimalDto(currentSession().get(SampleImpl.class, 2)));
    lib2.setPaired(true);
    lib2.setDiscarded(true);
    lib2.setLowQuality(true);
    lib2.setUmis(true);
    lib2.setSopId(4L);
    lib2.setKitLot("KITLOTONE");
    lib2.setPlatformType("PACBIO");



    return Arrays.asList(lib1, lib2);
  }

  @Test
  public void testCreate() throws Exception {
    LibraryImpl lib = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("LIB1", lib.getName());
    assertEquals(1L, lib.getSample().getId());
    assertEquals(false, lib.getPaired());
    assertEquals(false, lib.isDiscarded());
    assertEquals(false, lib.isLowQuality());
    assertEquals(false, lib.getUmis());

  }

  @Test
  public void testUpdate() throws Exception {
    LibraryDto updated = Dtos.asDto(currentSession().get(entityClass, 1), true);

    updated.setDescription("updated");

    LibraryImpl returned = baseTestUpdate(CONTROLLER_BASE, updated, 1, entityClass);
    assertEquals(updated.getDescription(), returned.getDescription());

  }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", allIds);
  }

  @Test
  public void testDatatableByProj() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/3", Arrays.asList(1));
  }

  @Test
  public void testDatatableByBatch() throws Exception {
    // all test libraries are in the same batch
    testDtRequest(CONTROLLER_BASE + "/dt/batch/1", allIds);
  }

  @Test
  public void testDatatableSamplesByReq() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/requisition/1", Arrays.asList(1, 2, 201, 12));

  }

  @Test
  public void testDatatableSupplementalSamplesByReq() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/requisition-supplemental/2", Arrays.asList(502));

  }

  @Test
  public void testDatatablePreparedSamplesByReq() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/requisition-prepared/1", Arrays.asList(1, 204, 205, 206));


  }

  @Test
  public void testDatatableByWorkstation() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/workstation/1", Arrays.asList(1, 204, 205, 206, 304, 305, 306, 504, 505));
  }

  @Test
  public void testGetLibrariesByWorkstation() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/workset/1", Arrays.asList(100001, 100002, 100003));
  }

  @Test
  public void testQuery() throws Exception {
    List<String> names = Arrays.asList("LIB1", "LIB204");

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/query").content(makeJson(names)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("LIB1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("11211"))
        .andExpect(jsonPath("$[1].name").value("LIB204"))
        .andExpect(jsonPath("$[1].identificationBarcode").value("11311"));
  }

  @Test
  public void testGetSpreadSheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    req.setIds(ids);
    req.setSheet("TRACKING_LIST");
    List<String> headers = Arrays.asList("Name", "Alias", "Tissue Origin", "Tissue Type", "Barcode", "Library Type",
        "Library Design", "i7 Index Name", "i7 Index", "i5 Index Name", "i5 Index", "Sample Name", "Sample Alias",
        "Sample Barcode", "Identity Name", "Identity Alias", "External Identifier", "Secondary Identifier", "Location");
    List<List<String>> rows = Arrays.asList(
        Arrays.asList("LIB1", "TEST_0001_Bn_R_PE_300_WG", "Bn", "R", "11211", "Paired End", "WG", "", "", "", "",
            "SAM8",
            "TEST_0001_Bn_R_nn_1-1_D_1", "88888", "SAM1", "TEST_0001", "TEST_external_1", "tube 1", "First Box - A01"));
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, headers, rows);

    // List<String> headers = Arrays.asList();

    // MockHttpServletResponse response = getMockMvc()
    // .perform(post(CONTROLLER_BASE +
    // "/spreadsheet").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
    // .andDo(print())
    // .andExpect(status().isOk())
    // .andExpect(content().contentType("text/csv")).andReturn().getResponse();

    // String filename = response.getHeader("Content-Disposition").split("=")[1];
    // List<List<String>> records = new ArrayList<>();
    // try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
    // String line;
    // int row = 0;
    // while ((line = br.readLine()) != null) {
    // String[] values = line.split(",");
    // records.add(Arrays.asList(values));
    // switch (row) {
    // case 0:
    // assertEquals(values[0], "Name");
    // break;

    // case 1:
    // assertEquals(values[0], "LIB1");
    // break;
    // }
    // row++;
    // }
    // } catch (Exception e) {
    // }

    // TODO: fix
  }

  @Test
  public void testGetParents() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(204L);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[1].id").value(201))
        .andExpect(jsonPath("$[1].name").value("SAM201"));
  }

  @Test
  public void testGetKids() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(304L);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/children/Pool").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$[0].box.name").value("BOX1"))
        .andExpect(jsonPath("$[0].box.tubeCount").value(11))
        .andExpect(jsonPath("$[0].boxPosition").value("C03"))
        .andExpect(jsonPath("$[0].alias").value("POOL_1"))
        .andExpect(jsonPath("$[0].id").value(1));
  }


  @Test
  @Transactional
  public void testDelete() throws Exception {
    // only owner or admin can delete
    sut.setEntityManager(getEntityManager());
    SampleImpl associated = currentSession().get(SampleImpl.class, 200004);
    sut.delete(associated, currentSession().get(UserImpl.class, 3));
    // need to delete the associated library aliquot first

    testBulkDelete(entityClass, 200002, CONTROLLER_BASE);
  }

  @Test
  @Transactional
  public void testDeleteFail() throws Exception {
    sut.setEntityManager(getEntityManager());
    SampleImpl associated = currentSession().get(SampleImpl.class, 8);
    sut.delete(associated, currentSession().get(UserImpl.class, 1));
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testBulkCreate() throws Exception {
    List<LibraryImpl> libs = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    // TODO
    // ASSERTIONS HERE
  }

  @Test
  public void testBulkUpdate() throws Exception {
    LibraryDto lib1 = Dtos.asDto(currentSession().get(LibraryImpl.class, 1), false);
    LibraryDto lib204 = Dtos.asDto(currentSession().get(LibraryImpl.class, 204), false);
    lib1.setDescription("one");
    lib204.setDescription("two hundred four");

    List<LibraryImpl> updatedLibs =
        baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, Arrays.asList(lib1, lib204), LibraryDto::getId);

    assertEquals(lib1.getDescription(), updatedLibs.get(0).getDescription());
    assertEquals(lib204.getDescription(), updatedLibs.get(1).getDescription());
  }



  @Test
  public void testFindRelated() throws Exception {
    FindRelatedRequest req = new FindRelatedRequest();
    req.identityIds = Arrays.asList(1L, 201L);
    req.libraryDesignCodeId = 7;
    req.excludeRequisitionId = 2;

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/find-related").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(4)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[1].id").value(204))
        .andExpect(jsonPath("$[2].id").value(205))
        .andExpect(jsonPath("$[3].id").value(206));

  }

  // TODO -- missing 1-2 tests
}
