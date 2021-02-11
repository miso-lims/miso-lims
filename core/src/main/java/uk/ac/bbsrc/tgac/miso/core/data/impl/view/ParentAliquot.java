package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;

@Entity
@Immutable
@Table(name = "LibraryAliquot")
public class ParentAliquot implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long aliquotId;

  @ManyToOne
  @JoinColumn(name = "parentAliquotId")
  private ParentAliquot parentAliquot;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  public long getId() {
    return aliquotId;
  }

  public void setId(long id) {
    this.aliquotId = id;
  }

  public ParentAliquot getParentAliquot() {
    return parentAliquot;
  }

  public void setParentAliquot(ParentAliquot parentAliquot) {
    this.parentAliquot = parentAliquot;
  }

  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

}
