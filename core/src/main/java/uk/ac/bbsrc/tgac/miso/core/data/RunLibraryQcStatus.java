package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class RunLibraryQcStatus implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long statusId = UNSAVED_ID;

  private String description;
  private Boolean qcPassed;

  @Override
  public long getId() {
    return statusId;
  }

  @Override
  public void setId(long id) {
    this.statusId = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Run-Library QC Status";
  }

  @Override
  public String getDeleteDescription() {
    return getDescription();
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, qcPassed);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        RunLibraryQcStatus::getDescription,
        RunLibraryQcStatus::getQcPassed);
  }

}
