package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

/**
 * A lab within an Institute
 */
public interface Lab extends Serializable, Aliasable, Deletable, Timestamped {

  public void setAlias(String alias);

  public boolean isArchived();

  public void setArchived(boolean archived);

}
