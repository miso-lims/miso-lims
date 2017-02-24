package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;

/**
 * Access change logs. This doesn't extend from the usual interface because most of the methods don't apply to change logs.
 * 
 * @author amasella
 * 
 */
public interface ChangeLogStore {

  /**
   * List all change logs of a specified type.
   * 
   * @param type The change log type derived from {@link HibernateChangeLogDao.ChangeLogType ChangeLogType}.
   * @return A collection of change logs.
   */
  public Collection<ChangeLog> listAll(String type);

  /**
   * List all change logs of a specified type associated with the id of a specific entity.
   * 
   * @param type The change log type derived from {@link HibernateChangeLogDao.ChangeLogType ChangeLogType}
   * @param entityId The id of the entity the change log is associated with. (Not the id of the change log.)
   * @return A collection of change logs.
   * @throws IOException
   */
  public Collection<ChangeLog> listAllById(String type, long entityId) throws IOException;

  /**
   * Delete all change logs of a specified type associated with the id of a specific entity.
   * 
   * @param type The change log type derived from {@link HibernateChangeLogDao.ChangeLogType ChangeLogType}
   * @param entityId The id of the entity the change log is associated with. (Not the id of the change log.)
   */
  public void deleteAllById(String type, long entityId);

  /**
   * Persist the give change log.
   * 
   * @param changeLog The change log to persist.
   * @return The id of the newly persisted change log.
   */
  public Long create(ChangeLog changeLog);

}
