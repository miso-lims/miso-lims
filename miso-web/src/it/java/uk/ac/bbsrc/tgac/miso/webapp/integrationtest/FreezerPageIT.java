package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertAttribute;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FreezerPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FreezerPage.Field;

public class FreezerPageIT extends AbstractIT {

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testCreate() {
    FreezerPage page1 = FreezerPage.getForCreate(getDriver(), getBaseUrl());

    Map<FreezerPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ROOM, "Room One");
    fields.put(Field.ALIAS, "New Freezer");
    page1.setFields(fields);

    FreezerPage page2 = page1.save();
    long savedId = Long.parseLong(page2.getField(Field.ID));
    StorageLocation saved = (StorageLocation) getSession().get(StorageLocation.class, savedId);
    assertFreezerAttributes(fields, saved);
  }

  @Test
  public void testUpdate() {
    FreezerPage page1 = FreezerPage.get(getDriver(), getBaseUrl(), 3L);
    FreezerPage page2 = page1.save();
    assertNotNull(page2);
  }

  private void assertFreezerAttributes(Map<FreezerPage.Field, String> expectedValues, StorageLocation freezer) {
    assertAttribute(Field.ROOM, expectedValues, freezer.getParentLocation().getAlias());
    assertAttribute(Field.ALIAS, expectedValues, freezer.getAlias());
    assertEquals(LocationUnit.FREEZER, freezer.getLocationUnit());
  }

}
