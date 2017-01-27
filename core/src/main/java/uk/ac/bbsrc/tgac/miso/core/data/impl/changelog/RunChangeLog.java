package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;

@Entity
@Table(name = "RunChangeLog")
public class RunChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long runChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = RunImpl.class)
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
