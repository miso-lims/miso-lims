package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PartitionQCType")
public class PartitionQCType implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(nullable = false)

  private String description;

  private boolean noteRequired;

  @Id
  private long partitionQcTypeId;

  public String getDescription() {
    return description;
  }

  public long getId() {
    return partitionQcTypeId;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long partitionQcTypeId) {
    this.partitionQcTypeId = partitionQcTypeId;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }
}
