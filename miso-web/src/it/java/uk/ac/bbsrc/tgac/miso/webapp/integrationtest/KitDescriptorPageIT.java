package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.KitDescriptorPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.KitDescriptorPage.Field;

public class KitDescriptorPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreate() throws Exception {
    KitDescriptorPage page1 = KitDescriptorPage.getForCreate(getDriver(), getBaseUrl());

    // default values
    Map<KitDescriptorPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "Unsaved");
    fields.put(Field.NAME, null);
    fields.put(Field.VERSION, null);
    fields.put(Field.MANUFACTURER, null);
    fields.put(Field.PART_NUMBER, null);
    fields.put(Field.STOCK_LEVEL, "0");
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.KIT_TYPE, "Library");
    fields.put(Field.PLATFORM, "Illumina");
    assertFieldValues("default values", fields, page1);

    // enter kit info
    Map<KitDescriptorPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.NAME, "Test Clustering Kit");
    changes.put(Field.VERSION, "1");
    changes.put(Field.MANUFACTURER, "ACME");
    changes.put(Field.PART_NUMBER, "123");
    changes.put(Field.STOCK_LEVEL, "11");
    changes.put(Field.DESCRIPTION, "Test Description");
    changes.put(Field.KIT_TYPE, "Clustering");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("pre-save", fields, page1);

    KitDescriptorPage page2 = page1.save();
    fields.remove(Field.ID);
    assertFieldValues("post-save", fields, page2);

    long savedId = Long.parseLong(page2.getField(Field.ID));
    KitDescriptor saved = (KitDescriptor) getSession().get(KitDescriptor.class, savedId);
    assertKitDescriptorAttributes(fields, saved);
  }

  @Test
  public void testChangeValues() throws Exception {
    KitDescriptorPage page1 = KitDescriptorPage.getForEdit(getDriver(), getBaseUrl(), 1L);

    // initial values
    Map<KitDescriptorPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "1");
    fields.put(Field.NAME, "Test Kit");
    fields.put(Field.VERSION, "1");
    fields.put(Field.MANUFACTURER, "TestCo");
    fields.put(Field.PART_NUMBER, "123");
    fields.put(Field.STOCK_LEVEL, "0");
    fields.put(Field.KIT_TYPE, "Library");
    fields.put(Field.PLATFORM, "Illumina");
    assertFieldValues("initial values", fields, page1);

    Map<KitDescriptorPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.VERSION, "2");
    changes.put(Field.MANUFACTURER, "CoTest");
    changes.put(Field.PART_NUMBER, "321");
    changes.put(Field.STOCK_LEVEL, "1");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    KitDescriptorPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    KitDescriptor saved = (KitDescriptor) getSession().get(KitDescriptor.class, 1L);
    assertKitDescriptorAttributes(fields, saved);
  }

  private void assertKitDescriptorAttributes(Map<KitDescriptorPage.Field, String> expectedValues, KitDescriptor kd) {
    assertAttribute(Field.ID, expectedValues, Long.toString(kd.getId()));
    assertAttribute(Field.NAME, expectedValues, kd.getName());
    assertAttribute(Field.VERSION, expectedValues, kd.getVersion().toString());
    assertAttribute(Field.MANUFACTURER, expectedValues, kd.getManufacturer());
    assertAttribute(Field.PART_NUMBER, expectedValues, kd.getPartNumber());
    assertAttribute(Field.STOCK_LEVEL, expectedValues, kd.getStockLevel().toString());
    assertAttribute(Field.DESCRIPTION, expectedValues, kd.getDescription());
    assertAttribute(Field.KIT_TYPE, expectedValues, kd.getKitType().getKey());
    assertAttribute(Field.PLATFORM, expectedValues, kd.getPlatformType().getKey());
  }
}
