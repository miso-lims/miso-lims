package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;
import java.util.regex.Pattern;

import com.eaglegenomics.simlims.core.User;

/**
 * Marker interface to indicate an implementing class can generate change logs.
 *
 */
public interface ChangeLoggable {

  public static final Pattern COMMA = Pattern.compile(",");

  public Collection<ChangeLog> getChangeLog();

  public ChangeLog createChangeLog(String summary, String columnsChanged, User user);

  public default boolean didSomeoneElseChangeColumn(String referenceColumnName, User nonHumanUser) {
    return getChangeLog().stream().filter(cl -> cl.getUser().getUserId() != nonHumanUser.getUserId())
        .flatMap(cl -> COMMA.splitAsStream(cl.getColumnsChanged())).anyMatch(colName -> colName.equals(referenceColumnName));
  }

}
