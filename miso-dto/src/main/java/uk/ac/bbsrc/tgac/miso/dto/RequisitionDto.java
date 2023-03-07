package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

public class RequisitionDto {

  private Long id;
  private String alias;
  private Long assayId;
  private boolean stopped = false;
  private String stopReason;
  private String lastModified;

  public static RequisitionDto from(Requisition from) {
    RequisitionDto to = new RequisitionDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    Dtos.setId(to::setAssayId, from.getAssay());
    setBoolean(to::setStopped, from.isStopped(), false);
    setString(to::setStopReason, from.getStopReason());
    setDateTimeString(to::setLastModified, from.getLastModified());
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Long getAssayId() {
    return assayId;
  }

  public void setAssayId(Long assayId) {
    this.assayId = assayId;
  }

  public boolean isStopped() {
    return stopped;
  }

  public void setStopped(boolean stopped) {
    this.stopped = stopped;
  }

  public String getStopReason() {
    return stopReason;
  }

  public void setStopReason(String stopReason) {
    this.stopReason = stopReason;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public Requisition to() {
    Requisition to = new Requisition();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    setObject(to::setAssay, Assay::new, getAssayId());
    setBoolean(to::setStopped, isStopped(), false);
    setString(to::setStopReason, getStopReason());
    return to;
  }

}
