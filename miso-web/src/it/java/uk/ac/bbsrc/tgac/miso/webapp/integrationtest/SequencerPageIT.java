package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertNotNull;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.SequencerPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.SequencerPage.Field;

public class SequencerPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testStatusEffects() throws Exception {
    // goal: ensure the Status radio buttons affect the visible fields as expected
    SequencerPage page = SequencerPage.get(getDriver(), getBaseUrl(), 102L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "102");
    fields.put(Field.PLATFORM, "Illumina - Illumina HiSeq 2500");
    fields.put(Field.NAME, "OldHiSeq_102");
    fields.put(Field.IP_ADDRESS, "127.0.0.1");
    fields.put(Field.COMMISSIONED, "2017-01-01");
    fields.put(Field.STATUS, "upgraded");
    fields.put(Field.DECOMMISSIONED, "2017-02-01");
    fields.put(Field.UPGRADED_REF, "NewHiSeq_101");
    assertFieldValues("loaded (upgraded)", fields, page);

    // set status to retired
    Map<Field, String> retired = Maps.newLinkedHashMap();
    retired.put(Field.STATUS, "retired");
    page.setFields(retired);

    // copy unchanged except for Upgraded Sequencer name, which should be hidden
    fields.forEach((key, val) -> {
      if (!retired.containsKey(key) && !Field.UPGRADED_REF.equals(key)) retired.put(key, val);
    });
    assertFieldValues("changes pre-save (retired)", retired, page);

    SequencerPage retiredPage = page.save();
    assertNotNull(retiredPage);
    assertFieldValues("changes post-save (retired)", retired, retiredPage);

    // set status to production
    Map<Field, String> production = Maps.newLinkedHashMap();
    production.put(Field.STATUS, "production");
    page.setFields(production);

    // copy unchanged except for Decommissioned date, which should be hidden
    retired.forEach((key, val) -> {
      if (!production.containsKey(key) && !Field.DECOMMISSIONED.equals(key)) production.put(key, val);
    });
    assertFieldValues("changes pre-save (production)", production, page);

    SequencerPage productionPage = page.save();
    assertNotNull(productionPage);
    assertFieldValues("changes post-save (production)", production, productionPage);
  }

  @Test
  public void testChangeValues() throws Exception {
    // goal: test editing all editable fields on a sequencer
    SequencerPage page = SequencerPage.get(getDriver(), getBaseUrl(), 100L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "100");
    fields.put(Field.COMMISSIONED, "2017-01-01");
    fields.put(Field.PLATFORM, "Illumina - Illumina HiSeq 2500");
    fields.put(Field.SERIAL_NUMBER, "100");
    fields.put(Field.NAME, "HiSeq_100");
    fields.put(Field.IP_ADDRESS, "127.0.0.1");
    fields.put(Field.STATUS, "production");
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.NAME, "HiSeq_changed_100");
    changes.put(Field.SERIAL_NUMBER, "100100");
    changes.put(Field.COMMISSIONED, "2017-01-31");
    changes.put(Field.IP_ADDRESS, "127.0.0.5");
    changes.put(Field.STATUS, "retired");
    changes.put(Field.DECOMMISSIONED, "2017-10-31");
    page.setFields(changes);
    // need to make sure this is done after the status change is made

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key)) changes.put(key, val);
    });
    assertFieldValues("changes pre-save", changes, page);

    SequencerPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    SequencerReference sr = (SequencerReference) getSession().get(SequencerReferenceImpl.class, 100L);
    assertSequencerReferenceAttributes(changes, sr);
  }

  private static final DateTimeFormatter dateFormatter = ISODateTimeFormat.date();

  private static void assertSequencerReferenceAttributes(Map<Field, String> expectedValues, SequencerReference sr) {
    assertAttribute(Field.ID, expectedValues, Long.toString(sr.getId()));
    assertAttribute(Field.NAME, expectedValues, sr.getName());
    assertAttribute(Field.PLATFORM, expectedValues, sr.getPlatform().getNameAndModel());
    assertAttribute(Field.SERIAL_NUMBER, expectedValues, sr.getSerialNumber());
    assertAttribute(Field.IP_ADDRESS, expectedValues,
        (sr.getIpAddress() == null ? "" : sr.getIpAddress().toString()));
    assertAttribute(Field.COMMISSIONED, expectedValues, dateFormatter.print(sr.getDateCommissioned().getTime()));
    assertAttribute(Field.STATUS, expectedValues,
        (sr.getUpgradedSequencerReference() != null ? "Upgraded" : (sr.getDateDecommissioned() != null ? "Retired" : "Production"))
            .toLowerCase());
    if (expectedValues.containsKey(Field.DECOMMISSIONED)) {
      assertAttribute(Field.DECOMMISSIONED, expectedValues,
          (sr.getDateDecommissioned() == null ? null : dateFormatter.print(sr.getDateDecommissioned().getTime())));
    }
    if (expectedValues.containsKey(Field.UPGRADED_REF)) {
      assertAttribute(Field.UPGRADED_REF, expectedValues, sr.getUpgradedSequencerReference().getName());
    }
  }

}
