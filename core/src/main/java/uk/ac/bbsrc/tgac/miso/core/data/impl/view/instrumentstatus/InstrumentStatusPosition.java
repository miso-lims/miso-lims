package uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

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
