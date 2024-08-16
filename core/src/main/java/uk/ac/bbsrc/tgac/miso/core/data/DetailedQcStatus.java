package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface DetailedQcStatus extends Deletable, Serializable, Timestamped {

  Boolean getStatus();

  void setStatus(Boolean status);

  String getDescription();

  void setDescription(String description);

  Boolean getNoteRequired();

  void setNoteRequired(Boolean noteRequired);

  boolean getArchived();

  void setArchived(boolean archived);

}
