package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface ReferenceGenome extends Aliasable, Deletable, Serializable {

  void setAlias(String alias);

  String getDefaultSciName();

  void setDefaultSciName(String defaultSciName);

}
