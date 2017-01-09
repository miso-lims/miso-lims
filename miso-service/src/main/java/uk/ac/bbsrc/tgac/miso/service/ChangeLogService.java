package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;

public interface ChangeLogService {

  public Collection<ChangeLog> listAll(String type) throws IOException;

  public Collection<ChangeLog> listAllById(String type, long id) throws IOException;

  public void deleteAllById(String type, long id) throws IOException;

  /**
   * Create a change log from a provided ChangeLog object.
   * 
   * @param type The type of change log, as a String.
   * @param entityId The id of the entity that the change log is associated with.
   * @param changeLog Values from this ChangeLog will be copied to the new ChangeLog with the exceptions of the id and the timestamp.
   * @return The id of the newly created ChangeLog.
   */
  public Long create(String type, long entityId, ChangeLog changeLog) throws IOException;
}
