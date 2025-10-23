package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultRunContainer;

public class MisoRunContainer extends DefaultRunContainer {

  private Integer containerId;

  public Integer getContainerId() {
    return containerId;
  }

  public void setContainerId(Integer containerId) {
    this.containerId = containerId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    MisoRunContainer other = (MisoRunContainer) obj;
    if (containerId == null) {
      if (other.containerId != null)
        return false;
    } else if (!containerId.equals(other.containerId))
      return false;
    return true;
  }

}
