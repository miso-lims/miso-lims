package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertFieldValues;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BoxPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BoxPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BoxVisualization;

public class BoxPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  private BoxPage getBoxPage(Long boxId) {
    return BoxPage.get(getDriver(), getBaseUrl(), boxId);
  }

  @Test
  public void testSaveNewBox() {
    // Goal: save one new box
    BoxPage page = getBoxPage(null);
    assertNotNull(page);

    Map<Field, String> unsaved = new HashMap<>();
    unsaved.put(Field.ALIAS, "New Box");
    unsaved.put(Field.BARCODE, "newbarcode");
    unsaved.put(Field.DESCRIPTION, "Test");
    unsaved.put(Field.USE, "Storage");
    unsaved.put(Field.SIZE, "10 × 10");
    unsaved.put(Field.LOCATION, "Here");
    page.setFields(unsaved);

    assertEquals("Box ID is unsaved", "Unsaved", page.getField(Field.ID));
    assertFieldValues("changes pre-save", unsaved, page);
    BoxPage savedPage = page.clickSave();
    assertFieldValues("changes post-save", unsaved, savedPage);

    Box box = (Box) getSession().get(BoxImpl.class, Long.valueOf(savedPage.getField(Field.ID)));
    assertNotEquals("Box ID is now a number", "Unsaved", box.getId());
    assertEquals("confirm box alias", unsaved.get(Field.ALIAS), box.getAlias());
    assertEquals("confirm box barcode", unsaved.get(Field.BARCODE), box.getIdentificationBarcode());
    assertEquals("confirm box description", unsaved.get(Field.DESCRIPTION), box.getDescription());
    assertEquals("confirm box use", unsaved.get(Field.USE), box.getUse().getAlias());
    assertEquals("confirm box size", unsaved.get(Field.SIZE), box.getSize().getRowsByColumns());
    assertEquals("confirm box location", unsaved.get(Field.LOCATION), box.getLocationBarcode());
  }

  @Test
  public void testEditBox() {
    BoxPage page = getBoxPage(502L);
    assertNotNull(page);

    Map<Field, String> changed = new HashMap<>();
    changed.put(Field.ALIAS, "Changed alias");
    changed.put(Field.ALIAS, "Changed Box");
    changed.put(Field.BARCODE, "Changed Barcode");
    changed.put(Field.DESCRIPTION, "Changed Description");
    changed.put(Field.USE, "Libraries");
    changed.put(Field.LOCATION, "Changed Location");
    page.setFields(changed);

    assertFieldValues("pre-save changes", changed, page);
    BoxPage savedPage = page.clickSave();
    assertFieldValues("post-save changes", changed, savedPage);

    Box box = (Box) getSession().get(BoxImpl.class, 502L);
    assertEquals(changed.get(Field.ALIAS), box.getAlias());
    assertEquals(changed.get(Field.BARCODE), box.getIdentificationBarcode());
    assertEquals(changed.get(Field.DESCRIPTION), box.getDescription());
    assertEquals(changed.get(Field.USE), box.getUse().getAlias());
    assertEquals(changed.get(Field.LOCATION), box.getLocationBarcode());
  }

  @Test
  public void testAddOneItemToBox() {
    final String position = "F06";

    // confirm values pre-add
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertTrue("empty position is empty", visualization.isEmptyPosition(position));

    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(initial.getBoxPositions().get(position));

    // add the item
    visualization.selectPosition(position);
    visualization.searchBoxables("TIB_SamStock");
    visualization.updatePosition(false);
    
    // confirm values post-add
    BoxPage again = getBoxPage(500L);
    assertFalse("checking that position is no longer empty", again.getVisualization().isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(box.getBoxPositions().get(position));
  }

  @Test
  public void testLookupBadBarcode() {
    final String position = "H12";

    // confirm values pre-lookup
    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(initial.getBoxPositions().get(position));

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertTrue("empty position is empty", visualization.isEmptyPosition(position));

    // do the lookup
    visualization.selectPosition(position);
    visualization.searchBoxables("Bad Barcode");
    assertFalse(visualization.isUpdatePositionButtonClickable());

    // confirm nothing has changed
    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxPositions().get(position));
    
    BoxPage again = getBoxPage(500L);
    assertTrue("empty position is still empty", again.getVisualization().isEmptyPosition(position));
  }

  @Test
  public void testMoveItemWithinBox() {
    final String initialPosition = "F10";
    final String finalPosition = "F12";
    Library lib = (Library) getSession().get(LibraryImpl.class, 505L);
    assertNotNull(lib);
    BoxableId libBoxableId = new BoxableId(lib.getEntityType(), lib.getId());

    // confirm positions pre-move
    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    BoxPosition itemAtInitialPosition = initial.getBoxPositions().get(initialPosition);
    assertNotNull(itemAtInitialPosition);
    assertNull(initial.getBoxPositions().get(finalPosition));
    assertEquals(libBoxableId, itemAtInitialPosition.getBoxableId());

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertFalse("checking that library is in position F10", visualization.isEmptyPosition(initialPosition));
    assertTrue("checking which library is in position F10",
        visualization.getPositionTitle(initialPosition).contains(lib.getAlias()));
    assertTrue("checking that no tube is in position F12", visualization.isEmptyPosition(finalPosition));

    visualization.selectPosition(finalPosition);
    visualization.searchBoxables(lib.getIdentificationBarcode());
    visualization.updatePosition(false);

    // confirm positions post-move
    Box updated = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(updated.getBoxPositions().get(initialPosition));
    BoxPosition updatedAtFinalPosition = updated.getBoxPositions().get(finalPosition);
    assertNotNull(updatedAtFinalPosition);
    assertEquals(libBoxableId, updatedAtFinalPosition.getBoxableId());

    BoxPage afterSave = getBoxPage(500L);
    BoxVisualization afterVisualization = afterSave.getVisualization();

    assertTrue("checking that no tube is in position F10", afterVisualization.isEmptyPosition(initialPosition));
    assertFalse("checking that library is in position F12", afterVisualization.isEmptyPosition(finalPosition));
    assertTrue("checking which library is in position F12",
        afterVisualization.getPositionTitle(finalPosition).contains(lib.getAlias()));
  }

  @Test
  public void testMoveItemBetweenBoxes() {
    final String position = "A01";
    final String barcode = "TIB_SamTissue";

    // confirm values pre-save
    Box firstBox = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(firstBox.getBoxPositions().get(position));
    Box secondBox = (Box) getSession().get(BoxImpl.class, 501L);
    assertNull(secondBox.getBoxPositions().get(position));

    BoxPage firstPage = getBoxPage(500L);
    BoxVisualization firstVis = firstPage.getVisualization();
    assertFalse("tissue is in position 500-A01", firstVis.isEmptyPosition(position));

    BoxPage secondPage = getBoxPage(501L);
    BoxVisualization secondVisualization = secondPage.getVisualization();
    assertTrue("position 501-A01 is empty", secondVisualization.isEmptyPosition(position));

    // move the item from one box to the next
    secondVisualization.selectPosition(position);
    secondVisualization.searchBoxables(barcode);
    secondVisualization.updatePosition(false);

    // confirm values post-move
    Box first = (Box) getSession().get(BoxImpl.class, 500L);
    Box second = (Box) getSession().get(BoxImpl.class, 501L);
    assertNotNull(second.getBoxPositions().get(position));
    assertNull(first.getBoxPositions().get(position));

    BoxPage firstAgain = getBoxPage(500L);
    assertTrue("check that position 500-A01 is empty", firstAgain.getVisualization().isEmptyPosition(position));

    BoxPage secondAgain = getBoxPage(501L);
    assertFalse("check that tissue is in position 501-A01", secondAgain.getVisualization().isEmptyPosition(position));
  }

  @Test
  public void testRemoveTube() {
    final String position = "B01";

    // confirm values pre-save
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertFalse("check that position B01 is full", visualization.isEmptyPosition(position));

    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(initial.getBoxPositions().get(position));

    // remove the tube
    visualization.selectPosition(position);
    visualization.removeTube();

    // confirm values post-removal
    BoxPage newPage = getBoxPage(500L);
    BoxVisualization newVisualization = newPage.getVisualization();
    assertTrue(" check that position B01 is now empty", newVisualization.isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxPositions().get(position));
  }

  @Test
  public void testDiscardTube() {
    final String position = "C01";
    
    // confirm values pre-save
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertFalse("check that position C01 is full", visualization.isEmptyPosition(position));
    assertTrue("check that title matches LDI name", visualization.getPositionTitle(position).contains("LDI504"));

    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(initial.getBoxPositions().get(position));

    LibraryDilution initialLD = (LibraryDilution) getSession().get(LibraryDilution.class, 504L);
    assertFalse("check that boxable is not discarded", initialLD.isDiscarded());
    assertFalse("check that boxable location is not EMPTY", "EMPTY".equals(BoxUtils.makeLocationLabel(initialLD)));

    // discard the tube
    visualization.selectPosition(position);
    visualization.discardTube();

    // confirm values post-discard
    BoxPage newPage = getBoxPage(500L);
    BoxVisualization newVisualization = newPage.getVisualization();
    assertTrue("check that position C01 is now empty", newVisualization.isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxPositions().get(position));

    LibraryDilution boxable = (LibraryDilution) getSession().get(LibraryDilution.class, 504L);
    assertTrue("check that boxable is discarded", boxable.isDiscarded());
    assertTrue("check that boxable volume is null", boxable.getVolume().equals(Double.valueOf(0D)));
    assertEquals("check that boxable location is empty", "EMPTY", BoxUtils.makeLocationLabel(boxable));
  }

  @Test
  public void testReplaceTube() {
    final String position = "D01";

    // assert values pre-replace
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertFalse("check that position D01 is full", visualization.isEmptyPosition(position));
    assertTrue("check that D01 contains pool", visualization.getPositionTitle(position).contains("Pool"));

    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertEquals("check that D01 is a pool", EntityType.POOL, initial.getBoxPositions().get(position).getBoxableId().getTargetType());

    // replace the tube
    visualization.selectPosition(position);
    visualization.searchBoxables("TIB_replaceDil");
    visualization.updatePosition(true);

    // assert values post-replace
    BoxPage post = getBoxPage(500L);
    BoxVisualization postVis = post.getVisualization();
    assertFalse("check that position D01 is still full", postVis.isEmptyPosition(position));
    assertTrue("check that D01 is now a dilution", postVis.getPositionTitle(position).contains("LDI"));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertEquals("check that D01 now contains dilution", EntityType.DILUTION,
        box.getBoxPositions().get(position).getBoxableId().getTargetType());
  }

  @Test
  public void testAddMultipleTubes() {
    Library lib1 = (Library) getSession().get(LibraryImpl.class, 100001L);
    Library lib2 = (Library) getSession().get(LibraryImpl.class, 100002L);
    Library lib3 = (Library) getSession().get(LibraryImpl.class, 100003L);

    BoxPage page = getBoxPage(1L);
    BoxVisualization visualization = page.getVisualization();

    assertNull(lib1.getBox());
    assertNull(lib2.getBox());
    assertNull(lib3.getBox());

    assertTrue(visualization.isEmptyPosition("H10"));
    assertTrue(visualization.isEmptyPosition("H11"));
    assertTrue(visualization.isEmptyPosition("H12"));

    Map<String, String> updates = new HashMap<>();
    updates.put("H10", lib1.getName());
    updates.put("H11", lib2.getName());
    updates.put("H12", lib3.getName());
    visualization.updatePositions(updates, false);

    Box box = (Box) getSession().get(BoxImpl.class, 1L);
    assertTrue(visualization.getPositionTitle("H10").contains(lib1.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib1.getId()), box.getBoxPositions().get("H10").getBoxableId());
    assertTrue(visualization.getPositionTitle("H11").contains(lib2.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib2.getId()), box.getBoxPositions().get("H11").getBoxableId());
    assertTrue(visualization.getPositionTitle("H12").contains(lib3.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib3.getId()), box.getBoxPositions().get("H12").getBoxableId());
  }

  @Test
  public void testMoveMultipleTubes() {
    Sample sam1 = (Sample) getSession().get(SampleImpl.class, 4L);
    Sample sam2 = (Sample) getSession().get(SampleImpl.class, 7L);
    Sample sam3 = (Sample) getSession().get(SampleImpl.class, 8L);

    BoxPage page = getBoxPage(1L);
    BoxVisualization visualization = page.getVisualization();

    assertTrue(visualization.getPositionTitle("F06").contains(sam1.getAlias()));
    assertTrue(visualization.getPositionTitle("G07").contains(sam2.getAlias()));
    assertTrue(visualization.getPositionTitle("H08").contains(sam3.getAlias()));

    assertTrue(visualization.isEmptyPosition("H10"));
    assertTrue(visualization.isEmptyPosition("H11"));
    assertTrue(visualization.isEmptyPosition("H12"));

    Map<String, String> updates = new HashMap<>();
    updates.put("H10", sam1.getIdentificationBarcode());
    updates.put("H11", sam2.getIdentificationBarcode());
    updates.put("H12", sam3.getIdentificationBarcode());
    visualization.updatePositions(updates, false);

    Box box = (Box) getSession().get(BoxImpl.class, 1L);
    assertTrue(visualization.isEmptyPosition("F06"));
    assertNull(box.getBoxPositions().get("F06"));
    assertTrue(visualization.isEmptyPosition("G07"));
    assertNull(box.getBoxPositions().get("G07"));
    assertTrue(visualization.isEmptyPosition("H08"));
    assertNull(box.getBoxPositions().get("H08"));

    assertTrue(visualization.getPositionTitle("H10").contains(sam1.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam1.getId()), box.getBoxPositions().get("H10").getBoxableId());
    assertTrue(visualization.getPositionTitle("H11").contains(sam2.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam2.getId()), box.getBoxPositions().get("H11").getBoxableId());
    assertTrue(visualization.getPositionTitle("H12").contains(sam3.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam3.getId()), box.getBoxPositions().get("H12").getBoxableId());
  }

  @Test
  public void replaceMultipleTubes() {
    Library lib1 = (Library) getSession().get(LibraryImpl.class, 100001L);
    Library lib2 = (Library) getSession().get(LibraryImpl.class, 100002L);
    Library lib3 = (Library) getSession().get(LibraryImpl.class, 100003L);

    BoxPage page = getBoxPage(1L);
    BoxVisualization visualization = page.getVisualization();

    assertNull(lib1.getBox());
    assertNull(lib2.getBox());
    assertNull(lib3.getBox());

    assertFalse(visualization.isEmptyPosition("F06"));
    assertFalse(visualization.isEmptyPosition("G07"));
    assertFalse(visualization.isEmptyPosition("H08"));

    Map<String, String> updates = new HashMap<>();
    updates.put("F06", lib1.getName());
    updates.put("G07", lib2.getName());
    updates.put("H08", lib3.getName());
    visualization.updatePositions(updates, true);

    Box box = (Box) getSession().get(BoxImpl.class, 1L);
    assertTrue(visualization.getPositionTitle("F06").contains(lib1.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib1.getId()), box.getBoxPositions().get("F06").getBoxableId());
    assertTrue(visualization.getPositionTitle("G07").contains(lib2.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib2.getId()), box.getBoxPositions().get("G07").getBoxableId());
    assertTrue(visualization.getPositionTitle("H08").contains(lib3.getAlias()));
    assertEquals(new BoxableId(EntityType.LIBRARY, lib3.getId()), box.getBoxPositions().get("H08").getBoxableId());
  }

  @Test
  public void rearrangeMultipleTubes() {
    Sample sam1 = (Sample) getSession().get(SampleImpl.class, 4L);
    Sample sam2 = (Sample) getSession().get(SampleImpl.class, 7L);
    Sample sam3 = (Sample) getSession().get(SampleImpl.class, 8L);

    BoxPage page = getBoxPage(1L);
    BoxVisualization visualization = page.getVisualization();

    assertTrue(visualization.getPositionTitle("F06").contains(sam1.getAlias()));
    assertTrue(visualization.getPositionTitle("G07").contains(sam2.getAlias()));
    assertTrue(visualization.getPositionTitle("H08").contains(sam3.getAlias()));

    Map<String, String> updates = new HashMap<>();
    updates.put("G07", sam1.getIdentificationBarcode());
    updates.put("H08", sam2.getIdentificationBarcode());
    updates.put("F06", sam3.getIdentificationBarcode());
    visualization.updatePositions(updates, true);

    Box box = (Box) getSession().get(BoxImpl.class, 1L);
    assertTrue(visualization.getPositionTitle("G07").contains(sam1.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam1.getId()), box.getBoxPositions().get("G07").getBoxableId());
    assertTrue(visualization.getPositionTitle("H08").contains(sam2.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam2.getId()), box.getBoxPositions().get("H08").getBoxableId());
    assertTrue(visualization.getPositionTitle("F06").contains(sam3.getAlias()));
    assertEquals(new BoxableId(EntityType.SAMPLE, sam3.getId()), box.getBoxPositions().get("F06").getBoxableId());
  }

}
