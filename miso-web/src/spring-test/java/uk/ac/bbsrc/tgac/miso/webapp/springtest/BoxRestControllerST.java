package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.FileReader;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.BeforeClass;
import java.io.FileOutputStream;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Box;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;

import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.BoxRestController.*;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate.*;
import uk.ac.bbsrc.tgac.miso.webapp.springtest.utils.visionmateSrc.ca.on.oicr.gsi.visionmate.mockServer.*;
import java.io.IOException;



public class BoxRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxes";
  private static final Class<BoxImpl> controllerClass = BoxImpl.class;
  // 24 endpoints to test
  // do the auxiliary box ones before this one

  @Test
  public void testDatatable() throws Exception {
    checkIds(performDtRequest(CONTROLLER_BASE + "/dt")
        .andExpect(jsonPath("$.iTotalRecords").value(6)),
        Arrays.asList(1,2,500, 501,502,100001),
        true);
  }

  @Test
  public void testDatatableByUse() throws Exception {
    checkIds(performDtRequest(CONTROLLER_BASE + "/dt/use/2")
        .andExpect(jsonPath("$.iTotalRecords").value(1)),
        Arrays.asList(502),
        true);
  }


  @Test
  public void testSetPosition() throws Exception {
    // should this test invalid/already filled position as well?
    // WIP

    // currently getting "Invalid URL" --- not setting it right

    //just gonna try it without the param
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/position/C04").param("entity", "SAMPLE:1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl updatedBox = currentSession().get(controllerClass, 1);
    assertFalse(updatedBox.isFreePosition("C04"));
    // check the position here to see if it's filled with sample 1

  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", searchTerm("BOX501"), 1, Arrays.asList(501));
  }

  @Test
  public void testPartialSearch() throws Exception {
    MultiValueMap<String, String> map = searchTerm("BOX50");
    map.add("b", "true");
    baseSearchByTerm(CONTROLLER_BASE + "/search/partial", map, 3, Arrays.asList(500, 501, 502));
  }


  @Test
  public void testCreateSpreadsheet() throws Exception {
    // I think this just returns a hashcode

    // currently getting "failed to get contents form"" idk why
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/spreadsheet"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hashCode", notNullValue(Integer.class)));
  }

  @Test
  public void testGetSpreadSheet() throws Exception {


    SpreadsheetRequest req = new SpreadsheetRequest();
    req.setFormat("CSV");
    List<Long> ids = new ArrayList<Long>();
    ids.add(1L);
    ids.add(2L);
    req.setIds(ids);
    req.setSheet("TRACKING_LIST");


    MockHttpServletResponse response = getMockMvc().perform(post(CONTROLLER_BASE + "/spreadsheet").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/csv")).andReturn().getResponse();

    String filename = response.getHeader("Content-Disposition").split("=")[1];
    List<List<String>> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      int row = 0;
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        records.add(Arrays.asList(values));
        switch (row) {
          case 0:
            assertEquals(values[0], "Name");
            break;
          
          case 1:
            assertEquals(values[0], "BOX1");
            break;
          
          case 2:
            assertEquals(values[0], "BOX2");
            break;
        }
      }
    }
    catch(Exception e) {}
  }


  @Test
  public void testPrepareBoxScanner() throws Exception {

    ScannerPreparationRequest req = new ScannerPreparationRequest();
    req.setColumns(12);
    req.setRows(8);
    req.setScannerName("Lab 1 Scanner"); // I think this will work but idk, this seems like the localhost scanner
    // ask for help later if needed
    // DP5MIRAGE_SCANNER
    getMockMvc().perform(post(CONTROLLER_BASE + "/prepare-scan").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
    .andDo(print())
        .andExpect(status().isNoContent());
  }

  @BeforeClass
  public static void setupScanner() throws Exception {
      MockScannerServer server = new MockScannerServer();
    new Thread(server).start();
    
    VisionMateClient client = new VisionMateClient("127.0.0.1", 8000);
    try {
      client.connect();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to connect");
    }
  }


  @Test
  public void testGetBoxScan() throws Exception {
    // not sure if the scanner name will work

    // Invalid scanner specified
    // WHAT ARE THE VALID BOX SCANNER NAMES

    ScanRequest req = new ScanRequest();
    req.setScannerName("Lab 1 Scanner");
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/scan").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void testRemoveSingle() throws Exception {
    getMockMvc().perform(delete(CONTROLLER_BASE + "/1/positions/A07")).andExpect(status().isOk());

    BoxImpl box = currentSession().get(controllerClass, 1);
    assertTrue(box.isFreePosition("A07"));
  }


  @Test
  public void testRemoveMultiple() throws Exception {

    List<String> positions = new ArrayList<String>();
    positions.add("A07");
    positions.add("B05");
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/bulk-remove").content(makeJson(positions)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(controllerClass, 1);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));
  }

  @Test
  public void testDiscardSingle() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/positions/A07/discard")).andExpect(status().isOk());

    BoxImpl box = currentSession().get(controllerClass, 1);
    assertTrue(box.isFreePosition("A07"));

    SampleImpl sample = currentSession().get(SampleImpl.class, 205);
    assertTrue(sample.isDiscarded());
    // check that the sample has discarded checked
  }

  @Test
  public void testDiscardMultiple() throws Exception {
    List<String> positions = new ArrayList<String>();
    positions.add("A07");
    positions.add("B05");
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/bulk-discard").content(makeJson(positions)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(controllerClass, 1);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));


    SampleImpl sample = currentSession().get(SampleImpl.class, 205);
    assertTrue(sample.isDiscarded());
    sample = currentSession().get(SampleImpl.class, 206);
    assertTrue(sample.isDiscarded());
  }

  @Test
  public void testDiscardAll() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/discard-all"))
    .andExpect(status().isNoContent());
    BoxImpl box = currentSession().get(controllerClass, 1);
    assertTrue(box.getFreeCount() == 96); // all 8 x 12 spots are empty
    // checking if each individual item was marked as discarded is not needed, as the other discard
    // tests cover that
  }

  @Test
  public void testBulkUpdatePositions() throws Exception {
    BulkUpdateRequestItem item1 = new BulkUpdateRequestItem();
    item1.setPosition("A07");
    item1.setSearchString("SAM205");

    BulkUpdateRequestItem item2 = new BulkUpdateRequestItem();
    item2.setSearchString("SAM204");
    item2.setPosition("C06");

    List<BulkUpdateRequestItem> items = new ArrayList<BulkUpdateRequestItem>();
    items.add(item1);
    items.add(item2);

    getMockMvc().perform(post(CONTROLLER_BASE + "/1/bulk-update").content(makeJson(items)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl updatedBox = currentSession().get(controllerClass, 1);
    assertFalse(updatedBox.isFreePosition("A07"));
    assertFalse(updatedBox.isFreePosition("C06"));

  }
  @Test
  public void testSetBoxLocation() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/setlocation").param("storageId", "7"))
    .andExpect(status().isAccepted());

    BoxImpl box = currentSession().get(controllerClass, 1);
    assertEquals("Loose Storage", box.getStorageLocation().getLocationUnit().getDisplayName());

  }

  @Test
  public void testCreateBox() throws Exception {
    BoxImpl box = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), controllerClass, 200);
    assertEquals("the first", box.getAlias()); // just a quick check that it went through fine
  }

  @Test
  public void testUpdateBox() throws Exception {
    BoxImpl box = currentSession().get(controllerClass, 1);
    box.setAlias("changed");
    BoxUse use = currentSession().get(BoxUse.class, 1);
    box.setUse(use);

    List<BoxDto> dtos =  Dtos.asBoxDtosWithPositions(Arrays.asList(box));
    BoxImpl updatedBox = baseTestUpdate(CONTROLLER_BASE, dtos.get(0), 1, controllerClass);
    assertEquals("changed", updatedBox.getAlias());
  }

  @Test
  public void testRecreateBoxFromPrefix() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/1/positions/fill-by-pattern").param("prefix", "PRO").param("suffix", "standard"))
    .andExpect(status().isNoContent());
  }

  @Test
  public void createFragmentAnalyserSheet() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/fragmentAnalyser"))
    .andExpect(status().isOk());
  }


  private List<BoxDto> makeCreateDtos() {
    BoxDto dto1 = new BoxDto();
    dto1.setSizeId(1L);
    dto1.setUseId(1L);
    dto1.setName("boxy one");
    dto1.setAlias("the first");

    BoxDto dto2 = new BoxDto();
    dto2.setSizeId(1L);
    dto2.setUseId(1L);
    dto2.setName("boxy two");
    dto2.setAlias("the second");

    List<BoxDto> dtos = new ArrayList<BoxDto>();
    dtos.add(dto1);
    dtos.add(dto2);
    return dtos;

  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    BoxImpl box1 = currentSession().get(controllerClass, 1);
    BoxImpl box2 = currentSession().get(controllerClass, 2);

    box1.setAlias("changed 1");
    box2.setAlias("changed 2");

    List<BoxDto> boxes = Dtos.asBoxDtosWithPositions(Arrays.asList(box1, box2));

    List<BoxImpl> updatedBoxes =
        (List<BoxImpl>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, boxes, Arrays.asList(1, 2));

    assertEquals("changed 1", updatedBoxes.get(0).getAlias());
    assertEquals("changed 2", updatedBoxes.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArray() throws Exception {
    testBulkDelete(controllerClass, 100001, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 100001, CONTROLLER_BASE);
  }



}
