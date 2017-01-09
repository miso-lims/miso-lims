package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

@Entity
@Table(appliesTo = "RunChangeLog", indexes = { @Index(name = "RunChangeLog_runId_changeTime", columnNames = { "runId", "changeTime" }) })
public class RunChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long runChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = AbstractRun.class)
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

}
