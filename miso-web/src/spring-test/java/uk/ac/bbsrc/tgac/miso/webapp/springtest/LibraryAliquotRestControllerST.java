package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;


import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import com.jayway.jsonpath.JsonPath;
import java.io.BufferedReader;
import java.io.FileReader;

import static org.hamcrest.Matchers.*;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.dto.LibraryAliquotDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;

import org.springframework.security.test.context.support.WithMockUser;
import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;


public class LibraryAliquotRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryaliquots";
  private static final Class<LibraryAliquot> entityClass = LibraryAliquot.class;


  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt",
        Arrays.asList(1, 304, 305, 504, 505, 701, 702, 800, 801, 802, 803, 804, 901, 902, 1001, 1002, 1003, 1004,
            120001,
            120002, 200001, 200002));
  }

  @Test
  public void testDatatableByProject() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/300", Arrays.asList(304, 305));
  }

  @Test
  public void testDatatableByAvailable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/pool/802/available",
        Arrays.asList(1, 304, 305, 504, 505, 701, 702, 800, 801, 802, 803, 804, 901, 902, 1001, 1002, 1003, 1004,
            120001,
            120002, 200001, 200002));
  }

  @Test
  public void testDatatableByWorkset() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/workset/1", Arrays.asList(120001, 120002));
  }

  @Test
  public void testGetSpreadSheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(304L);
    req.setIds(ids);
    req.setSheet("TRACKING_LIST");

    List<String> headers = Arrays.asList("Name", "Alias", "Tissue Origin", "Tissue Type", "Barcode", "Library Name",
        "Library Alias", "Library Barcode", "Library Type", "Library Design", "Index(es)", "i7 Index", "i5 Index",
        "Targeted Sequencing", "Sample Name", "Sample Alias", "Sample Barcode", "Identity Name", "Identity Alias",
        "External Identifier", "Secondary Identifier", "Group ID", "Location");
    List<List<String>> rows = Arrays.asList(
        Arrays.asList("LDI1", "TEST_0001_Bn_R_PE_300_WG", "Bn", "R", "12321", "LIB1", "TEST_0001_Bn_R_PE_300_WG",
            "11211", "Paired End", "WG", "", "", "", "", "SAM8", "TEST_0001_Bn_R_nn_1-1_D_1", "88888", "SAM1",
            "TEST_0001", "TEST_external_1", "tube 1", "7357", "First Box - B02"),
        Arrays.asList("LDI304", "DILT_0001_nn_n_PE_304_WG", "Bn", "R", "300304", "LIB304", "DILT_0001_nn_n_PE_304_WG",
            "", "Paired End", "WG", "AAACCC", "AAACCC", "", "", "SAM304", "DILT_0001_nn_n_1-1_D_1", "", "SAM301",
            "DILT_0001", "DILT_identity_1", "", "", "Unknown"));
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, headers, rows);


  }

  @Test
  public void testGetLibraryAliquotsInBulk() throws Exception {
    List<String> names = new ArrayList<String>();
    names.add("LDI1");
    names.add("LDI304");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/query").content(makeJson(names)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("LDI1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("12321"))
        .andExpect(jsonPath("$[1].name").value("LDI304"))
        .andExpect(jsonPath("$[1].identificationBarcode").value("300304"));
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
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].box.name").value("BOX1"))
        .andExpect(jsonPath("$[0].box.tubeCount").value(11))
        .andExpect(jsonPath("$[0].boxPosition").value("C03"))
        .andExpect(jsonPath("$[0].alias").value("POOL_1"))
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  public void testGetParents() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(304L);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[1].name").value("SAM301"));
  }

  @Test
  public void testCreate() throws Exception {
    LibraryAliquot newLibraryAliquot = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("TEST_1111_Bn_R_PE_300_WG", newLibraryAliquot.getAlias());
    assertTrue(newLibraryAliquot.getLibrary().getId() == 1);
  }

  @Test
  public void testUpdate() throws Exception {
    LibraryAliquot lib = currentSession().get(entityClass, 1);
    lib.setAlias("TEST_1111_Bn_R_PE_300_WG");

    LibraryAliquot updated = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(lib, true), 1, entityClass);
    assertEquals("TEST_1111_Bn_R_PE_300_WG", updated.getAlias());
  }

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andExpect(jsonPath("$.name").value("LDI1"))
        .andExpect(jsonPath("$.alias").value("TEST_0001_Bn_R_PE_300_WG"))
        .andExpect(jsonPath("$.concentration").value(5.9));

  }

  private List<LibraryAliquotDto> makeCreateDtos() {

    List<LibraryAliquotDto> dtos = new ArrayList<LibraryAliquotDto>();
    LibraryAliquotDto one = new LibraryAliquotDto();
    one.setAlias("TEST_1111_Bn_R_PE_300_WG");
    one.setLibraryId(1L);
    one.setConcentration("12.5");
    one.setName("new one");
    one.setConcentrationUnits(ConcentrationUnit.NANOMOLAR);
    one.setCreationDate("2025-07-23");
    one.setDiscarded(false);

    LibraryAliquotDto two = new LibraryAliquotDto();
    two.setAlias("TEST_2222_Bn_R_PE_300_WG");
    two.setLibraryId(1L);
    two.setConcentration("22.5");
    two.setName("new two");
    two.setConcentrationUnits(ConcentrationUnit.NANOMOLAR);
    two.setCreationDate("2025-07-23");
    two.setDiscarded(false);

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryAliquot> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals(codes.get(0).getAlias(), "TEST_1111_Bn_R_PE_300_WG");
    assertEquals(codes.get(1).getAlias(), "TEST_2222_Bn_R_PE_300_WG");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these LibraryAliquots so only admin can update them
    LibraryAliquotDto libal1 = Dtos.asDto(currentSession().get(entityClass, 1), true);
    LibraryAliquotDto libal2 = Dtos.asDto(currentSession().get(entityClass, 304), true);
    libal1.setAlias("TEST_1111_Bn_R_PE_300_WG");
    libal2.setAlias("TEST_2222_Bn_R_PE_300_WG");

    List<LibraryAliquotDto> dtos = new ArrayList<LibraryAliquotDto>();
    dtos.add(libal1);
    dtos.add(libal2);

    List<LibraryAliquot> libraryAliquots =
        baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, LibraryAliquotDto::getId);
    assertEquals("TEST_1111_Bn_R_PE_300_WG", libraryAliquots.get(0).getAlias());
    assertEquals("TEST_2222_Bn_R_PE_300_WG", libraryAliquots.get(1).getAlias());
  }

  @Test
  public void testDeleteLibraryAliquot() throws Exception {
    // only creator can delete
    testBulkDelete(entityClass, 304, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 304, CONTROLLER_BASE);
  }
}
