package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

@Entity
@Table(name = "RunChangeLog")
public class RunChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long runChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = Run.class)
  @JoinColumn(name = "runId", nullable = false, updatable = false)
  private Run run;

  @Override
  public Long getId() {
    return run.getId();
  }

  @Override
  public void setId(Long id) {
    run.setId(id);
  }

  public Long getRunChangeLogId() {
    return runChangeLogId;
  }

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

}
