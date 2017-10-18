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

}
