package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import java.util.List;
import java.util.stream.Collectors;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;

public class RequisitionDto {

  private Long id;
  private String alias;
  private List<Long> assayIds;
  private boolean stopped = false;
  private String stopReason;
  private String creationTime;
  private String lastModified;
  private List<RequisitionPauseDto> pauses;

  public static RequisitionDto from(Requisition from) {
    RequisitionDto to = new RequisitionDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    to.setAssayIds(from.getAssays().stream().map(Assay::getId).toList());
    setBoolean(to::setStopped, from.isStopped(), false);
    setString(to::setStopReason, from.getStopReason());
    setDateTimeString(to::setCreationTime, from.getCreationTime());
    setDateTimeString(to::setLastModified, from.getLastModified());
    to.setPauses(from.getPauses().stream().map(RequisitionPauseDto::from).toList());
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

  public List<Long> getAssayIds() {
    return assayIds;
  }

  public void setAssayIds(List<Long> assayIds) {
    this.assayIds = assayIds;
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

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String created) {
    this.creationTime = created;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public List<RequisitionPauseDto> getPauses() {
    return pauses;
  }

  public void setPauses(List<RequisitionPauseDto> pauses) {
    this.pauses = pauses;
  }

  public Requisition to() {
    Requisition to = new Requisition();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    if (getAssayIds() != null) {
      to.setAssays(getAssayIds().stream()
          .map(id -> {
            Assay assay = new Assay();
            assay.setId(id);
            return assay;
          })
          .collect(Collectors.toSet()));
    }
    setBoolean(to::setStopped, isStopped(), false);
    setString(to::setStopReason, getStopReason());
    if (getPauses() != null) {
      to.setPauses(getPauses().stream().map(RequisitionPauseDto::to).toList());
    }
    return to;
  }

}
