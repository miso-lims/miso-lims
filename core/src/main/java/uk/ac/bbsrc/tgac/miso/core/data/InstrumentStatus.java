package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

@Entity
@Immutable
@Table(name = "InstrumentStats")
public class InstrumentStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long id;

  @ManyToOne(targetEntity = InstrumentImpl.class)
  @JoinColumn(name = "instrumentId", nullable = false)
  private Instrument instrument;

  @ManyToOne
  @JoinColumn(name = "runId", nullable = true)
  private Run run;

  private boolean outOfService;

  public long getId() {
    return id;
  }

  public Instrument getInstrument() {
    return instrument;
  }

  public Run getRun() {
    return run;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }
}
