package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface ReferenceGenome extends Identifiable, Serializable {

  String getAlias();

  void setAlias(String alias);

  String getDefaultSciName();

  void setDefaultSciName(String defaultSciName);

}
