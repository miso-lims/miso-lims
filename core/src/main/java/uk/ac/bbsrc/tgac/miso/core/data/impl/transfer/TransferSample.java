package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample.TransferSampleId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "Transfer_Sample")
@IdClass(TransferSampleId.class)
public class TransferSample extends TransferItem<Sample> {

  public static class TransferSampleId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Transfer transfer;
    private Sample item;

    public Transfer getTransfer() {
      return transfer;
    }

    public void setTransfer(Transfer transfer) {
      this.transfer = transfer;
    }

    public Sample getItem() {
      return item;
    }

    public void setItem(Sample item) {
      this.item = item;
    }

    @Override
    public int hashCode() {
      return Objects.hash(transfer, item);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          TransferSampleId::getTransfer,
          TransferSampleId::getItem);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "transferId")
  private Transfer transfer;

  @Id
  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sampleId")
  private Sample item;

  @Override
  public Transfer getTransfer() {
    return transfer;
  }

  @Override
  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  @Override
  public Sample getItem() {
    return item;
  }

  @Override
  public void setItem(Sample sample) {
    this.item = sample;
  }

}
