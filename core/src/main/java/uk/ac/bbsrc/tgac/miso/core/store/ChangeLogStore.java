package uk.ac.bbsrc.tgac.miso.core.store;

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
   */
  public Collection<ChangeLog> listAllById(String type, long entityId);
  
  /**
   * Delete all change logs of a specified type associated with the id of a specific entity.
   * 
   * @param type The change log type derived from {@link HibernateChangeLogDao.ChangeLogType ChangeLogType}
   * @param entityId The id of the entity the change log is associated with. (Not the id of the change log.)
   */
  public void deleteAllById(String type, long entityId);
  
  /**
   * Create a change log of a specified type for an entity specified by entityId. The details of the change log message are copied from the
   * specified {@link ChangeLog} argument.
   * 
   * @param type The change log type derived from {@link HibernateChangeLogDao.ChangeLogType ChangeLogType}
   * @param entityId The id of the entity the change log is associated with. (Not the id of the change log.)
   * @param changeLog The change log details are copied from this object. It is only necessary to provide the columnsChanged, summary and
   *          user values. All other properties are ignored.
   * @return The id of newly created change log.
   */
  public Long create(String type, long entityId, ChangeLog changeLog);
  
}
