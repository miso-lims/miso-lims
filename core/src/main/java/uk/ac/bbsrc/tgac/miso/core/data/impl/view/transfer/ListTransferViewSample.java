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

  @ManyToOne
  @JoinColumn(name = "sampleId")
  private ListTransferViewSampleParent sample;

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

  public ListTransferViewSampleParent getSample() {
    return sample;
  }

  public void setSample(ListTransferViewSampleParent sample) {
    this.sample = sample;
  }

  @Override
  public ListTransferViewProject getProject() {
    return getSample().getProject();
  }

}
