package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_Sample")
@Immutable
public class ListTransferViewSample extends ListTransferViewItem {

  public static class ListTransferViewSampleId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private long sampleId;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public long getSampleId() {
      return sampleId;
    }

    public void setSampleId(long sampleId) {
      this.sampleId = sampleId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, sampleId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          ListTransferViewSampleId::getTransfer,
          ListTransferViewSampleId::getSampleId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  private long sampleId;

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
    return sampleId;
  }

  @Override
  public void setItemId(long id) {
    this.sampleId = id;
  }

}
