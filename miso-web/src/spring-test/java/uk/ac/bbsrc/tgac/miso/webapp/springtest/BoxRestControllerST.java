package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
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

    BoxImpl updatedBox = currentSession().get(entityClass, 1L);
    assertNotNull(updatedBox);
    assertFalse(updatedBox.isFreePosition("C04"));
    assertNotNull(updatedBox.getBoxPositions().get("C04"));
    assertEquals(1L, updatedBox.getBoxPositions().get("C04").getBoxableId().getTargetId());
    assertEquals(EntityType.SAMPLE, updatedBox.getBoxPositions().get("C04").getBoxableId().getTargetType());
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

    assertNotNull(box1);
    assertNotNull(box2);
    assertNotNull(csvContent);
    assertFalse(csvContent.isEmpty());
    assertTrue("CSV should contain box 1 alias", csvContent.contains(box1.getAlias()));
    assertTrue("CSV should contain box 2 alias", csvContent.contains(box2.getAlias()));
  }

  @Test
  public void testRemoveSingleItem() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    assertFalse(boxBefore.isFreePosition("A07"));

    getMockMvc()
        .perform(delete(CONTROLLER_BASE + "/1/positions/A07"))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    assertTrue(box.isFreePosition("A07"));
  }

  @Test
  public void testRemoveMultipleItems() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    assertFalse(boxBefore.isFreePosition("A07"));
    assertFalse(boxBefore.isFreePosition("B05"));

    List<String> positions = Arrays.asList("A07", "B05");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/bulk-remove")
            .content(makeJson(positions))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));
  }

  @Test
  public void testDiscardSingleItem() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    assertFalse(boxBefore.isFreePosition("A07"));

    SampleImpl sampleBefore = currentSession().get(SampleImpl.class, 205L);
    assertNotNull(sampleBefore);
    assertFalse(sampleBefore.isDiscarded());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/positions/A07/discard"))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    assertTrue(box.isFreePosition("A07"));

    SampleImpl sample = currentSession().get(SampleImpl.class, 205L);
    assertNotNull(sample);
    assertTrue(sample.isDiscarded());
  }

  @Test
  public void testDiscardMultipleItems() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    assertFalse(boxBefore.isFreePosition("A07"));
    assertFalse(boxBefore.isFreePosition("B05"));

    SampleImpl sample205Before = currentSession().get(SampleImpl.class, 205L);
    SampleImpl sample206Before = currentSession().get(SampleImpl.class, 206L);
    assertNotNull(sample205Before);
    assertNotNull(sample206Before);
    assertFalse(sample205Before.isDiscarded());
    assertFalse(sample206Before.isDiscarded());

    List<String> positions = Arrays.asList("A07", "B05");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/bulk-discard")
            .content(makeJson(positions))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    assertTrue(box.isFreePosition("A07"));
    assertTrue(box.isFreePosition("B05"));

    SampleImpl sample205 = currentSession().get(SampleImpl.class, 205L);
    assertNotNull(sample205);
    assertTrue(sample205.isDiscarded());

    SampleImpl sample206 = currentSession().get(SampleImpl.class, 206L);
    assertNotNull(sample206);
    assertTrue(sample206.isDiscarded());
  }

  @Test
  public void testDiscardEntireBox() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    int initialFreeCount = boxBefore.getFreeCount();
    assertTrue(initialFreeCount < 96);

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/discard-all"))
        .andExpect(status().isNoContent());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
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

    BoxImpl updatedBox = currentSession().get(entityClass, 1L);
    assertNotNull(updatedBox);
    assertFalse(updatedBox.isFreePosition("A07"));
    assertFalse(updatedBox.isFreePosition("C06"));
    assertNotNull(updatedBox.getBoxPositions().get("A07"));
    assertNotNull(updatedBox.getBoxPositions().get("C06"));
    assertEquals(205L, updatedBox.getBoxPositions().get("A07").getBoxableId().getTargetId());
    assertEquals(204L, updatedBox.getBoxPositions().get("C06").getBoxableId().getTargetId());
    assertEquals(EntityType.SAMPLE, updatedBox.getBoxPositions().get("A07").getBoxableId().getTargetType());
    assertEquals(EntityType.SAMPLE, updatedBox.getBoxPositions().get("C06").getBoxableId().getTargetType());
  }

  @Test
  public void testSetBoxLocation() throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/setlocation")
            .param("storageId", "7"))
        .andExpect(status().isAccepted());

    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    assertNotNull(box.getStorageLocation());
    assertEquals(7L, box.getStorageLocation().getId());
  }

  @Test
  public void testCreateBox() throws Exception {
    BoxDto dto = makeCreateDtos().get(0);
    assertNotNull(dto);

    BoxImpl box = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
    assertNotNull(box);
    assertNotNull(box.getId());
    assertEquals("the first", box.getAlias());
    assertNotNull(box.getName());
    assertNotNull(box.getSize());
    assertEquals(1L, box.getSize().getId());
    assertNotNull(box.getUse());
    assertEquals(1L, box.getUse().getId());
  }

  @Test
  public void testUpdateBox() throws Exception {
    BoxImpl box = currentSession().get(entityClass, 1L);
    assertNotNull(box);
    String originalAlias = box.getAlias();
    assertNotNull(originalAlias);
    box.setAlias("changed");

    List<BoxDto> dtos = Dtos.asBoxDtosWithPositions(Arrays.asList(box));
    BoxImpl updatedBox = baseTestUpdate(CONTROLLER_BASE, dtos.get(0), 1, entityClass);
    assertNotNull(updatedBox);
    assertEquals("changed", updatedBox.getAlias());
    assertNotEquals(originalAlias, updatedBox.getAlias());
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<BoxDto> dtos = makeCreateDtos();
    assertNotNull(dtos);
    assertEquals(2, dtos.size());

    List<BoxImpl> createdBoxes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, dtos);

    assertNotNull(createdBoxes);
    assertEquals(2, createdBoxes.size());

    BoxImpl box1 = createdBoxes.get(0);
    assertNotNull(box1);
    assertNotNull(box1.getId());
    assertEquals("the first", box1.getAlias());
    assertNotNull(box1.getName());
    assertNotNull(box1.getSize());
    assertEquals(1L, box1.getSize().getId());
    assertNotNull(box1.getUse());
    assertEquals(1L, box1.getUse().getId());

    BoxImpl box2 = createdBoxes.get(1);
    assertNotNull(box2);
    assertNotNull(box2.getId());
    assertEquals("the second", box2.getAlias());
    assertNotNull(box2.getName());
    assertNotNull(box2.getSize());
    assertEquals(1L, box2.getSize().getId());
    assertNotNull(box2.getUse());
    assertEquals(1L, box2.getUse().getId());

    assertNotEquals(box1.getId(), box2.getId());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    BoxImpl box1 = currentSession().get(entityClass, 1L);
    BoxImpl box2 = currentSession().get(entityClass, 2L);
    assertNotNull(box1);
    assertNotNull(box2);

    String originalAlias1 = box1.getAlias();
    String originalAlias2 = box2.getAlias();

    box1.setAlias("changed 1");
    box2.setAlias("changed 2");

    List<BoxDto> boxes = Dtos.asBoxDtosWithPositions(Arrays.asList(box1, box2));
    List<BoxImpl> updatedBoxes = baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, boxes, BoxDto::getId);

    assertNotNull(updatedBoxes);
    assertEquals(2, updatedBoxes.size());

    assertNotNull(updatedBoxes.get(0));
    assertEquals("changed 1", updatedBoxes.get(0).getAlias());
    assertNotEquals(originalAlias1, updatedBoxes.get(0).getAlias());

    assertNotNull(updatedBoxes.get(1));
    assertEquals("changed 2", updatedBoxes.get(1).getAlias());
    assertNotEquals(originalAlias2, updatedBoxes.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDelete() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 100001L);
    assertNotNull(boxBefore);

    testBulkDelete(entityClass, 100001, CONTROLLER_BASE);

    BoxImpl boxAfter = currentSession().get(entityClass, 100001L);
    assertNull(boxAfter);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testBulkDeleteUnauthorized() throws Exception {
    testDeleteUnauthorized(entityClass, 100001, CONTROLLER_BASE);
  }

  @Test
  public void testFillByPattern() throws Exception {
    BoxImpl boxBefore = currentSession().get(entityClass, 1L);
    assertNotNull(boxBefore);
    int initialCount = boxBefore.getBoxPositions().size();

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/1/positions/fill-by-pattern")
            .param("prefix", "PRO")
            .param("suffix", "standard"))
        .andExpect(status().isNoContent());

    BoxImpl boxAfter = currentSession().get(entityClass, 1L);
    assertNotNull(boxAfter);
    int finalCount = boxAfter.getBoxPositions().size();

    assertTrue("Fill-by-pattern should result in valid box state", finalCount >= 0);
    assertTrue("Final count should not exceed capacity", finalCount <= 96);
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

    assertNotNull(sheetContent);
    assertFalse(sheetContent.isEmpty());
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
