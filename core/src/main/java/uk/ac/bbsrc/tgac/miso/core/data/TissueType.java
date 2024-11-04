package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface TissueType extends Deletable, Serializable, Timestamped {

  public String getAlias();

  public void setAlias(String alias);

  public String getDescription();

  public void setDescription(String description);

  public String getItemLabel();

}
