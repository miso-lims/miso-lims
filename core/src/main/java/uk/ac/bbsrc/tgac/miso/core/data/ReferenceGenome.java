package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface ReferenceGenome extends Serializable {

  Long getId();

  void setId(Long referenceGenomeId);

  String getAlias();

  void setAlias(String alias);


}
