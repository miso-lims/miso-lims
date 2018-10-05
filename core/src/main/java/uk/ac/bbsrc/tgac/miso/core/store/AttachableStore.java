package uk.ac.bbsrc.tgac.miso.core.store;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;

public interface AttachableStore {
  
  public Attachable getManaged(Attachable object);

  public void save(Attachable object);

  public FileAttachment getAttachment(long attachmentId);

  public long getUsage(FileAttachment attachment);

  public void delete(FileAttachment attachment);

}
