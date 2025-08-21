package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;



import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;


import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.PoolRestController.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Comparator;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Arrays;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



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
    pool1.setLibraryAliquotCount(1);
    pool1.setPooledElements(Set.of(Dtos.asDto(currentSession().get(LibraryAliquot.class, 1), false)));



    PoolDto pool2 = new PoolDto();
    pool2.setPlatformType("PACBIO");
    pool2.setAlias("pool2_alias");
    pool2.setDiscarded(false);
    pool2.setCreationDate("2025-07-30");
    pool2.setLibraryAliquotCount(2);
    pool2.setPooledElements(Set.of(Dtos.asDto(currentSession().get(LibraryAliquot.class, 304), false),
        Dtos.asDto(currentSession().get(LibraryAliquot.class, 305), false)));


    return Arrays.asList(pool1, pool2);
  }

  @Test
  public void testCreate() throws Exception {
    PoolImpl created = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("ILLUMINA", created.getPlatformType().toString());
    assertEquals("pool1_alias", created.getAlias());
    assertEquals(false, created.isDiscarded());
    assertEquals(LocalDate.of(2025, 7, 29), created.getCreationDate());
    assertEquals(1, created.getPoolContents().size());
    assertEquals(1L, created.getPoolContents().iterator().next().getAliquot().getId());
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
    List<PoolElement> elements =
        changed.getPoolContents().stream().sorted(Comparator.comparing(PoolElement::getProportion)).toList();

    assertEquals(801L, elements.get(0).getAliquot().getId());
    assertEquals(801, elements.get(0).getProportion());

    assertEquals(802L, elements.get(1).getAliquot().getId());
    assertEquals(802, elements.get(1).getProportion());

  }

  @Test
  public void testAssignPool() throws Exception {
    AssignPoolDto dto = new AssignPoolDto();
    dto.setUnits(ConcentrationUnit.NANOGRAMS_PER_MICROLITRE);
    dto.setConcentration("4.0000000000");
    List<Long> partitionIds = Arrays.asList(13L, 14L);
    dto.setPartitionIds(partitionIds);

    int poolId = 802;

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + poolId + "/assign").content(makeJson(dto))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    PoolImpl updated = currentSession().get(entityClass, 802);
    // just to make sure it matches the expected
    assertEquals(dto.getUnits(), updated.getConcentrationUnits());
    assertEquals(dto.getConcentration(), updated.getConcentration().toString());
    for (Long id : partitionIds) {
      PartitionImpl part = currentSession().get(PartitionImpl.class, id);
      assertEquals(poolId, part.getPool().getId());
      assertEquals(updated.getConcentration(), part.getLoadingConcentration());
    }
  }

  @Test
  public void testGetPoolsByPlatform() throws Exception {
    testListPools(CONTROLLER_BASE + "/platform/Illumina", ALL_IDS);
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
    testListPools(CONTROLLER_BASE + "/picker/search", Arrays.asList(5101, 5102, 5103, 5104, 5105), params, true);

  }

  @Test
  public void testGetPickersByRecentSearch() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap();
    params.add("platform", "ILLUMINA");
    testListPools(CONTROLLER_BASE + "/picker/recent",
        Arrays.asList(120001, 801, 120002, 120003, 802, 803, 804, 5004, 5101, 5005, 5102, 5006, 5103,
            5007, 5104, 5105, 501, 2201, 701, 702),
        params, true);
    // returns the 20 pools with latest last modified date
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
    testSpreadsheetContents(CONTROLLER_BASE + "/spreadsheet", req, headers, rows);

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

    testSpreadsheetContents(CONTROLLER_BASE + "/contents/spreadsheet", req, headers, rows);
  }

  @Test
  public void testGetIdentityParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Identity").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("SAM1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("11111"));

  }

  @Test
  public void testGetTissueParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(2))
        .andExpect(jsonPath("$[0].name").value("SAM2"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("22222"));
  }

  @Test
  public void testGetTissueProcessingParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Tissue Processing").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(5))
        .andExpect(jsonPath("$[0].name").value("SAM5"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("55555"));

  }

  @Test
  public void testGetStockParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Stock").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(6))
        .andExpect(jsonPath("$[0].name").value("SAM6"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("66666"));

  }

  @Test
  public void testGetAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Aliquot").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(8))
        .andExpect(jsonPath("$[0].name").value("SAM8"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("88888"));

  }

  @Test
  public void testGetLibraryParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("LIB1"))
        .andExpect(jsonPath("$[0].box.id").value("1"))
        .andExpect(jsonPath("$[0].description").value("description lib 1"));

  }

  @Test
  public void testGetLibraryAliquotParents() throws Exception {
    List<Integer> ids = Arrays.asList(1, 504);
    getMockMvc()
        .perform(
            post(CONTROLLER_BASE + "/parents/Library Aliquot").content(makeJson(ids))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("LDI1"))
        .andExpect(jsonPath("$[0].identificationBarcode").value("12321"));
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

    String response = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/samplesheet").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn().getResponse().getContentAsString();

    List<String> sectionTitles = Arrays.asList("[Header]", "[Reads]", "[Settings],", "[Data],");
    for (String title : sectionTitles) {
      assertTrue(response.contains(title));
    }
  }

  @Test
  public void testAsyncCreate() throws Exception {
    List<PoolImpl> created = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("ILLUMINA", created.get(0).getPlatformType().toString());
    assertEquals("pool1_alias", created.get(0).getAlias());
    assertEquals(false, created.get(0).isDiscarded());
    assertEquals(1, created.get(0).getPoolContents().size());
    assertEquals(1L, created.get(0).getPoolContents().toArray(new PoolElement[0])[0].getAliquot().getId());
    assertEquals(LocalDate.of(2025, 7, 29), created.get(0).getCreationDate());


    assertEquals("PACBIO", created.get(1).getPlatformType().toString());
    assertEquals("pool2_alias", created.get(1).getAlias());
    assertEquals(false, created.get(1).isDiscarded());
    assertEquals(2, created.get(1).getPoolContents().size());
    List<PoolElement> poolTwoElements =
        created.get(1).getPoolContents().stream().sorted(Comparator.comparing(element -> element.getAliquot().getId()))
            .toList();
    assertEquals(304L, poolTwoElements.get(0).getAliquot().getId());
    assertEquals(305L, poolTwoElements.get(1).getAliquot().getId());
    assertEquals(LocalDate.of(2025, 7, 30), created.get(1).getCreationDate());
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
    String addedPath = "";
    String addedAfter = "";


    if (isItems) {
      addedPath = ".items";
      addedAfter = ".pool";
    }

    List<Integer> resultIds = JsonPath.read(response, "$" + addedPath + "[*]" + addedAfter + ".id");
    assertEquals(expectedIds.size(), resultIds.size());
    for (Integer expectedId : expectedIds) {
      assertTrue("id " + expectedId + " expected but not found", resultIds.contains(expectedId));
    }

  }


  private ResultActions testListPools(String url, List<Integer> ids, MultiValueMap params, boolean isItems)
      throws Exception {
    ResultActions result = getMockMvc().perform(get(url).params(params));

    String response = result.andReturn().getResponse().getContentAsString();
    checkPoolIds(ids, response, isItems);

    return result; // return the result for more content testing if needed
  }

  private ResultActions testListPools(String url, List<Integer> ids) throws Exception {
    return testListPools(url, ids, new LinkedMultiValueMap(), false);
  }

}
