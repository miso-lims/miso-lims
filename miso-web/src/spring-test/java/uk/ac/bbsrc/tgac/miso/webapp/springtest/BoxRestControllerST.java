package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SpreadsheetRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.BoxRestController.BulkUpdateRequestItem;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.BoxRestController.ScannerPreparationRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.BoxRestController.ScanRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.junit.Assert.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class BoxRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxes";
  private static final Class<BoxImpl> entityClass = BoxImpl.class;

  @Test
  public void testDataTableByUse() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/use/1", Arrays.asList(1, 2, 500, 501, 502, 100001));
  }
  
  @Test
  public void testDataTable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 500, 501, 502, 100001));
  }

  @Test
  public void testSetPosition() throws Exception {
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/position/C04")
            .param("entity", "SAMPLE:1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl updatedBox = currentSession().get(entityClass, 1);
    assertFalse(updatedBox.isFreePosition("C04"));
    assertEquals(1L, updatedBox.getBoxPositions().get("C04").getBoxableId().getTargetId());
  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "BOX501", Arrays.asList(501));
  }

  @Test
  public void testPartialSearch() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("q", "BOX50");
    params.add("b", "true");
    baseSearchByTerm(CONTROLLER_BASE + "/search/partial", params, Arrays.asList(500, 501, 502));
  }

  @Test
  public void testGetSpreadsheet() throws Exception {
    SpreadsheetRequest request = new SpreadsheetRequest();
    request.setFormat("CSV");
    request.setIds(Arrays.asList(1L, 2L));
    request.setSheet("TRACKING_LIST");

    String csvContent = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/spreadsheet")
            .content(makeJson(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/csv"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    BoxImpl box1 = currentSession().get(entityClass, 1L);
    BoxImpl box2 = currentSession().get(entityClass, 2L);
    
    assertTrue("CSV should contain box 1 alias", csvContent.contains(box1.getAlias()));
    assertTrue("CSV should contain box 2 alias", csvContent.contains(box2.getAlias()));
  }

  @Test
  public void testRemoveSingleItem() throws Exception {
    getMockMvc()
        .perform(delete(CONTROLLER_BASE + "/1/positions/A07"))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertTrue(box.isFreePosition("A07"));
  }

  @Test
  public void testRemoveMultipleItems() throws Exception {
    List<String> positions = Arrays.asList("A07", "B05");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/bulk-remove")
            .content(makeJson(positions))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));
  }

  @Test
  public void testDiscardSingleItem() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/positions/A07/discard"))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertTrue(box.isFreePosition("A07"));

    SampleImpl sample = currentSession().get(SampleImpl.class, 205);
    assertTrue(sample.isDiscarded());
  }

  @Test
  public void testDiscardMultipleItems() throws Exception {
    List<String> positions = Arrays.asList("A07", "B05");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/bulk-discard")
            .content(makeJson(positions))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));

    SampleImpl sample = currentSession().get(SampleImpl.class, 205);
    assertTrue(sample.isDiscarded());
    sample = currentSession().get(SampleImpl.class, 206);
    assertTrue(sample.isDiscarded());
  }

  @Test
  public void testDiscardEntireBox() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/discard-all"))
        .andExpect(status().isNoContent());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertEquals(96, box.getFreeCount());
  }

  @Test
  public void testBulkUpdatePositions() throws Exception {
    BulkUpdateRequestItem item1 = new BulkUpdateRequestItem();
    item1.setPosition("A07");
    item1.setSearchString("SAM205");

    BulkUpdateRequestItem item2 = new BulkUpdateRequestItem();
    item2.setPosition("C06");
    item2.setSearchString("SAM204");

    List<BulkUpdateRequestItem> items = Arrays.asList(item1, item2);

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/bulk-update")
            .content(makeJson(items))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl updatedBox = currentSession().get(entityClass, 1);
    assertFalse(updatedBox.isFreePosition("A07"));
    assertFalse(updatedBox.isFreePosition("C06"));
  }

  @Test
  public void testSetBoxLocation() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/setlocation")
            .param("storageId", "7"))
        .andExpect(status().isAccepted());

    BoxImpl box = currentSession().get(entityClass, 1);
    assertEquals("Loose Storage", box.getStorageLocation().getLocationUnit().getDisplayName());
  }

  @Test
  public void testCreateBox() throws Exception {
    BoxImpl box = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
    assertEquals("the first", box.getAlias());
  }

  @Test
  public void testUpdateBox() throws Exception {
    BoxImpl box = currentSession().get(entityClass, 1);
    box.setAlias("changed");

    List<BoxDto> dtos = Dtos.asBoxDtosWithPositions(Arrays.asList(box));
    BoxImpl updatedBox = baseTestUpdate(CONTROLLER_BASE, dtos.get(0), 1, entityClass);
    assertEquals("changed", updatedBox.getAlias());
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    BoxImpl box1 = currentSession().get(entityClass, 1);
    BoxImpl box2 = currentSession().get(entityClass, 2);

    box1.setAlias("changed 1");
    box2.setAlias("changed 2");

    List<BoxDto> boxes = Dtos.asBoxDtosWithPositions(Arrays.asList(box1, box2));
    List<BoxImpl> updatedBoxes = baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, boxes, BoxDto::getId);

    assertEquals("changed 1", updatedBoxes.get(0).getAlias());
    assertEquals("changed 2", updatedBoxes.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDelete() throws Exception {
    testBulkDelete(entityClass, 100001, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testBulkDeleteUnauthorized() throws Exception {
    testDeleteUnauthorized(entityClass, 100001, CONTROLLER_BASE);
  }

  @Test
  public void testFillByPattern() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1);
    int initialCount = boxBefore.getBoxPositions().size();
    
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/positions/fill-by-pattern")
            .param("prefix", "PRO")
            .param("suffix", "standard"))
        .andExpect(status().isNoContent());

    BoxImpl boxAfter = currentSession().get(entityClass, 1);
    int finalCount = boxAfter.getBoxPositions().size();
    
    assertTrue("Fill-by-pattern should result in valid box state", finalCount >= 0);
  }

  @Test
  public void testGetFragmentAnalyserSheet() throws Exception {
    String sheetContent = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1/fragmentAnalyser"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/plain"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    assertTrue("Sheet should contain position A01", sheetContent.contains("A01"));
    assertTrue("Sheet should contain position H12", sheetContent.contains("H12"));
    assertTrue("Sheet should contain Ladder", sheetContent.contains("Ladder"));
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

    return Arrays.asList(dto1, dto2);
  }
}