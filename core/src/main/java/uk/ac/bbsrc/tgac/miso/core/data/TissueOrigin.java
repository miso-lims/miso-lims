package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface TissueOrigin extends Aliasable, Deletable, Serializable, Timestamped {

  public void setAlias(String alias);

  public String getDescription();

  public void setDescription(String description);

  public String getItemLabel();

}
