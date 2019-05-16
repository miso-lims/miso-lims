package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "StudyType")
public class StudyType implements Serializable, Identifiable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long typeId;
  private String name;

  @Override
  public long getId() {
    return typeId;
  }

  public String getName() {
    return name;
  }

  @Override
  public void setId(long typeId) {
    this.typeId = typeId;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isSaved() {
    return getId() == UNSAVED_ID;
  }

}
