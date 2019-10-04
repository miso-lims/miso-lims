package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface HierarchyEntity extends Boxable, ChangeLoggable {

  public HierarchyEntity getParent();

  public BigDecimal getVolumeUsed();

  public void setVolumeUsed(BigDecimal volumeUsed);

}
