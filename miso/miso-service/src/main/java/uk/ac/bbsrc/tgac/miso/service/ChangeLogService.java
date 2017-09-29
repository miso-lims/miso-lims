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
   * @param ChangeLog The ChangeLog to be persisted.
   * @return The id of the newly created ChangeLog.
   */
  public Long create(ChangeLog changeLog) throws IOException;
}
