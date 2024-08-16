package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SampleType implements Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long typeId = UNSAVED_ID;
  private String name;
  private boolean archived;

  @Override
  public long getId() {
    return typeId;
  }

  @Override
  public void setId(long id) {
    this.typeId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public String getDeleteType() {
    return "Sample Type";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }

}
