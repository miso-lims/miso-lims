package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;

@MappedSuperclass
public abstract class ListTransferViewItem implements Serializable {

  private static final long serialVersionUID = 1L;

  private Boolean received;

  private Boolean qcPassed;

  public abstract Transfer getTransfer();

  public abstract void setTransfer(Transfer transfer);

  public abstract long getItemId();

  public abstract void setItemId(long id);

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

}
