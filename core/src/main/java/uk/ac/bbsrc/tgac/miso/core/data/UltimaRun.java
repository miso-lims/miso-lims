package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunUltima")
public class UltimaRun extends Run {

  private Integer completedFlows;
  private Integer expectedFlows;
  private Integer waferShelf;
  private static final long serialVersionUID = 1L;

  public UltimaRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ULTIMA;
  }

  @Override
  public String getDeleteType() {
    return "Ultima Run";
  }

  public Integer getCompletedFlows() {
    return completedFlows;
  }

  public void setCompletedFlows(Integer completedFlows) {
    this.completedFlows = completedFlows;
  }

  public Integer getExpectedFlows() {
    return expectedFlows;
  }

  public void setExpectedFlows(Integer expectedFlows) {
    this.expectedFlows = expectedFlows;
  }

  public Integer getWaferShelf() {
    return waferShelf;
  }

  public void setWaferShelf(Integer waferShelf) {
    this.waferShelf = waferShelf;
  }
}
