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

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;

public class PoolRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/pools";
  private static final Class<PoolImpl> entityClass = PoolImpl.class;


  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andDo(print());
  }

  @Test
  public void testGetRunsByPoolId() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/runs")).andDo(print());
  }

  private List<PoolDto> makeCreateDtos() {
    PoolDto pool1 = new PoolDto();
    pool1.setName("pool1");
    pool1.setPlatformType("ILLUMINA");
    pool1.setAlias("pool1_alias");
    pool1.setDiscarded(false);

    PoolDto pool2 = new PoolDto();
    pool2.setName("pool2");
    pool2.setPlatformType("PACBIO");
    pool2.setAlias("pool2_alias");
    pool2.setDiscarded(false);

    return Arrays.asList(pool1, pool2);
  }

  @Test
  public void testCreate() throws Exception {
    PoolImpl created = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("pool1", created.getName());
    assertEquals("ILLUMINA", created.getPlatformType());
    assertEquals("pool1_alias", created.getAlias());
    assertEquals(false, created.isDiscarded());
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
    int poolId = 802;

    PoolImpl beforeChange = currentSession().get(entityClass, poolId);
    assertEquals(2, beforeChange.getPoolContents().size());
    req.setRemove(Arrays.asList(801, 802)); // removes both of the library aliquots in this pool
    // based on the test data

    req.setAdd(Arrays.asList(1)); // adds library aliquot 1

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/" + poolId + "/contents").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    PoolImpl changed = currentSession().get(entityClass, poolId);
    assertEquals(beforeChange.getPoolContents().size() - 1, changed.getPoolContents().size());
    assertEquals(1L, changed.getPoolContents().iterator().next().getAliquot().getId());
  }

  @Test
  public void testChangeProportions() throws Exception {
    // string library aliquot name, int proportion

    Map<String, Integer> proportions = new HashMap<String, Integer>();
    proportions.put("LDI801", 801);
    proportions.put("LDI802", 802);

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/802/proportions").content(makeJson(proportions))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    PoolImpl changed = currentSession().get(entityClass, 802);
    Iterator<PoolElement> elements = changed.getPoolContents().iterator();
    elements = elements.next();
    assertEquals(801, elements.next().getProportion());
    assertEquals(802, elements.next().getProportion());
  }

  @Test
  public void testAssignPool() throws Exception {
    AssignPoolDto dto = new AssignPoolDto();
    dto.setUnits(ConcentrationUnit.PICOMOLAR);
    dto.setConcentration("12.25");
    dto.setPartitionIds(Arrays.asList(21, 22));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/802/assign").content(makeJson(dto)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNoContent());

    PoolImpl updated = currentSession().get(entityClass, 802);
    assertEquals(dto.getUnits(), updated.getConcentrationUnits());
    assertEquals(dto.getConcentration(), updated.getConcentration().toString());
  }

  @Test
  public void testGetPoolsByPlatform() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/platform/Illumina"))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void tesGetDatatablePoolsByPlatform() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/platform/Illumina",
        Arrays.asList(1, 501, 120001, 120002, 120003, 120004, 120005, 200001, 200002, 200003, 200004, 200005, 200006,
            5004, 5005, 5006, 5007, 5101, 5102, 5103, 5104, 5105, 701, 702, 801, 802, 803, 804));
  }

  @Test
  public void testGetDatatablePoolsByProject() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/1", Arrays.asList(1));

  }

  @Test
  public void testGetPickersBySearch() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/picker/search").param("platform", "Illumina").param("query", "IPO1"))
    .andDo(print())
    .andExpect(status().isOk());
  }

  @Test
  public void testGetPickersByRecentSearch() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/picker/recent").param("platform", "Illumina"))
    .andDo(print())
    .andExpect(status().isOk());
  }

  @Test
  public void testGetBulkPools() throws Exception {
    List<String> names = Arrays.asList("IPO1", "IPO501");
    getMockMvc().perform(get(CONTROLLER_BASE  + "/query").content(makeJson(names)).contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isOk());
  }

  @Test
  public void testSearch() throws Exception {
    baseTestSearch(CONTROLLER_BASE + "/search", "IPO51", Arrays.asList(5101, 5102, 5103, 5104, 5105));
  }

  @Test
  public void testGetSpreadsheet() throws Exception {

  }

  @Test
  public void testGetContentsSpreadsheet() throws Exception {

  }

  @Test
  public void testGetParents() throws Exception {

  }

  @Test
  public void testGetKids() throws Exception {

  }

  @Test
  public void testGetSamplesheet() throws Exception {

  }

  @Test
  public void testAsyncCreate() throws Exception {
    List<PoolImpl> created = baseTestBulkCreateAsync(CONTROLLER_BASE, entityType, makeCreateDtos());
    // assertions here

  }

  @Test
  public void testAsyncUpdate() throws Exception {
    PoolDto pool1 = Dtos.asDto(currentSession().get(entityClass, 1), true, true, null);
    PoolDto pool504 = Dtos.asDto(currentSession().get(entityClass, 504), true, true, null);
    pool1.setDescription("pool 1");
    pool504.setDescription("pool 504");

    List<PoolImpl> pools =
        (List<PoolImpl>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClas, dtos, Arrays.asList(1, 504));
    assertEquals(pool1.getDescription(), pools.get(0).getDescription());
    assertEquals(pool504.getDescription(), pools.get(1).getDescription());

  }

  @Test
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 701, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }

}
