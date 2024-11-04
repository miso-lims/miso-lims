package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;

/**
 * Access change logs. This doesn't extend from the usual interface because most of the methods
 * don't apply to change logs.
 * 
 * @author amasella
 * 
 */
public interface ChangeLogStore {

  /**
   * Persist the give change log.
   * 
   * @param changeLog The change log to persist.
   * @return The id of the newly persisted change log.
   */
  public Long create(ChangeLog changeLog);

}
