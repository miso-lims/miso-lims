package uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.MappedSuperclass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@MappedSuperclass
public abstract class ListTransferViewItem implements Serializable {

  private static final long serialVersionUID = 1L;

  private Boolean received;

  private Boolean qcPassed;

  public abstract Transfer getTransfer();

  public abstract void setTransfer(Transfer transfer);

  public abstract long getItemId();

  public abstract void setItemId(long id);

  public abstract ListTransferViewProject getProject();

  public String getProjectLabel() {
    ListTransferViewProject project = getProject();
    return project.getCode() == null ? project.getName() : project.getCode();
  }

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

  @Override
  public int hashCode() {
    return Objects.hash(received, qcPassed, getItemId(), getTransfer());
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        ListTransferViewItem::isReceived,
        ListTransferViewItem::isQcPassed,
        ListTransferViewItem::getItemId,
        ListTransferViewItem::getTransfer);
  }

}
