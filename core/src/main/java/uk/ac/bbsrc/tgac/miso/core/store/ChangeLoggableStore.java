package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;

public interface ChangeLoggableStore {

  public void update(ChangeLoggable changeLoggable);

}
