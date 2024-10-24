package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

@Entity
public class WorksetChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long worksetChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "worksetId", nullable = false, updatable = false)
  private Workset workset;

  @Override
  public Long getId() {
    return workset.getId();
  }

  @Override
  public void setId(Long id) {
    workset.setId(id);
  }

  public Long getWorksetChangeLogId() {
    return worksetChangeLogId;
  }

  public void setWorksetChangeLogId(Long worksetChangeLogId) {
    this.worksetChangeLogId = worksetChangeLogId;
  }

  public Workset getWorkset() {
    return workset;
  }

  public void setWorkset(Workset workset) {
    this.workset = workset;
  }

}
