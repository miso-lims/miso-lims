package uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition.InstrumentStatusPositionId;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "InstrumentStatusPositionView")
@Immutable
@IdClass(InstrumentStatusPositionId.class)
public class InstrumentStatusPosition implements Serializable {

  public static class InstrumentStatusPositionId implements Serializable {

    private static final long serialVersionUID = 1L;

    private InstrumentStatus instrumentStatus;

    private long positionId;

    public InstrumentStatus getInstrumentStatus() {
      return instrumentStatus;
    }

    public void setInstrumentStatus(InstrumentStatus instrumentStatus) {
      this.instrumentStatus = instrumentStatus;
    }

    public long getPositionId() {
      return positionId;
    }

    public void setPositionId(long positionId) {
      this.positionId = positionId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(instrumentStatus, positionId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          InstrumentStatusPositionId::getInstrumentStatus,
          InstrumentStatusPositionId::getPositionId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne
  @JoinColumn(name = "instrumentId")
  private InstrumentStatus instrumentStatus;

  @Id
  private long positionId = -1;

  private String alias;

  @Temporal(TemporalType.TIMESTAMP)
  private Date outOfServiceTime;

  @Transient
  private InstrumentStatusPositionRun run;

  public InstrumentStatus getInstrumentStatus() {
    return instrumentStatus;
  }

  public void setInstrumentStatus(InstrumentStatus instrumentStatus) {
    this.instrumentStatus = instrumentStatus;
  }

  public long getPositionId() {
    return positionId;
  }

  public void setPositionId(long positionId) {
    this.positionId = positionId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Date getOutOfServiceTime() {
    return outOfServiceTime;
  }

  public void setOutOfServiceTime(Date outOfServiceTime) {
    this.outOfServiceTime = outOfServiceTime;
  }

  public InstrumentStatusPositionRun getRun() {
    return run;
  }

  public void setRun(InstrumentStatusPositionRun run) {
    this.run = run;
  }

}
