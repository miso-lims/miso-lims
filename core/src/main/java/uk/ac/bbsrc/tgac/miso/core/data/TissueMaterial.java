package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface TissueMaterial extends Serializable, Aliasable, Deletable, Timestamped {

  void setAlias(String alias);

}