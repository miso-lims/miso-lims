package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.print.attribute.standard.Media;
import javax.ws.rs.core.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDeletionDao;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;
import java.io.BufferedReader;
import java.io.FileReader;

import jakarta.transaction.Transactional;
import javassist.bytecode.ExceptionTable;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;

import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.PoolRestController.*;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;

public class PoolRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/pools";
  private static final Class<PoolImpl> entityClass = PoolImpl.class;
  private static final List<Integer> ALL_IDS = Arrays.asList(1, 501, 120001, 120002, 120003, 120004, 120005, 200001,
      200002, 200003, 200004, 200005, 200006,
      5004, 5005, 5006, 5007, 5101, 5102, 5103, 5104, 5105, 701, 702, 801, 802, 803, 804, 2201);

  @Value("${miso.pools.samplesheet.dragenVersion}")
  private String dragenVersion;

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andExpect(jsonPath("$.box.name").value("BOX1"))
        .andExpect(jsonPath("$.box.id").value(1))
        .andExpect(jsonPath("$.alias").value("POOL_1"))
        .andExpect(jsonPath("$.concentration").value("8.25"));
  }

  @Test
  public void testGetRunsByPoolId() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/runs"))
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].instrumentId").value(2))
        .andExpect(jsonPath("$[0].name").value("RUN1"))
        .andExpect(jsonPath("$[0].type").value("Illumina"));
  }

  private List<PoolDto> makeCreateDtos() {
    PoolDto pool1 = new PoolDto();
    pool1.setPlatformType("ILLUMINA");
    pool1.setAlias("pool1_alias");
    pool1.setDiscarded(false);
    pool1.setCreationDate("2025-07-29");

    PoolDto pool2 = new PoolDto();
    pool2.setPlatformType("PACBIO");
    pool2.setAlias("pool2_alias");
    pool2.setDiscarded(false);
    pool2.setCreationDate("2025-07-29");

    return Arrays.asList(pool1, pool2);
  }

  @Test
  public void testCreate() throws Exception {
    PoolImpl created = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("ILLUMINA", created.getPlatformType().toString());
    assertEquals("pool1_alias", created.getAlias());
    assertEquals(false, created.isDiscarded());
    assertEquals("2025-07-29", created.getCreationDate().toString());
  }

  @Test
  public void testUpdate() throws Exception {
    PoolDto dto = Dtos.asDto(currentSession().get(entityClass, 1), true, true, null);
    dto.setDescription("updated");

    PoolImpl updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(updated.getDescription(), dto.getDescription());
  }

  @Test
  public void testChangeContents() throws Exception {
    PoolChangeRequest req = new PoolChangeRequest();
    // request to remove or add library aliquots to a pool
    int poolId = 801;

    PoolImpl beforeChange = currentSession().get(entityClass, poolId);
    assertEquals(2, beforeChange.getPoolContents().size());
    req.setRemove(Arrays.asList(200001L, 200002L)); // removes both of the library aliquots in this pool
    // based on the test data

    req.setAdd(Arrays.asList(1L)); // adds library aliquot 1

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/" + poolId + "/contents").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    PoolImpl changed = currentSession().get(entityClass, poolId);
    assertEquals(1, changed.getPoolContents().size());
    assertEquals(1L, changed.getPoolContents().iterator().next().getAliquot().getId());
  }

  @Test
  public void testChangeProportions() throws Exception {

    Map<String, Integer> proportions = new HashMap<String, Integer>();
    proportions.put("LDI801", 801);
    proportions.put("LDI802", 802);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/802/proportions").content(makeJson(proportions))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    PoolImpl changed = currentSession().get(entityClass, 802);
    Set<PoolElement> elements = changed.getPoolContents();
    assertTrue(elements.stream().anyMatch(x -> x.getProportion() == 801));
    assertTrue(elements.stream().anyMatch(x -> x.getProportion() == 802));
  }

  @Test
  public void testAssignPool() throws Exception {
    AssignPoolDto dto = new AssignPoolDto();
    dto.setUnits(ConcentrationUnit.NANOGRAMS_PER_MICROLITRE);
    dto.setConcentration("4.0000000000");
    dto.setPartitionIds(Arrays.asList(13L, 14L));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/802/assign").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    PoolImpl updated = currentSession().get(entityClass, 802);
    assertEquals(dto.getUnits(), updated.getConcentrationUnits());
    assertEquals(dto.getConcentration(), updated.getConcentration().toString());
  }

  @Test
  public void testGetPoolsByPlatform() throws Exception {
    testListAll(CONTROLLER_BASE + "/platform/Illumina", ALL_IDS, false);
  }

  @Test
  public void testGetDatatablePoolsByPlatform() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/platform/ILLUMINA", ALL_IDS);
  }

  @Test
  public void testGetDatatablePoolsByProject() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/3", Arrays.asList(1));

  }

  @Test
  public void testGetPickersBySearch() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap();
    params.add("platform", "ILLUMINA");
    params.add("query", "IPO51");
    testListAll(CONTROLLER_BASE + "/picker/search", Arrays.asList(5101, 5102, 5103, 5104, 5105), params, true);

  }

  @Test
  public void testGetPickersByRecentSearch() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap();
    params.add("platform", "ILLUMINA");
    testListAll(CONTROLLER_BASE + "/picker/recent",
        Arrays.asList(120001, 801, 120002, 120003, 802, 803, 804, 5004, 5101, 5005, 5102, 5006, 5103,
            5007, 5104, 5105, 501, 2201, 701, 702),
        params, true);
    // should only be 20 pools
    // this pool list was obtained from the request response

    // getMockMvc().perform(get(CONTROLLER_BASE +
    // "/picker/recent").param("platform", "ILLUMINA"))
    // .andDo(print())
    // .andExpect(status().isOk());

  }

  @Test
  public void testGetBulkPools() throws Exception {
    List<String> names = Arrays.asList("IPO1", "IPO501");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/query").content(makeJson(names)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].alias").value("POOL_1"))
        .andExpect(jsonPath("$[1].id").value(501))
        .andExpect(jsonPath("$[1].alias").value("TIB_Pool"));
  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "IPO51", Arrays.asList(5101, 5102, 5103, 5104, 5105));
  }

  @Test
  public void testGetSpreadsheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(501L);
    req.setIds(ids);
    req.setSheet("QPCR_RESULTS");

    List<String> headers = Arrays.asList("Name", "Alias", "Barcode", "Latest qPCR QC");
    List<List<String>> rows = Arrays.asList(Arrays.asList("IPO1", "POOL_1", "12341"),
        Arrays.asList("IPO501", "TIB_Pool", "TIB_Pool"));
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, rows, headers);

  }

  @Test
  public void testGetContentsSpreadsheet() throws Exception {
    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    req.setIds(ids);
    req.setSheet("TRACKING_LIST");

    List<String> headers = Arrays.asList("Name", "Alias", "Tissue Origin", "Tissue Type", "Barcode", "Library Name",
        "Library Alias", "Library Barcode", "Library Type", "Library Design", "Index(es)", "i7 Index", "i5 Index",
        "Targeted Sequencing", "Sample Name", "Sample Alias", "Sample Barcode", "Identity Name", "Identity Alias",
        "External Identifier", "Secondary Identifier", "Group ID", "Location");

    List<List<String>> rows = Arrays.asList(Arrays.asList("LDI1", "TEST_0001_Bn_R_PE_300_WG", "Bn", "R", "12321",
        "LIB1", "TEST_0001_Bn_R_PE_300_WG",
        "11211", "Paired End", "WG", "", "", "", "", "SAM8", "TEST_0001_Bn_R_nn_1-1_D_1", "88888", "SAM1", "TEST_0001",
        "TEST_external_1", "tube 1", "7357", "First Box - B02"));

    testSpreadsheetContents(CONTROLLER_BASE + "/contents/spreadsheet", req, rows, headers);
  }

  @Test
  public void testGetParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("11111"));
  }

  @Test
  public void testGetKids() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/children/Run").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("RUN1"))
        .andExpect(jsonPath("$[0].instrumentId").value(2));
  }

  @Test
  public void testGetSamplesheet() throws Exception {
    SampleSheetRequest req = new SampleSheetRequest();
    req.setPoolIds(Arrays.asList(1L, 501L));
    req.setExperimentType("CLONE_CHECKING");
    req.setSequencingParametersId(2L);
    req.setDragenVersion(dragenVersion);
    List<String> headers = Arrays.asList("Sample_ID", "Lane", "Sample_Plate", "Sample_Well", "I7_Index_ID", "index",
        "GenomeFolder", "Sample_Project", "Description");
    List<List<String>> rows = Arrays.asList(
        Arrays.asList("TEST_0001_Bn_R_PE_300_WG", "1", "", "", "No Index", "", "", "TEST", "12321"),
        Arrays.asList("TIB_0001_nn_n_PE_404_WG", "2", "", "", "No Index", "", "", "TIB", "TIB_Dil"));

    String response = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/samplesheet").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    response = response.split("\\[Data\\],")[1];

    String[][] records = new String[headers.size()][rows.size() + 1];
    String[] rawRows = response.split("\n");


    for (int i = 0; i < rawRows.length; i++) {
      String s = rawRows[i].replaceAll("\\r", "");
      s = s.replaceAll("\\n", "");
      s = s.replaceAll("\"", "");
      records[i] = s.split(",");

    }
    checkArray(records[1], headers);
    for (int i = 1; i < rows.size(); i++) {
      checkArray(records[i + 1], rows.get(i - 1));
    }
  }

  @Test
  public void testAsyncCreate() throws Exception {
    List<PoolImpl> created = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    // assertions here
    assertEquals("ILLUMINA", created.get(0).getPlatformType().toString());
    assertEquals("pool1_alias", created.get(0).getAlias());
    assertEquals(false, created.get(0).isDiscarded());

    assertEquals("PACBIO", created.get(1).getPlatformType().toString());
    assertEquals("pool2_alias", created.get(1).getAlias());
    assertEquals(false, created.get(1).isDiscarded());

  }

  @Test
  public void testAsyncUpdate() throws Exception {
    PoolDto pool1 = Dtos.asDto(currentSession().get(entityClass, 1), true, true, null);
    PoolDto pool501 = Dtos.asDto(currentSession().get(entityClass, 501), true, true, null);
    pool1.setDescription("pool 1");
    pool501.setDescription("pool 501");

    List<PoolImpl> pools = baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
        Arrays.asList(pool1, pool501),
        PoolDto::getId);
    assertEquals(pool1.getDescription(), pools.get(0).getDescription());
    assertEquals(pool501.getDescription(), pools.get(1).getDescription());

  }

  @Test
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 701, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }


  private void checkPoolIds(List<Integer> expectedIds, String response, boolean isItems) throws Exception {
    List<Integer> returnedIds = new ArrayList<Integer>();
    List<Integer> resultIds = JsonPath.read(response, "$.items[*].pool.id");
    assertEquals(expectedIds.size(), resultIds.size());
    for (Integer expectedId : expectedIds) {
      assertTrue("id " + expectedId + " expected but not found", resultIds.contains(expectedId));
    }

  }


  private ResultActions testListPools(String url, List<Integer> ids, MultiValueMap params, boolean isItems)
      throws Exception {
    ResultActions result = getMockMvc().perform(get(url).params(params));

    String response = result.andReturn().getResponse().getContentAsString();
    checkPoolIds(ids, false, response);

    return result; // return the result for more content testing if needed
  }

  private ResultActions testListPools(String url, List<Integer> ids) throws Exception {
    return testListPools(url, ids, new MultiValueMap());
  }

}
