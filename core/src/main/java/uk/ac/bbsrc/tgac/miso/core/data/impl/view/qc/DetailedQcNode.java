package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;

@MappedSuperclass
public abstract class DetailedQcNode implements QcNode {

  private static final long serialVersionUID = 1L;

  private String name;

  private String alias;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;

  private String detailedQcStatusNote;

  public abstract void setId(long id);

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  public void setDetailedQcStatusNote(String detailedQcStatusNote) {
    this.detailedQcStatusNote = detailedQcStatusNote;
  }

  @Override
  public String getLabel() {
    return getAlias();
  }

  @Override
  public Boolean getQcPassed() {
    return getDetailedQcStatus() == null ? null : getDetailedQcStatus().getStatus();
  }

  @Override
  public Long getQcStatusId() {
    return getDetailedQcStatus() == null ? null : getDetailedQcStatus().getId();
  }

  @Override
  public String getQcNote() {
    return getDetailedQcStatusNote();
  }

}
