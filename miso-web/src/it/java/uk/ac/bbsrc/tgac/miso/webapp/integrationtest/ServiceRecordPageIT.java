package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.assertFieldValues;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerServiceRecordImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.SequencerPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage.Field;

public class ServiceRecordPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void addServiceRecord() throws Exception {
    // goal: add one service record
    SequencerReference seq = (SequencerReference) getSession().get(SequencerReferenceImpl.class, 100L);
    assertNotNull(seq);
    assertEquals(0, seq.getServiceRecords().size());

    SequencerPage seqPage = SequencerPage.get(getDriver(), getBaseUrl(), 100L);
    ServiceRecordPage page = seqPage.addServiceRecord();
    assertNotNull(page);

    assertEquals(page.getField(Field.SEQUENCER), seq.getName());

    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.TITLE, "Test Service Record");
    fields.put(Field.DETAILS, "Many details, all of them important");
    fields.put(Field.SERVICED_BY, "Technician");
    fields.put(Field.REFERENCE_NUMBER, "123456");
    fields.put(Field.SERVICE_DATE, "2017-09-01");
    fields.put(Field.SHUTDOWN_TIME, "2017-08-31 16:00:00");
    fields.put(Field.RESTORED_TIME, "2017-09-01 09:00:00");
    page.setFields(fields);

    assertFieldValues("pre-save", fields, page);

    ServiceRecordPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("post-save", fields, page2);

    String newId = page2.getField(Field.ID);

    SequencerServiceRecord sr = (SequencerServiceRecord) getSession().get(SequencerServiceRecordImpl.class, Long.valueOf(newId));
    assertServiceRecordAttributes(fields, sr);
  }

}
