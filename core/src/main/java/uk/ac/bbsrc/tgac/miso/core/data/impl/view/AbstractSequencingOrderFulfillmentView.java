package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class AbstractSequencingOrderFulfillmentView implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  private String orderSummaryId;

  private int fulfilled;

  public String getId() {
    return orderSummaryId;
  }

  public void setId(String id) {
    this.orderSummaryId = id;
  }

  public int getFulfilled() {
    return fulfilled;
  }

  public void setFulfilled(int fulfilled) {
    this.fulfilled = fulfilled;
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderSummaryId, fulfilled);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        AbstractSequencingOrderFulfillmentView::getId,
        AbstractSequencingOrderFulfillmentView::getFulfilled);
  }

}

