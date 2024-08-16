package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Immutable
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SequencingOrderFulfillmentView implements Serializable {

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
        SequencingOrderFulfillmentView::getId,
        SequencingOrderFulfillmentView::getFulfilled);
  }

}
