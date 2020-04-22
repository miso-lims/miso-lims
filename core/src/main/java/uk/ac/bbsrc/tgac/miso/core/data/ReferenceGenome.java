package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface ReferenceGenome extends Aliasable, Deletable, Serializable {

  public void setAlias(String alias);

  public ScientificName getDefaultScientificName();

  public void setDefaultScientificName(ScientificName defaultScientificName);

}
