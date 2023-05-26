package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertNotNull;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.InstrumentPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.InstrumentPage.Field;

public class InstrumentPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testStatusEffects() throws Exception {
    // goal: ensure the Status radio buttons affect the visible fields as expected
    InstrumentPage page = InstrumentPage.get(getDriver(), getBaseUrl(), 102L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "102");
    fields.put(Field.INSTRUMENT_MODEL, "Illumina - Illumina HiSeq 2500");
    fields.put(Field.NAME, "OldHiSeq_102");
    fields.put(Field.COMMISSIONED, "2017-01-01");
    fields.put(Field.STATUS, "Upgraded");
    fields.put(Field.DECOMMISSIONED, "2017-02-01");
    fields.put(Field.UPGRADED_INSTRUMENT, "NewHiSeq_101");
    fields.put(Field.DEFAULT_PURPOSE, "Production");
    assertFieldValues("loaded (upgraded)", fields, page);

    // set status to retired
    Map<Field, String> retired = Maps.newLinkedHashMap();
    retired.put(Field.STATUS, "Retired");
    page.setFields(retired);

    // copy unchanged except for Upgraded Instrument name, which should be hidden
    fields.forEach((key, val) -> {
      if (!retired.containsKey(key) && !Field.UPGRADED_INSTRUMENT.equals(key))
        retired.put(key, val);
    });
    assertFieldValues("changes pre-save (retired)", retired, page);

    InstrumentPage retiredPage = page.save();
    assertNotNull(retiredPage);
    assertFieldValues("changes post-save (retired)", retired, retiredPage);

    // set status to production
    Map<Field, String> production = Maps.newLinkedHashMap();
    production.put(Field.STATUS, "Production");
    page.setFields(production);

    // copy unchanged except for Decommissioned date, which should be hidden
    retired.forEach((key, val) -> {
      if (!production.containsKey(key) && !Field.DECOMMISSIONED.equals(key))
        production.put(key, val);
    });
    assertFieldValues("changes pre-save (production)", production, page);

    InstrumentPage productionPage = page.save();
    assertNotNull(productionPage);
    assertFieldValues("changes post-save (production)", production, productionPage);
  }

  @Test
  public void testChangeValues() throws Exception {
    // goal: test editing all editable fields on an instrument
    InstrumentPage page = InstrumentPage.get(getDriver(), getBaseUrl(), 100L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "100");
    fields.put(Field.COMMISSIONED, "2017-01-01");
    fields.put(Field.INSTRUMENT_MODEL, "Illumina - Illumina HiSeq 2500");
    fields.put(Field.SERIAL_NUMBER, "100");
    fields.put(Field.NAME, "HiSeq_100");
    fields.put(Field.STATUS, "Production");
    fields.put(Field.DEFAULT_PURPOSE, "Production");
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.NAME, "HiSeq_changed_100");
    changes.put(Field.SERIAL_NUMBER, "100100");
    changes.put(Field.COMMISSIONED, "2017-01-31");
    changes.put(Field.STATUS, "Retired");
    changes.put(Field.DECOMMISSIONED, "2017-10-31");
    page.setFields(changes);
    // need to make sure this is done after the status change is made

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key))
        changes.put(key, val);
    });
    assertFieldValues("changes pre-save", changes, page);

    InstrumentPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    Instrument sr = (Instrument) getSession().get(InstrumentImpl.class, 100L);
    assertInstrumentAttributes(changes, sr);
  }

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

  private static void assertInstrumentAttributes(Map<Field, String> expectedValues, Instrument sr) {
    assertAttribute(Field.ID, expectedValues, Long.toString(sr.getId()));
    assertAttribute(Field.NAME, expectedValues, sr.getName());
    assertAttribute(Field.INSTRUMENT_MODEL, expectedValues, sr.getInstrumentModel().getPlatformAndAlias());
    assertAttribute(Field.SERIAL_NUMBER, expectedValues, sr.getSerialNumber());
    assertAttribute(Field.COMMISSIONED, expectedValues, sr.getDateCommissioned().format(dateFormatter));
    assertAttribute(Field.STATUS, expectedValues,
        sr.getUpgradedInstrument() != null ? "Upgraded"
            : (sr.getDateDecommissioned() != null ? "Retired" : "Production"));
    if (expectedValues.containsKey(Field.DECOMMISSIONED)) {
      assertAttribute(Field.DECOMMISSIONED, expectedValues,
          (sr.getDateDecommissioned() == null ? null : sr.getDateDecommissioned().format(dateFormatter)));
    }
    if (expectedValues.containsKey(Field.UPGRADED_INSTRUMENT)) {
      assertAttribute(Field.UPGRADED_INSTRUMENT, expectedValues, sr.getUpgradedInstrument().getName());
    }
  }

}
