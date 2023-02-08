package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.InstrumentPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ServiceRecordPage.Field;

public class ServiceRecordPageIT extends AbstractIT {

  @Autowired
  static InstrumentService instrumentService;

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testCreate() throws Exception {
    // goal: add one service record
    Instrument seq = (Instrument) getSession().get(InstrumentImpl.class, 200L);
    assertNotNull(seq);
    assertEquals(0, seq.getServiceRecords().size());

    InstrumentPage seqPage = InstrumentPage.get(getDriver(), getBaseUrl(), 200L);
    ServiceRecordPage page = seqPage.addServiceRecord();
    assertNotNull(page);

    assertEquals(seq.getName(), page.getField(Field.INSTRUMENT));

    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.TITLE, "Test Service Record");
    fields.put(Field.DETAILS, "Many details, all of them important");
    fields.put(Field.SERVICED_BY, "Technician");
    fields.put(Field.REFERENCE_NUMBER, "123456");
    fields.put(Field.SERVICE_DATE, "2017-09-01");
    fields.put(Field.START_TIME, "2017-08-31 16:00:00");
    fields.put(Field.OUT_OF_SERVICE, "true");
    fields.put(Field.END_TIME, "2017-09-01 09:00:00");
    page.setFields(fields);

    assertFieldValues("pre-save", fields, page);

    ServiceRecordPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("post-save", fields, page2);

    String newId = page2.getField(Field.ID);

    ServiceRecord sr = (ServiceRecord) getSession().get(ServiceRecord.class, Long.valueOf(newId));
    assertServiceRecordAttributes(fields, sr);
  }

  @Test
  public void testChangeValues() throws IOException {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 150L);
    assertNotNull(page1);

    // initial values
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "150");
    fields.put(Field.INSTRUMENT, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 150");
    fields.put(Field.DETAILS, "details go here");
    fields.put(Field.SERVICED_BY, "technician1");
    fields.put(Field.REFERENCE_NUMBER, "12345");
    fields.put(Field.SERVICE_DATE, "2017-09-05");
    fields.put(Field.START_TIME, "2017-09-01 06:00:00");
    fields.put(Field.END_TIME, "2017-09-05 06:00:00");
    assertFieldValues("initial values", fields, page1);

    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.TITLE, "New Title");
    changes.put(Field.DETAILS, "new details");
    changes.put(Field.SERVICED_BY, "new technician");
    changes.put(Field.REFERENCE_NUMBER, "54321");
    changes.put(Field.SERVICE_DATE, "2017-09-09");
    changes.put(Field.START_TIME, "2012-01-01 01:00:00");
    changes.put(Field.END_TIME, "2017-12-12 23:59:59");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    ServiceRecord savedRecord = (ServiceRecord) getSession().get(ServiceRecord.class, 150L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  @Test
  public void testAddValues() throws Exception {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 151L);

    // initial values;
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "151");
    fields.put(Field.INSTRUMENT, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 151");
    fields.put(Field.DETAILS, null);
    fields.put(Field.SERVICED_BY, null);
    fields.put(Field.REFERENCE_NUMBER, null);
    fields.put(Field.SERVICE_DATE, "2017-09-12");
    fields.put(Field.START_TIME, null);
    fields.put(Field.OUT_OF_SERVICE, "false");
    fields.put(Field.END_TIME, null);
    assertFieldValues("initial values", fields, page1);

    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DETAILS, "details");
    changes.put(Field.REFERENCE_NUMBER, "REFERENCE");
    changes.put(Field.SERVICED_BY, "person");
    changes.put(Field.START_TIME, "2017-09-10 09:10:00");
    changes.put(Field.OUT_OF_SERVICE, "true");
    changes.put(Field.END_TIME, "2017-09-12 12:00:00");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    ServiceRecord savedRecord = (ServiceRecord) getSession().get(ServiceRecord.class, 151L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  @Test
  public void testRemoveValues() throws Exception {
    ServiceRecordPage page1 = ServiceRecordPage.get(getDriver(), getBaseUrl(), null, 152L);

    // initial values;
    Map<ServiceRecordPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "152");
    fields.put(Field.INSTRUMENT, "NewHiSeq_101");
    fields.put(Field.TITLE, "Test 152");
    fields.put(Field.DETAILS, "details to remove");
    fields.put(Field.SERVICED_BY, "technitchin");
    fields.put(Field.REFERENCE_NUMBER, "Riffraff");
    fields.put(Field.SERVICE_DATE, "2017-09-12");
    fields.put(Field.START_TIME, "2017-09-11 07:00:00");
    fields.put(Field.OUT_OF_SERVICE, "true");
    fields.put(Field.END_TIME, "2017-09-12 08:00:00");
    assertFieldValues("initial values", fields, page1);

    Map<ServiceRecordPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DETAILS, null);
    changes.put(Field.REFERENCE_NUMBER, null);
    changes.put(Field.SERVICED_BY, null);
    changes.put(Field.START_TIME, null);
    changes.put(Field.OUT_OF_SERVICE, "false");
    changes.put(Field.END_TIME, null);
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    ServiceRecordPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    ServiceRecord savedRecord = (ServiceRecord) getSession().get(ServiceRecord.class, 152L);
    assertServiceRecordAttributes(fields, savedRecord);
  }

  private static void assertServiceRecordAttributes(Map<Field, String> expectedValues, ServiceRecord sr)
      throws IOException {
    assertAttribute(Field.ID, expectedValues, Long.toString(sr.getId()));
    assertAttribute(Field.INSTRUMENT, expectedValues, instrumentService.getInstrument(sr).getName());
    assertAttribute(Field.TITLE, expectedValues, sr.getTitle());
    assertAttribute(Field.DETAILS, expectedValues, sr.getDetails());
    assertAttribute(Field.SERVICE_DATE, expectedValues, formatDate(sr.getServiceDate()));
    assertAttribute(Field.START_TIME, expectedValues, formatDateTime(sr.getStartTime()));
    assertAttribute(Field.END_TIME, expectedValues, formatDateTime(sr.getEndTime()));
  }
}
