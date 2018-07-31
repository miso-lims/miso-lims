package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;

public interface AttachableStore {
  
  public Attachable getManaged(Attachable object);

  public void save(Attachable object);

}
