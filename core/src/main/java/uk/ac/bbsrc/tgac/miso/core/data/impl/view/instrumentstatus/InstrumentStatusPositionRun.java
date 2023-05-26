package uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPositionRun.InstrumentStatusPositionRunId;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "InstrumentStatusPositionRunView")
@Immutable
@IdClass(InstrumentStatusPositionRunId.class)
public class InstrumentStatusPositionRun implements Serializable {

  public static class InstrumentStatusPositionRunId implements Serializable {

    private static final long serialVersionUID = 1L;

    private long runId;

    private long positionId;

    public long getRunId() {
      return runId;
    }

    public void setRunId(long runId) {
      this.runId = runId;
    }

    public long getPositionId() {
      return positionId;
    }

    public void setPositionId(long positionId) {
      this.positionId = positionId;
    }

    @Override
    public int hashCode() {
      return Objects.hash(positionId, runId);
    }

    @Override
    public boolean equals(Object obj) {
      return LimsUtils.equals(this, obj,
          InstrumentStatusPositionRunId::getPositionId,
          InstrumentStatusPositionRunId::getRunId);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  private long runId;

  @Id
  private long positionId;

  private long instrumentId;

  private String name;
  private String alias;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private HealthType health = HealthType.Unknown;

  private LocalDate startDate;
  private LocalDate completionDate;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Transient
  private List<InstrumentStatusPositionRunPool> pools;

  public long getRunId() {
    return runId;
  }

  public void setRunId(long runId) {
    this.runId = runId;
  }

  public long getPositionId() {
    return positionId;
  }

  public void setPositionId(long positionId) {
    this.positionId = positionId;
  }

  public long getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(long instrumentId) {
    this.instrumentId = instrumentId;
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

  public HealthType getHealth() {
    return health;
  }

  public void setHealth(HealthType health) {
    this.health = health;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getCompletionDate() {
    return completionDate;
  }

  public void setCompletionDate(LocalDate completionDate) {
    this.completionDate = completionDate;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public List<InstrumentStatusPositionRunPool> getPools() {
    return pools;
  }

  public void setPools(List<InstrumentStatusPositionRunPool> pools) {
    this.pools = pools;
  }

}
