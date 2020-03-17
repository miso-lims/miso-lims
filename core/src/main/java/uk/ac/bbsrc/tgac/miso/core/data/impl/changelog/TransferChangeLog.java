package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;

@Entity
public class TransferChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transferChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transferId", nullable = false, updatable = false)
  private Transfer transfer;

  @Override
  public Long getId() {
    return transfer.getId();
  }

  @Override
  public void setId(Long id) {
    transfer.setId(id);
  }

  public Long getTransferChangeLogId() {
    return transferChangeLogId;
  }

  public void setTransferChangeLogId(Long transferChangeLogId) {
    this.transferChangeLogId = transferChangeLogId;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

}
