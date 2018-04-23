package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import com.eaglegenomics.simlims.core.User;

/**
 * Marker interface to indicate an implementing class can generate change logs.
 *
 */
public interface ChangeLoggable {

  public static final Pattern COMMA = Pattern.compile(",");

  public User getLastModifier();

  public void setLastModifier(User user);

  public Date getLastModified();

  public void setLastModified(Date lastModified);

  public User getCreator();

  public void setCreator(User user);

  public Date getCreationTime();

  public void setCreationTime(Date creationTime);

  public boolean isSaved();

  public Collection<ChangeLog> getChangeLog();

  public ChangeLog createChangeLog(String summary, String columnsChanged, User user);

  public default boolean didSomeoneElseChangeColumn(String referenceColumnName, User nonHumanUser) {
    return getChangeLog().stream().filter(cl -> cl.getUser().getUserId() != nonHumanUser.getUserId())
        .flatMap(cl -> COMMA.splitAsStream(cl.getColumnsChanged())).anyMatch(colName -> colName.equals(referenceColumnName));
  }

  /**
   * Updates all user data and timestamps associated with the change. Existing timestamps will be preserved
   * if the object is unsaved, and they are already set; otherwise, they are set to the current time
   * 
   * @param user User to associate with the change
   * @throws IOException
   */
  public default void setChangeDetails(User user) {
    Date now = new Date();
    setLastModifier(user);

    if (!isSaved()) {
      setCreator(user);
      if (getCreationTime() == null) {
        setCreationTime(now);
        setLastModified(now);
      } else if (getLastModified() == null) {
        setLastModified(now);
      }
    } else {
      setLastModified(now);
    }
  }

}
