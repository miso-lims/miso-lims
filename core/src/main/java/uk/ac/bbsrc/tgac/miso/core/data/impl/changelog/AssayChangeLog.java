package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;

@Entity
public class AssayChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long assayChangeLogId;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assayId", nullable = false, updatable = false)
  private Assay assay;

  @Override
  public Long getId() {
    return assay.getId();
  }

  @Override
  public void setId(Long id) {
    assay.setId(id);
  }

  public Long getAssayChangeLogId() {
    return assayChangeLogId;
  }

  public void setAssayChangeLogId(Long assayChangeLogId) {
    this.assayChangeLogId = assayChangeLogId;
  }

  public Assay getAssay() {
    return assay;
  }

  public void setAssay(Assay assay) {
    this.assay = assay;
  }

}
