package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertFieldValues;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
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
    assertNull(initial.getBoxable(position));

    // add the item
    visualization.selectPosition(position);
    visualization.lookupBarcode("TIB_SamStock");
    visualization.updatePosition(false);
    
    // confirm values post-add
    BoxPage again = getBoxPage(500L);
    assertFalse("checking that position is no longer empty", again.getVisualization().isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(box.getBoxable(position));
  }

  @Test
  public void testLookupBadBarcode() {
    final String position = "H12";

    // confirm values pre-lookup
    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(initial.getBoxable(position));

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();
    assertTrue("empty position is empty", visualization.isEmptyPosition(position));

    // do the lookup
    visualization.selectPosition(position);
    visualization.lookupBarcode("Bad Barcode");
    assertFalse(visualization.isUpdatePositionButtonClickable());

    // confirm nothing has changed
    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxable(position));
    
    BoxPage again = getBoxPage(500L);
    assertTrue("empty position is still empty", again.getVisualization().isEmptyPosition(position));
  }

  @Test
  public void testMoveItemWithinBox() {
    final String initialPosition = "F10";
    final String finalPosition = "F12";
    final String libraryBarcode = "TIB_Lib2";
    final String libraryAlias = "TIB_0001_nn_n_PE_505_WG";

    // confirm positions pre-move
    Box initial = (Box) getSession().get(BoxImpl.class, 500L);
    BoxableView itemAtInitialPosition = initial.getBoxable(initialPosition);
    assertNotNull(itemAtInitialPosition);
    assertNull(initial.getBoxable(finalPosition));
    assertTrue(libraryAlias.equals(itemAtInitialPosition.getAlias()));

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertFalse("checking that library is in position F10", visualization.isEmptyPosition(initialPosition));
    assertTrue("checking which library is in position F10",
        visualization.getPositionTitle(initialPosition).contains(libraryAlias));
    assertTrue("checking that no tube is in position F12", visualization.isEmptyPosition(finalPosition));

    visualization.selectPosition(finalPosition);
    visualization.lookupBarcode(libraryBarcode);
    visualization.updatePosition(false);

    // confirm positions post-move
    Box updated = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(updated.getBoxable(initialPosition));
    BoxableView updatedAtFinalPosition = updated.getBoxable(finalPosition);
    assertNotNull(updatedAtFinalPosition);
    assertTrue(libraryAlias.equals(updatedAtFinalPosition.getAlias()));

    BoxPage afterSave = getBoxPage(500L);
    BoxVisualization afterVisualization = afterSave.getVisualization();

    assertTrue("checking that no tube is in position F10", afterVisualization.isEmptyPosition(initialPosition));
    assertFalse("checking that library is in position F12", afterVisualization.isEmptyPosition(finalPosition));
    assertTrue("checking which library is in position F12",
        afterVisualization.getPositionTitle(finalPosition).contains(libraryAlias));
  }

  @Test
  public void testMoveItemBetweenBoxes() {
    final String position = "A01";
    final String barcode = "TIB_SamTissue";

    // confirm values pre-save
    Box firstBox = (Box) getSession().get(BoxImpl.class, 500L);
    assertNotNull(firstBox.getBoxable(position));
    Box secondBox = (Box) getSession().get(BoxImpl.class, 501L);
    assertNull(secondBox.getBoxable(position));

    BoxPage firstPage = getBoxPage(500L);
    BoxVisualization firstVis = firstPage.getVisualization();
    assertFalse("tissue is in position 500-A01", firstVis.isEmptyPosition(position));

    BoxPage secondPage = getBoxPage(501L);
    BoxVisualization secondVisualization = secondPage.getVisualization();
    assertTrue("position 501-A01 is empty", secondVisualization.isEmptyPosition(position));

    // move the item from one box to the next
    secondVisualization.selectPosition(position);
    secondVisualization.lookupBarcode(barcode);
    secondVisualization.updatePosition(false);

    // confirm values post-move
    Box first = (Box) getSession().get(BoxImpl.class, 500L);
    Box second = (Box) getSession().get(BoxImpl.class, 501L);
    assertNotNull(second.getBoxable(position));
    assertNull(first.getBoxable(position));

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
    assertNotNull(initial.getBoxable(position));

    // remove the tube
    visualization.selectPosition(position);
    visualization.removeTube();

    // confirm values post-removal
    BoxPage newPage = getBoxPage(500L);
    BoxVisualization newVisualization = newPage.getVisualization();
    assertTrue(" check that position B01 is now empty", newVisualization.isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxable(position));
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
    assertNotNull(initial.getBoxable(position));

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
    assertNull(box.getBoxable(position));

    LibraryDilution boxable = (LibraryDilution) getSession().get(LibraryDilution.class, 504L);
    assertTrue("check that boxable is discarded", boxable.isDiscarded());
    assertTrue("check that boxable volume is null", boxable.getVolume() == null);
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
    assertTrue("check that D01 is a pool", initial.getBoxable(position).getName().startsWith("IPO"));

    // replace the tube
    visualization.selectPosition(position);
    visualization.lookupBarcode("TIB_replaceDil");
    visualization.updatePosition(true);

    // assert values post-replace
    BoxPage post = getBoxPage(500L);
    BoxVisualization postVis = post.getVisualization();
    assertFalse("check that position D01 is still full", postVis.isEmptyPosition(position));
    assertTrue("check that D01 is now a dilution", postVis.getPositionTitle(position).contains("LDI"));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertTrue("check that D01 now contains dilution", box.getBoxable(position).getName().startsWith("LDI"));
  }

}
