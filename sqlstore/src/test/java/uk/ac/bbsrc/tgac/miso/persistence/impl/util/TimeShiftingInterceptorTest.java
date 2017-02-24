package uk.ac.bbsrc.tgac.miso.persistence.impl.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;

public class TimeShiftingInterceptorTest extends AbstractDAOTest {

  private static final String utcId = "UTC";
  TimeZone utc = TimeZone.getTimeZone(utcId);
  private static final String easternId = "Canada/Eastern";
  TimeZone eastern = TimeZone.getTimeZone(easternId);

  private TimeShiftingInterceptor sut;

  @Autowired
  private SessionFactory sessionFactory;

  private Session session;

  @Before
  public void setup() {
    this.sut = new TimeShiftingInterceptor(utcId, easternId);
    this.session = sessionFactory.withOptions().interceptor(sut).openSession();
  }

  @Test
  public void testTimestamp() throws Exception {
    Transaction tx = session.beginTransaction();
    SampleChangeLog cl = new SampleChangeLog();
    Sample sample = (Sample) session.get(SampleImpl.class, 1L);
    cl.setSample(sample);
    User user = (User) session.get(UserImpl.class, 1L);
    cl.setUser(user);
    cl.setColumnsChanged("cols");
    cl.setSummary("summary");
    Date timestamp = new Date();
    // The H2 driver won't shift the saved time like MySQL, so do it artificially for the test
    Date shifted = new Date(timestamp.getTime() - eastern.getOffset(timestamp.getTime()));
    assertNotEquals(timestamp, shifted);
    cl.setTime(shifted);

    Long newId = (Long) session.save(cl);
    // Interceptor onLoad is only called when retrieving from DB, so delete from cache to make this happen
    // Note: on save, the date in the (cached) Java object is not modified, so the shift is unnecessary
    session.evict(cl);

    SampleChangeLog saved = (SampleChangeLog) session.get(SampleChangeLog.class, newId);
    assertNotNull(saved);
    assertEquals(timestamp, saved.getTime());
    tx.rollback();
  }

  @Test
  public void testDate() throws Exception {
    // Make sure date (without time) is not affected. Otherwise, this could result in +/- 1 day being displayed
    Transaction tx = session.beginTransaction();
    DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
    LibraryDilution ldi = new LibraryDilution();
    ldi.setConcentration(0D);
    ldi.setLibrary((Library) session.get(LibraryImpl.class, 1L));
    Date date = formatter.parse("2017-02-13", Locale.CANADA);
    ldi.setCreationDate(date);
    ldi.setDilutionCreator("me");
    ldi.setName("Jim");
    ldi.setLastUpdated(new Date());
    ldi.setLastModifier((User) session.get(UserImpl.class, 1L));

    Long newId = (Long) session.save(ldi);
    session.evict(ldi);

    LibraryDilution saved = (LibraryDilution) session.get(LibraryDilution.class, newId);
    assertNotNull(saved);
    assertEquals(date.getTime(), saved.getCreationDate().getTime());
    tx.rollback();
  }

  @Test
  public void testToUiTimeStandard() {
    // During EST (-04:00)
    Date dbTime = makeUtcDate(2017, 6, 2, 2, 30);
    Date expectedUiTime = makeUtcDate(2017, 6, 1, 22, 30);
    assertEquals(expectedUiTime, sut.toUiTime(dbTime));
  }

  @Test
  public void testToUiTimeDaylight() {
    // During EDT (-05:00)
    Date dbTime = makeUtcDate(2017, 0, 1, 20, 39);
    Date expectedUiTime = makeUtcDate(2017, 0, 1, 15, 39);
    assertEquals(expectedUiTime, sut.toUiTime(dbTime));
  }

  private Date makeUtcDate(int year, int month, int day, int hour, int minute) {
    Calendar cal = Calendar.getInstance(utc);
    cal.clear();
    cal.set(year, month, day, hour, minute);
    return cal.getTime();
  }

}
