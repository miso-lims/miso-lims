package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

@Entity
public class RequisitionChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long requisitionChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requisitionId", nullable = false, updatable = false)
  private Requisition requisition;

  @Override
  public Long getId() {
    return requisition.getId();
  }

  @Override
  public void setId(Long id) {
    requisition.setId(id);
  }

  public Long getRequisitionChangeLogId() {
    return requisitionChangeLogId;
  }

  public void setTransferChangeLogId(Long transferChangeLogId) {
    this.requisitionChangeLogId = transferChangeLogId;
  }

  public Requisition getRequisition() {
    return requisition;
  }

  public void setRequisition(Requisition requisition) {
    this.requisition = requisition;
  }

}
