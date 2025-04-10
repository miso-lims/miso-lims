package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class StorageLabel implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long labelId = UNSAVED_ID;

  private String label;

  @Override
  public long getId() {
    return labelId;
  }

  @Override
  public void setId(long id) {
    this.labelId = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Storage Label";
  }

  @Override
  public String getDeleteDescription() {
    return getLabel();
  }

}
