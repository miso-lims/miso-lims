package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "OxfordNanoporeContainer")
public class OxfordNanoporeContainer extends SequencerPartitionContainerImpl {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "poreVersionId")
  private PoreVersion poreVersion;

  private LocalDate receivedDate;

  private LocalDate returnedDate;

  public PoreVersion getPoreVersion() {
    return poreVersion;
  }

  public void setPoreVersion(PoreVersion poreVersion) {
    this.poreVersion = poreVersion;
  }

  public LocalDate getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(LocalDate receivedDate) {
    this.receivedDate = receivedDate;
  }

  public LocalDate getReturnedDate() {
    return returnedDate;
  }

  public void setReturnedDate(LocalDate returnedDate) {
    this.returnedDate = returnedDate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((poreVersion == null) ? 0 : poreVersion.hashCode());
    result = prime * result + ((receivedDate == null) ? 0 : receivedDate.hashCode());
    result = prime * result + ((returnedDate == null) ? 0 : returnedDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    OxfordNanoporeContainer other = (OxfordNanoporeContainer) obj;
    if (poreVersion == null) {
      if (other.poreVersion != null)
        return false;
    } else if (!poreVersion.equals(other.poreVersion))
      return false;
    if (receivedDate == null) {
      if (other.receivedDate != null)
        return false;
    } else if (!receivedDate.equals(other.receivedDate))
      return false;
    if (returnedDate == null) {
      if (other.returnedDate != null)
        return false;
    } else if (!returnedDate.equals(other.returnedDate))
      return false;
    return true;
  }

}
