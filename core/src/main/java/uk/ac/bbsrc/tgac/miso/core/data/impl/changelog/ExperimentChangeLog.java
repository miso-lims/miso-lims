package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;

@Entity
@Table(name = "ExperimentChangeLog", indexes = {
    @Index(name = "ExperimentChangeLog_experimentId_changeTime", columnList = "experimentId, changeTime")})
public class ExperimentChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long experimentChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = Experiment.class)
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

  public Experiment getExperiment() {
    return experiment;
  }

  public void setExperiment(Experiment experiment) {
    this.experiment = experiment;
  }

}
