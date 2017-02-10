package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "StudyType")
public class StudyType {
  @Id
  private long typeId;
  private String name;

  public long getId() {
    return typeId;
  }

  public String getName() {
    return name;
  }

  public void setId(long typeId) {
    this.typeId = typeId;
  }

  public void setName(String name) {
    this.name = name;
  }

}
