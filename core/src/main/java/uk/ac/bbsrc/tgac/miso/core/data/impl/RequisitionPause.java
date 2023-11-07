package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class RequisitionPause implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long pauseId = UNSAVED_ID;

  private LocalDate startDate;
  private LocalDate endDate;
  private String reason;

  @Override
  public long getId() {
    return pauseId;
  }

  @Override
  public void setId(long id) {
    this.pauseId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pauseId, startDate, endDate, reason);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RequisitionPause::getId,
        RequisitionPause::getStartDate,
        RequisitionPause::getEndDate,
        RequisitionPause::getReason);
  }

}
