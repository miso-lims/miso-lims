package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;

public interface ChangeLogService {

  /**
   * Create a change log from a provided ChangeLog object.
   * 
   * @param ChangeLog The ChangeLog to be persisted.
   * @return The id of the newly created ChangeLog.
   */
  public Long create(ChangeLog changeLog) throws IOException;
}
