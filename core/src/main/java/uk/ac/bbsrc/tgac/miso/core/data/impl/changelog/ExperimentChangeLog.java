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
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;

@Entity
@Table(appliesTo = "ExperimentChangeLog", indexes = {
    @Index(name = "ExperimentChangeLog_experimentId_changeTime", columnNames = { "experimentId", "changeTime" }) })
public class ExperimentChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long experimentChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = ExperimentImpl.class)
  @JoinColumn(name = "experimentId", nullable = false, updatable = false)
  private Experiment experiment;

  @Override
  public Long getId() {
    return experiment.getId();
  }

  @Override
  public void setId(Long id) {
    experiment.setId(id);
  }

  public Long getExperimentChangeLogId() {
    return experimentChangeLogId;
  }

}
