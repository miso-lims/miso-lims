package uk.ac.bbsrc.tgac.miso.core.data.impl.transfer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class TransferItem<T extends Boxable> implements Serializable {

  private static final long serialVersionUID = 1L;

  private Boolean received;
  private Boolean qcPassed;
  private String qcNote;
  private BigDecimal distributedVolume;
  private String distributedBoxAlias;
  private String distributedBoxPosition;

  public abstract Transfer getTransfer();

  public abstract void setTransfer(Transfer transfer);

  public Boolean isReceived() {
    return received;
  }

  public void setReceived(Boolean received) {
    this.received = received;
  }

  public Boolean isQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

  public BigDecimal getDistributedVolume() {
    return distributedVolume;
  }

  public void setDistributedVolume(BigDecimal distributedVolume) {
    this.distributedVolume = distributedVolume;
  }

  public String getDistributedBoxAlias() {
    return distributedBoxAlias;
  }

  public void setDistributedBoxAlias(String distributedBoxAlias) {
    this.distributedBoxAlias = distributedBoxAlias;
  }

  public String getDistributedBoxPosition() {
    return distributedBoxPosition;
  }

  public void setDistributedBoxPosition(String distributedBoxPosition) {
    this.distributedBoxPosition = distributedBoxPosition;
  }

  public abstract T getItem();

  public abstract void setItem(T item);

  @Override
  public int hashCode() {
    return Objects.hash(received, qcPassed, qcNote, distributedVolume, distributedBoxAlias, distributedBoxPosition,
        getItem());
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        TransferItem::isReceived,
        TransferItem::isQcPassed,
        TransferItem::getQcNote,
        TransferItem::getDistributedVolume,
        TransferItem::getDistributedBoxAlias,
        TransferItem::getDistributedBoxPosition,
        TransferItem::getItem);
  }

}
