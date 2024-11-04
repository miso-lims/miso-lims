package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferPool.TransferPoolId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_Pool")
@IdClass(TransferPoolId.class)
public class TransferPool extends TransferItem<Pool> {

  public static class TransferPoolId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private Pool item;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public Pool getItem() {
      return item;
    }

    public void setItem(Pool item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          TransferPoolId::getTransfer,
          TransferPoolId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId")
  private Pool item;

  @Override
  public Transfer getTransfer() {
    return transfer;
  }

  @Override
  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  @Override
  public Pool getItem() {
    return item;
  }

  @Override
  public void setItem(Pool pool) {
    this.item = pool;
  }

}
