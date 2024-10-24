package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
