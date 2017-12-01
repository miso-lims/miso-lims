package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.SequencerPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage.Field;

public class ServiceRecordPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreate() throws Exception {
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

    SequencerServiceRecord sr = (SequencerServiceRecord) getSession().get(SequencerServiceRecord.class, Long.valueOf(newId));
    assertServiceRecordAttributes(fields, sr);
  }

  @Test
  public void testChangeValues() throws IOException {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 150L);
    assertNotNull(page1);

    // initial values
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "150");
    fields.put(Field.SEQUENCER, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 150");
    fields.put(Field.DETAILS, "details go here");
    fields.put(Field.SERVICED_BY, "technician1");
    fields.put(Field.REFERENCE_NUMBER, "12345");
    fields.put(Field.SERVICE_DATE, "2017-09-05");
    fields.put(Field.SHUTDOWN_TIME, "2017-09-01 10:00:00");
    fields.put(Field.RESTORED_TIME, "2017-09-05 10:00:00");
    assertFieldValues("initial values", fields, page1);
    
    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.TITLE, "New Title");
    changes.put(Field.DETAILS, "new details");
    changes.put(Field.SERVICED_BY, "new technician");
    changes.put(Field.REFERENCE_NUMBER, "54321");
    changes.put(Field.SERVICE_DATE, "2017-09-09");
    changes.put(Field.SHUTDOWN_TIME, "2012-01-01 01:00:00");
    changes.put(Field.RESTORED_TIME, "2017-12-12 23:59:59");
    page1.setFields(changes);
    
    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);
    
    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    SequencerServiceRecord savedRecord = (SequencerServiceRecord) getSession().get(SequencerServiceRecord.class, 150L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  @Test
  public void testAddValues() throws Exception {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 151L);

    // initial values;
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "151");
    fields.put(Field.SEQUENCER, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 151");
    fields.put(Field.DETAILS, null);
    fields.put(Field.SERVICED_BY, "tech");
    fields.put(Field.REFERENCE_NUMBER, null);
    fields.put(Field.SERVICE_DATE, "2017-09-12");
    fields.put(Field.SHUTDOWN_TIME, null);
    fields.put(Field.RESTORED_TIME, null);
    assertFieldValues("initial values", fields, page1);

    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DETAILS, "details");
    changes.put(Field.REFERENCE_NUMBER, "REFERENCE");
    changes.put(Field.SHUTDOWN_TIME, "2017-09-10 09:10:00");
    changes.put(Field.RESTORED_TIME, "2017-09-12 12:00:00");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    SequencerServiceRecord savedRecord = (SequencerServiceRecord) getSession().get(SequencerServiceRecord.class, 151L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  @Test
  public void testRemoveValues() throws Exception {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 152L);

    // initial values;
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "152");
    fields.put(Field.SEQUENCER, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 152");
    fields.put(Field.DETAILS, "details to remove");
    fields.put(Field.SERVICED_BY, "technitchin");
    fields.put(Field.REFERENCE_NUMBER, "Riffraff");
    fields.put(Field.SERVICE_DATE, "2017-09-12");
    fields.put(Field.SHUTDOWN_TIME, "2017-09-11 11:00:00");
    fields.put(Field.RESTORED_TIME, "2017-09-12 12:00:00");
    assertFieldValues("initial values", fields, page1);

    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DETAILS, null);
    changes.put(Field.REFERENCE_NUMBER, null);
    changes.put(Field.SHUTDOWN_TIME, null);
    changes.put(Field.RESTORED_TIME, null);
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    SequencerServiceRecord savedRecord = (SequencerServiceRecord) getSession().get(SequencerServiceRecord.class, 152L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  private static void assertServiceRecordAttributes(Map<Field, String> expectedValues, SequencerServiceRecord sr) {
    assertAttribute(Field.ID, expectedValues, Long.toString(sr.getId()));
    assertAttribute(Field.SEQUENCER, expectedValues, sr.getSequencerReference().getName());
    assertAttribute(Field.TITLE, expectedValues, sr.getTitle());
    assertAttribute(Field.DETAILS, expectedValues, sr.getDetails());
    assertAttribute(Field.SERVICE_DATE, expectedValues, formatDate(sr.getServiceDate()));
    assertAttribute(Field.SHUTDOWN_TIME, expectedValues, formatDateTime(sr.getShutdownTime()));
    assertAttribute(Field.RESTORED_TIME, expectedValues, formatDateTime(sr.getRestoredTime()));
  }
}
