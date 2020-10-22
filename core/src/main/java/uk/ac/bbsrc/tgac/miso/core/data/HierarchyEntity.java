package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.core.data.qc.DetailedQcItem;

public interface HierarchyEntity extends Boxable, ChangeLoggable, DetailedQcItem {

  public HierarchyEntity getParent();

  public BigDecimal getVolumeUsed();

  public void setVolumeUsed(BigDecimal volumeUsed);

  public default HierarchyEntity getFailingParent() {
    if (getParent() != null) {
      HierarchyEntity parentValue = getParent().getFailingParent();
      if (parentValue != null) {
        return parentValue;
      }
      if (getParent().getDetailedQcStatus() != null && Boolean.FALSE.equals(getParent().getDetailedQcStatus().getStatus())) {
        return getParent();
      }
    }
    return null;
  }

}
