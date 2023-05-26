package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.type.StringType;
import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.TransferPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.TransferPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class TimestampConversionIT extends AbstractIT {

  @Before
  public void setup() {
    login();
  }

  @Test
  public void verifyConfiguredTimezones() {
    assertEquals(EASTERN_TIME_ZONE, TimeZone.getDefault());

    Object[] results = (Object[]) getSession().createSQLQuery("SELECT @@session.time_zone, @@system_time_zone")
        .addScalar("@@session.time_zone", StringType.INSTANCE)
        .addScalar("@@system_time_zone", StringType.INSTANCE)
        .uniqueResult();

    assertEquals("Session should default to system time zone", "SYSTEM", results[0]);
    assertEquals("System time zone should be UTC", "UTC", results[1]);
  }

  @Test
  public void testUserEntered() {
    // Goal: verify that a time displayed after saving is the same as the time entered by the user
    TransferPage page = TransferPage.getForCreateWithSamples(getDriver(), getBaseUrl(), Collections.singleton(2L));
    Map<Field, String> fields = new LinkedHashMap<>();
    fields.put(Field.TRANSFER_TIME, "2020-02-20 12:48:00");
    fields.put(Field.SENDER_GROUP, "TestGroupOne");
    fields.put(Field.RECIPIENT, "Random Passerby");
    page.setFields(fields);

    TransferPage page2 = page.save();
    assertEquals(fields.get(Field.TRANSFER_TIME), page2.getField(Field.TRANSFER_TIME));
  }

  @Test
  public void testGenerated() throws ParseException {
    // Goal: verify that a service-generated timestamp is displayed in the local timezone
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date now = new Date();

    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 1L);
    DataTable changes = page.getChangeLogTable();
    for (String changeTimeString : changes.getColumnValues("Time")) {
      Date changeTime = formatter.parse(changeTimeString);
      assertTrue("Existing changes should be more than 5 minutes in the past",
          now.getTime() - changeTime.getTime() > 300000);
    }

    page.setField(LibraryPage.Field.DESCRIPTION, "updated description");

    LibraryPage page2 = page.save();
    DataTable changes2 = page2.getChangeLogTable();
    String latestString = changes2.getColumnValues("Time").stream().max(String::compareTo).orElse(null);
    Date latest = formatter.parse(latestString);
    assertTrue("New change should show approximately current time",
        Math.abs(latest.getTime() - now.getTime()) < 300000L);
  }

}
