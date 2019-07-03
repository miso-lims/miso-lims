package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;

public interface ChangeLoggableStore {

  public void update(ChangeLoggable changeLoggable);

}
