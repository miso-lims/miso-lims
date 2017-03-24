package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;

import com.eaglegenomics.simlims.core.User;

/**
 * Marker interface to indicate an implementing class can generate change logs.
 *
 */
public interface ChangeLoggable {

  public Collection<ChangeLog> getChangeLog();

  public ChangeLog createChangeLog(String summary, String columnsChanged, User user);

}
