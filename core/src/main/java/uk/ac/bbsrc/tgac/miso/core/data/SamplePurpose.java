package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface SamplePurpose extends Serializable, Aliasable, Deletable, Timestamped {

  void setAlias(String alias);

  boolean isArchived();

  void setArchived(boolean archived);

}