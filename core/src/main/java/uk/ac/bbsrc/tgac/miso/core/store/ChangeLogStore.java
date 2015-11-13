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
  public Collection<ChangeLog> listAll(String type);

  public Collection<ChangeLog> listAllById(String type, long id);

  public Collection<ChangeLog> listAllById(String type, String idName, long id);
}
