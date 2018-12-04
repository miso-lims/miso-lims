package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPositionStatus.InstrumentPositionStatusId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

@Entity
@Immutable
@IdClass(InstrumentPositionStatusId.class)
public class InstrumentPositionStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  public static class InstrumentPositionStatusId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Instrument instrument;
    // "key" fields default to -1 if the foreign key is null. These are used because Hibernate doesn't accept nulls in the primary key
    private long positionKey;
    private long runKey;

    public Instrument getInstrument() {
      return instrument;
    }

    public void setInstrument(Instrument instrument) {
      this.instrument = instrument;
    }

    public long getPositionKey() {
      return positionKey;
    }

    public void setPositionKey(long positionKey) {
      this.positionKey = positionKey;
    }

    public long getRunKey() {
      return runKey;
    }

    public void setRunKey(long runKey) {
      this.runKey = runKey;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
      result = prime * result + (int) (positionKey ^ (positionKey >>> 32));
      result = prime * result + (int) (runKey ^ (runKey >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      InstrumentPositionStatusId other = (InstrumentPositionStatusId) obj;
      if (instrument == null) {
        if (other.instrument != null) return false;
      } else if (!instrument.equals(other.instrument)) return false;
      if (positionKey != other.positionKey) return false;
      if (runKey != other.runKey) return false;
      return true;
    }

  }

  @Id
  @ManyToOne(targetEntity = InstrumentImpl.class)
  @JoinColumn(name = "instrumentId", nullable = false)
  private Instrument instrument;

  @ManyToOne
  @JoinColumn(name = "positionId", nullable = true)
  private InstrumentPosition position;

  @Id
  private long positionKey;

  @ManyToOne
  @JoinColumn(name = "runId", nullable = true)
  private Run run;

  @Id
  private long runKey;

  public Instrument getInstrument() {
    return instrument;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public InstrumentPosition getPosition() {
    return position;
  }

  public void setPosition(InstrumentPosition position) {
    this.position = position;
  }

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

}
