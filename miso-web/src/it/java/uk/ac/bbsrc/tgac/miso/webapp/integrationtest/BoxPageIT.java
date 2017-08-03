package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BoxPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BoxPage.Fields;

public class BoxPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  private BoxPage getBoxPage(Long boxId) {
    return BoxPage.get(getDriver(), getBaseUrl(), boxId);
  }

  @Test
  public void testSaveEditNewBox() {
    // Goal: save and edit one box
    BoxPage page = getBoxPage(null);
    assertNotNull(page);

    Map<String, String> unsaved = new HashMap<>();
    unsaved.put(Fields.ID, "Unsaved");
    unsaved.put(Fields.ALIAS, "New Box");
    unsaved.put(Fields.DESCRIPTION, "Test");
    unsaved.put(Fields.USE, "Storage");
    unsaved.put(Fields.SIZE, "10 × 10");
    unsaved.put(Fields.LOCATION, "Here");

    assertEquals("Box ID is unsaved", unsaved.get(Fields.ID), page.getId());
    page.setAlias(unsaved.get(Fields.ALIAS));
    page.setDescription(unsaved.get(Fields.DESCRIPTION));
    page.setBoxUse(unsaved.get(Fields.USE));
    page.setBoxSize(unsaved.get(Fields.SIZE));
    page.setLocation(unsaved.get(Fields.LOCATION));
    page.clickSave();
    assertNotEquals("Box ID is now a number", unsaved.get(Fields.ID), page.getId());
    assertEquals(unsaved.get(Fields.ALIAS), page.getAlias());
    assertEquals(unsaved.get(Fields.DESCRIPTION), page.getDescription());
    assertEquals(unsaved.get(Fields.USE), page.getBoxUse());
    assertEquals(unsaved.get(Fields.SIZE), page.getBoxSize());
    assertEquals(unsaved.get(Fields.LOCATION), page.getLocation());

    // change fields
    Map<String, String> update = new HashMap<>();
    update.put(Fields.ALIAS, "Changed Box");
    update.put(Fields.DESCRIPTION, "Changed Description");
    update.put(Fields.USE, "Libraries");
    update.put(Fields.LOCATION, "Changed Location");

    page.setAlias(update.get(Fields.ALIAS));
    page.setDescription(update.get(Fields.DESCRIPTION));
    page.setBoxUse(update.get(Fields.USE));
    page.setLocation(update.get(Fields.LOCATION));
    page.clickSave();
    assertEquals(update.get(Fields.ALIAS), page.getAlias());
    assertEquals(update.get(Fields.DESCRIPTION), page.getDescription());
    assertEquals(update.get(Fields.USE), page.getBoxUse());
    assertEquals(update.get(Fields.LOCATION), page.getLocation());
  }
}
