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
    unsaved.put(Field.DESCRIPTION, "Test");
    unsaved.put(Field.USE, "Storage");
    unsaved.put(Field.SIZE, "10 × 10");
    unsaved.put(Field.LOCATION, "Here");
    page.setFields(unsaved);

    assertEquals("Box ID is unsaved", "Unsaved", page.getField(Field.ID));
    assertFieldValues("changes pre-save", unsaved, page);
    BoxPage savedPage = page.clickSave();
    // assert all except box size, which is no longer editable after save
    assertFieldValues("changes post-save", unsaved, savedPage);

    Box box = (Box) getSession().get(BoxImpl.class, Long.valueOf(savedPage.getField(Field.ID)));
    assertNotEquals("Box ID is now a number", "Unsaved", box.getId());
    assertEquals("confirm box alias", unsaved.get(Field.ALIAS), box.getAlias());
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
    changed.put(Field.DESCRIPTION, "Changed Description");
    changed.put(Field.USE, "Libraries");
    changed.put(Field.LOCATION, "Changed Location");
    page.setFields(changed);

    assertFieldValues("pre-save changes", changed, page);
    BoxPage savedPage = page.clickSave();
    assertFieldValues("post-save changes", changed, savedPage);

    Box box = (Box) getSession().get(BoxImpl.class, 502L);
    assertEquals(changed.get(Field.ALIAS), box.getAlias());
    assertEquals(changed.get(Field.DESCRIPTION), box.getDescription());
    assertEquals(changed.get(Field.USE), box.getUse().getAlias());
    assertEquals(changed.get(Field.LOCATION), box.getLocationBarcode());
  }

  @Test
  public void testAddOneItemToBox() {
    final String position = "F06";
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertTrue("empty position is empty", visualization.isEmptyPosition(position));
    visualization.selectPosition(position);
    visualization.lookupBarcode("TIB_SamStock");
    visualization.updatePosition(false);
    
    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    BoxableView itemAtPosition = box.getBoxable(position);
    assertNotNull(itemAtPosition);
  }

  @Test
  public void testLookupBadBarcode() {
    final String position = "H12";

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertTrue("empty position is empty", visualization.isEmptyPosition(position));
    visualization.selectPosition(position);
    visualization.lookupBarcode("Bad Barcode");
    assertFalse(visualization.isUpdatePositionButtonClickable());

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    BoxableView itemAtPosition = box.getBoxable(position);
    assertNull(itemAtPosition);
  }

  @Test
  public void testMoveItemBetweenBoxes() {
    final String position = "A01";
    final String barcode = "TIB_SamTissue";

    BoxPage firstPage = getBoxPage(500L);
    BoxVisualization firstVisualization = firstPage.getVisualization();

    assertFalse("tissue is in position A01", firstVisualization.isEmptyPosition(position));

    BoxPage secondPage = getBoxPage(501L);
    BoxVisualization secondVisualization = secondPage.getVisualization();

    assertTrue("position A01 is empty", secondVisualization.isEmptyPosition(position));

    secondVisualization.selectPosition(position);
    secondVisualization.lookupBarcode(barcode);
    secondVisualization.updatePosition(false);

    Box first = (Box) getSession().get(BoxImpl.class, 500L);
    Box second = (Box) getSession().get(BoxImpl.class, 501L);
    assertNotNull(second.getBoxable(position));
    assertNull(first.getBoxable(position));
  }

  @Test
  public void testRemoveTube() {
    final String position = "B01";

    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertFalse("position B01 is full", visualization.isEmptyPosition(position));
    visualization.selectPosition(position);
    visualization.removeTube();

    BoxPage newPage = getBoxPage(500L);
    BoxVisualization newVisualization = newPage.getVisualization();
    assertTrue("position B01 is now empty", newVisualization.isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxable(position));
  }

  @Test
  public void testDiscardTube() {
    final String position = "C01";
    
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertFalse("position C01 is full", visualization.isEmptyPosition(position));
    assertTrue("title matches LDI name", visualization.getPositionTitle(position).contains("LDI504"));
    visualization.selectPosition(position);
    visualization.discardTube();

    BoxPage newPage = getBoxPage(500L);
    BoxVisualization newVisualization = newPage.getVisualization();
    assertTrue("position C01 is now empty", newVisualization.isEmptyPosition(position));

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    assertNull(box.getBoxable(position));
    LibraryDilution boxable = (LibraryDilution) getSession().get(LibraryDilution.class, 504L);
    assertTrue("boxable is discarded", boxable.isDiscarded());

    assertTrue("boxable volume is null", boxable.getVolume() == null);
    assertEquals("boxable location is empty", "EMPTY", BoxUtils.makeLocationLabel(boxable));
  }

  @Test
  public void testReplaceTube() {
    final String position = "D01";
    BoxPage page = getBoxPage(500L);
    BoxVisualization visualization = page.getVisualization();

    assertFalse("position D01 is full", visualization.isEmptyPosition(position));
    assertTrue("D01 contains pool", visualization.getPositionTitle(position).contains("Pool"));
    visualization.selectPosition(position);
    visualization.lookupBarcode("TIB_replaceDil");
    visualization.updatePosition(true);

    Box box = (Box) getSession().get(BoxImpl.class, 500L);
    BoxableView boxable = box.getBoxable(position);
    assertTrue("D01 now contains dilution", boxable.getName().startsWith("LDI"));
  }

}
