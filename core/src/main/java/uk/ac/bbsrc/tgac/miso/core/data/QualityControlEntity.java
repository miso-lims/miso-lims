package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

public interface QualityControlEntity extends Aliasable, SecurableByProfile {

  public QcTarget getQcTarget();

}
