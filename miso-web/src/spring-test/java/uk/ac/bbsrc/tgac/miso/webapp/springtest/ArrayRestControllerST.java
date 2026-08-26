package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ArrayRestController.BulkUpdateRequestItem;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class ArrayRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/arrays";
  private static final Class<Array> controllerClass = Array.class;


  @Test
  public void testDtResponse() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2));
  }

  @Test
  public void testSave() throws Exception {
    ArrayDto saver = new ArrayDto();
    saver.setAlias("new test array");
    saver.setArrayModelId(1L);
    saver.setLastModified("2025-07-09");
    saver.setSerialNumber("1453");

    Array saved = baseTestCreate(CONTROLLER_BASE, saver, controllerClass, 201);
    assertEquals(saver.getAlias(), saved.getAlias());
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testUpdate() throws Exception {
    ArrayDto array = Dtos.asDto(currentSession().find(controllerClass, 2));
    array.setAlias("testing array");

    Array updatedArray = baseTestUpdate(CONTROLLER_BASE, array, 2, controllerClass);
    assertEquals("testing array", updatedArray.getAlias());
  }

  @Test
  public void testRemoveSample() throws Exception {
    Array updatedArray = currentSession().find(controllerClass, 1);
    assertNotNull(updatedArray.getSample("R01C01"));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content("[{\"position\":\"R01C01\",\"searchString\":null}]")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    updatedArray = currentSession().find(controllerClass, 1);
    assertNull(updatedArray.getSample("R01C01"));
  }

  @Test
  public void testAddSample() throws Exception {
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions/R02C01").param("sampleId", "9").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    Array updatedArray = currentSession().find(controllerClass, 1);
    SampleImpl addedSample = currentSession().find(SampleImpl.class, 9);
    assertEquals(updatedArray.getSample("R02C01"), addedSample);
  }

  @Test
  public void testBulkAddSamples() throws Exception {
    Array updatedArray = currentSession().find(controllerClass, 1);
    assertNull(updatedArray.getSample("R02C01"));
    assertNull(updatedArray.getSample("R03C01"));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJsonForGenericList(Arrays.asList(
                bulkUpdateItem("R02C01", "SAM9"),
                bulkUpdateItem("R03C01", "SAM11"))))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    updatedArray = currentSession().find(controllerClass, 1);
    assertEquals(currentSession().find(SampleImpl.class, 9), updatedArray.getSample("R02C01"));
    assertEquals(currentSession().find(SampleImpl.class, 11), updatedArray.getSample("R03C01"));
  }

  @Test
  public void testBulkUpdateSamples() throws Exception {
    Array updatedArray = currentSession().find(controllerClass, 1);
    assertEquals(currentSession().find(SampleImpl.class, 8), updatedArray.getSample("R01C01"));
    assertNull(updatedArray.getSample("R02C01"));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJsonForGenericList(Arrays.asList(
                bulkUpdateItem("R01C01", "SAM9"),
                bulkUpdateItem("R02C01", "SAM11"))))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    updatedArray = currentSession().find(controllerClass, 1);
    assertEquals(currentSession().find(SampleImpl.class, 9), updatedArray.getSample("R01C01"));
    assertEquals(currentSession().find(SampleImpl.class, 11), updatedArray.getSample("R02C01"));
  }

  @Test
  public void testBulkRemoveSamples() throws Exception {
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJsonForGenericList(Arrays.asList(
                bulkUpdateItem("R02C01", "SAM9"),
                bulkUpdateItem("R03C01", "SAM11"))))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    Array updatedArray = currentSession().find(controllerClass, 1);
    assertNotNull(updatedArray.getSample("R01C01"));
    assertNotNull(updatedArray.getSample("R02C01"));
    assertNotNull(updatedArray.getSample("R03C01"));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(makeJsonForGenericList(Arrays.asList(
                bulkUpdateItem("R01C01", null),
                bulkUpdateItem("R02C01", null),
                bulkUpdateItem("R03C01", null))))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    updatedArray = currentSession().find(controllerClass, 1);
    assertNull(updatedArray.getSample("R01C01"));
    assertNull(updatedArray.getSample("R02C01"));
    assertNull(updatedArray.getSample("R03C01"));
  }

  @Test
  public void testFindSamples() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/sample-search", "SAM8", List.of(8));
  }

  @Test
  public void testGetChangelog() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/changelog")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$[1].summary").value("SAM8 added to R01C01"))
        .andDo(print());

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArray() throws Exception {
    testBulkDelete(controllerClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 2, CONTROLLER_BASE);
  }

  private BulkUpdateRequestItem bulkUpdateItem(String position, String searchString) {
    BulkUpdateRequestItem item = new BulkUpdateRequestItem();
    item.setPosition(position);
    item.setSearchString(searchString);
    return item;
  }
}
