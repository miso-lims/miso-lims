package uk.ac.bbsrc.tgac.miso.persistence.impl.util;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

/**
 * When saving java.util.Date values via MySQL JDBC driver (which is used by Hibernate), the time is
 * automatically shifted to compensate for timezone difference between the client and server. The
 * reverse doesn't happen on load though. This interceptor shifts the timestamps back to client time
 */
public class TimeShiftingInterceptor extends EmptyInterceptor {

  private static final long serialVersionUID = 1L;

  private final TimeZone dbZone;
  private final TimeZone uiZone;

  public TimeShiftingInterceptor(String dbZone, String uiZone) {
    this.dbZone = TimeZone.getTimeZone(dbZone);
    this.uiZone = TimeZone.getTimeZone(uiZone);
  }

  @Override
  public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    boolean modified = false;
    for (int i = 0; i < state.length; i++) {
      if (state[i] != null && types[i] instanceof TimestampType) {
        state[i] = toUiTime((Date) state[i]);
        modified = true;
      }
    }
    return modified;
  }

  public Date shiftTime(Date date, TimeZone from, TimeZone to) {
    long time = date.getTime();
    int fromOffset = from.getOffset(time);
    int toOffset = to.getOffset(time);
    long shifted = time - fromOffset + toOffset;
    return new Date(shifted);
  }

  public Date toUiTime(Date dbTime) {
    return shiftTime(dbTime, dbZone, uiZone);
  }

}
