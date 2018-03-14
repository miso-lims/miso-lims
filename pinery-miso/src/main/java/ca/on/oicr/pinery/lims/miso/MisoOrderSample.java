package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultOrderSample;

public class MisoOrderSample extends DefaultOrderSample {

  private Integer orderId;

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MisoOrderSample other = (MisoOrderSample) obj;
    if (orderId == null) {
      if (other.orderId != null) return false;
    } else if (!orderId.equals(other.orderId)) return false;
    return true;
  }

}
