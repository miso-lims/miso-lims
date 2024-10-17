package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_Pool")
@Immutable
public class ListTransferViewPool extends ListTransferViewItem {

  private static final ListTransferViewProject PROJECT_PLACEHOLDER = new ListTransferViewProject() {

    private static final long serialVersionUID = 1L;

    private static final String CODE = "Undetermined (pool)";

    @Override
    public String getCode() {
      return CODE;
    }

  };

  public static class ListTransferViewPoolId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private long poolId;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public long getPoolId() {
      return poolId;
    }

    public void setPoolId(long poolId) {
      this.poolId = poolId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, poolId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          ListTransferViewPoolId::getTransfer,
          ListTransferViewPoolId::getPoolId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  private long poolId;

  @Override
  public Transfer getTransfer() {
    return transfer;
  }

  @Override
  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  @Override
  public long getItemId() {
    return poolId;
  }

  @Override
  public void setItemId(long id) {
    this.poolId = id;
  }

  @Override
  public ListTransferViewProject getProject() {
    return PROJECT_PLACEHOLDER;
  }

}
