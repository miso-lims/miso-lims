package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Collection;

public interface QualityControllable<Q extends QC> extends QualityControlEntity {
  public Collection<Q> getQCs();

}
