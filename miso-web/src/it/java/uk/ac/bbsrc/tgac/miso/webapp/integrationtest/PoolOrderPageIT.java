package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertFieldValues;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.PoolOrderPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.PoolOrderPage.Field;

public class PoolOrderPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreateMinimal() {
    PoolOrderPage page = PoolOrderPage.getForCreate(getDriver(), getBaseUrl());
    Map<Field, String> fields = new LinkedHashMap<>();
    fields.put(Field.ALIAS, "Test Create Order");
    fields.put(Field.PURPOSE, "Production");
    page.setFields(fields);

    String aliquotName = "LDI1001";
    page.addAliquots(Lists.newArrayList(aliquotName));

    assertEquals("", page.getField(Field.ID));
    assertFieldValues("changes pre-save", fields, page);
    PoolOrderPage savedPage = page.clickSave();
    assertNotNull("Pool order should save successfully", savedPage);
    assertFieldValues("changes post-save", fields, savedPage);

    PoolOrder order = (PoolOrder) getSession().get(PoolOrder.class, Long.valueOf(savedPage.getField(Field.ID)));
    assertEquals(fields.get(Field.ALIAS), order.getAlias());
    assertEquals(fields.get(Field.PURPOSE), order.getPurpose().getAlias());
    assertEquals(1, order.getOrderLibraryAliquots().size());
    assertEquals(aliquotName, order.getOrderLibraryAliquots().iterator().next().getAliquot().getName());
  }

  @Test
  public void testEditMinimal() {
    PoolOrderPage page = PoolOrderPage.getForEdit(getDriver(), getBaseUrl(), 3L);
    PoolOrderPage savedPage = page.clickSave();
    assertNotNull(savedPage);
  }

  @Test
  public void testEditMaximal() {
    PoolOrderPage page = PoolOrderPage.getForEdit(getDriver(), getBaseUrl(), 2L);
    PoolOrderPage savedPage = page.clickSave();
    assertNotNull(savedPage);
  }

}
