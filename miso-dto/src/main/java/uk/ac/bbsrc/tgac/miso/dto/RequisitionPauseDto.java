package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;

public class RequisitionPauseDto {

  private Long id;
  private String startDate;
  private String endDate;
  private String reason;

  public static RequisitionPauseDto from(RequisitionPause from) {
    RequisitionPauseDto to = new RequisitionPauseDto();
    setLong(to::setId, from.getId(), true);
    setDateString(to::setStartDate, from.getStartDate());
    setDateString(to::setEndDate, from.getEndDate());
    setString(to::setReason, from.getReason());
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public RequisitionPause to() {
    RequisitionPause to = new RequisitionPause();
    setLong(to::setId, getId(), false);
    setLocalDate(to::setStartDate, getStartDate());
    setLocalDate(to::setEndDate, getEndDate());
    setString(to::setReason, getReason());
    return to;
  }

}
