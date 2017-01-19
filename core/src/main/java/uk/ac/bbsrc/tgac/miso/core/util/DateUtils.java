package uk.ac.bbsrc.tgac.miso.core.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import net.sf.json.JSONObject;

/**
 * Created by zakm on 06/08/2015.
 */
public class DateUtils {
  static final int EXPIRY_THRESHOLD_DAYS = 30;

  public enum ExpiryState {
    EXPIRED("expired"), SOON_TO_EXPIRE("soon to expire"), GOOD_TO_USE("good to use");

    private final String name;

    ExpiryState(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static Date asDate(LocalDate localDate) {
    // Java 8
    // return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    return localDate.toDateTimeAtStartOfDay(DateTimeZone.forTimeZone(TimeZone.getDefault())).toDate();
  }

  public static LocalDate asLocalDate(Date date) {
    // Java 8
    // return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    return new LocalDate(date.getTime(), DateTimeZone.forTimeZone(TimeZone.getDefault()));
  }

  public static LocalDate asLocalDate(String date) {
    return LocalDate.parse(date);
  }

  public static ExpiryState getExpiryState(LocalDate date) {
    LocalDate today = LocalDate.now();
    // JAVA 8
    // long periodDays = ChronoUnit.DAYS.between(today,date);
    Days periodDays = Days.daysBetween(today, date);
    if (periodDays.getDays() < 0) {
      return ExpiryState.EXPIRED;
    } else if (periodDays.getDays() < EXPIRY_THRESHOLD_DAYS) {
      return ExpiryState.SOON_TO_EXPIRE;
    } else {
      return ExpiryState.GOOD_TO_USE;
    }
  }

  public static Timestamp getTimeStampFromJSON(JSONObject timestampJSON) {
    long millis = timestampJSON.getLong("time");
    return new Timestamp(millis);
  }

  public static String getStringFromTimeStamp(Timestamp timestamp) {
    return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp);
  }
}