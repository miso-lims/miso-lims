package uk.ac.bbsrc.tgac.miso.core.data.qc;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;

public interface QualityControlEntity extends Aliasable, ChangeLoggable {

  public QcTarget getQcTarget();

}
