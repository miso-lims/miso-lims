package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "OxfordNanoporeContainer")
public class OxfordNanoporeContainer extends SequencerPartitionContainerImpl {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "flowCellVersionId")
  private FlowCellVersion flowCellVersion;

  @ManyToOne
  @JoinColumn(name = "poreVersionId")
  private PoreVersion poreVersion;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date receivedDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date returnedDate;

  public FlowCellVersion getFlowCellVersion() {
    return flowCellVersion;
  }

  public void setFlowCellVersion(FlowCellVersion flowCellVersion) {
    this.flowCellVersion = flowCellVersion;
  }

  public PoreVersion getPoreVersion() {
    return poreVersion;
  }

  public void setPoreVersion(PoreVersion poreVersion) {
    this.poreVersion = poreVersion;
  }

  public Date getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(Date receivedDate) {
    this.receivedDate = receivedDate;
  }

  public Date getReturnedDate() {
    return returnedDate;
  }

  public void setReturnedDate(Date returnedDate) {
    this.returnedDate = returnedDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((flowCellVersion == null) ? 0 : flowCellVersion.hashCode());
    result = prime * result + ((poreVersion == null) ? 0 : poreVersion.hashCode());
    result = prime * result + ((receivedDate == null) ? 0 : receivedDate.hashCode());
    result = prime * result + ((returnedDate == null) ? 0 : returnedDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    OxfordNanoporeContainer other = (OxfordNanoporeContainer) obj;
    if (flowCellVersion == null) {
      if (other.flowCellVersion != null) return false;
    } else if (!flowCellVersion.equals(other.flowCellVersion)) return false;
    if (poreVersion == null) {
      if (other.poreVersion != null) return false;
    } else if (!poreVersion.equals(other.poreVersion)) return false;
    if (receivedDate == null) {
      if (other.receivedDate != null) return false;
    } else if (!receivedDate.equals(other.receivedDate)) return false;
    if (returnedDate == null) {
      if (other.returnedDate != null) return false;
    } else if (!returnedDate.equals(other.returnedDate)) return false;
    return true;
  }

}
