package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;

public class QcTypeDto {

  private Long id;
  private String name;
  private String description;
  @Enumerated(EnumType.STRING)
  private QcTarget qcTarget;
  private String units;
  private Integer precisionAfterDecimal;
  private boolean archived;
  @Enumerated(EnumType.STRING)
  private QcCorrespondingField correspondingField;
  private boolean autoUpdateField;
  private Long instrumentModelId;
  private List<KitDescriptorDto> kitDescriptors;
  private List<QcControlDto> controls;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public QcTarget getQcTarget() {
    return qcTarget;
  }

  public void setQcTarget(QcTarget qcTarget) {
    this.qcTarget = qcTarget;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public Integer getPrecisionAfterDecimal() {
    return precisionAfterDecimal;
  }

  public void setPrecisionAfterDecimal(Integer precisionAfterDecimal) {
    this.precisionAfterDecimal = precisionAfterDecimal;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public QcCorrespondingField getCorrespondingField() {
    return correspondingField;
  }

  public void setCorrespondingField(QcCorrespondingField correspondingField) {
    this.correspondingField = correspondingField;
  }

  public boolean isAutoUpdateField() {
    return autoUpdateField;
  }

  public void setAutoUpdateField(boolean autoUpdateField) {
    this.autoUpdateField = autoUpdateField;
  }

  public Long getInstrumentModelId() {
    return instrumentModelId;
  }

  public void setInstrumentModelId(Long instrumentModelId) {
    this.instrumentModelId = instrumentModelId;
  }

  public List<KitDescriptorDto> getKitDescriptors() {
    return kitDescriptors;
  }

  public void setKitDescriptors(List<KitDescriptorDto> kitDescriptors) {
    this.kitDescriptors = kitDescriptors;
  }

  public List<QcControlDto> getControls() {
    return controls;
  }

  public void setControls(List<QcControlDto> controls) {
    this.controls = controls;
  }

}
