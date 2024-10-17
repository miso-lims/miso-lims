package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "LibraryDesignCode")
public class LibraryDesignCode implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long libraryDesignCodeId = UNSAVED_ID;

  @Column(unique = true, nullable = false)
  private String code;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Boolean targetedSequencingRequired;

  @Override
  public long getId() {
    return libraryDesignCodeId;
  };

  @Override
  public void setId(long id) {
    this.libraryDesignCodeId = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean isTargetedSequencingRequired() {
    return targetedSequencingRequired;
  }

  public void setTargetedSequencingRequired(Boolean targetedSequencingRequired) {
    this.targetedSequencingRequired = targetedSequencingRequired;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Design Code";
  }

  @Override
  public String getDeleteDescription() {
    return getCode() + " (" + getDescription() + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((targetedSequencingRequired == null) ? 0 : targetedSequencingRequired.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LibraryDesignCode other = (LibraryDesignCode) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (targetedSequencingRequired == null) {
      if (other.targetedSequencingRequired != null)
        return false;
    } else if (!targetedSequencingRequired.equals(other.targetedSequencingRequired))
      return false;
    return true;
  }

}
