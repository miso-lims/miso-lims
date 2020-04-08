package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "Run")
@Immutable
public class ListContainerRunView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long runId;

  private String name;

  private String alias;

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @ManyToOne
  @JoinColumn(name = "instrumentId")
  private ListContainerRunSequencerView sequencer;

  public long getId() {
    return runId;
  }

  public void setId(long runId) {
    this.runId = runId;
  }

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

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public ListContainerRunSequencerView getSequencer() {
    return sequencer;
  }

  public void setSequencer(ListContainerRunSequencerView sequencer) {
    this.sequencer = sequencer;
  }

}