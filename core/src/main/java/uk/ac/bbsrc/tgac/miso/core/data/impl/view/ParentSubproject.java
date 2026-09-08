package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "Subproject")
public class ParentSubproject implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long subprojectId;

  private String alias;
  private boolean priority;

  public long getId() {
    return subprojectId;
  }

  public void setId(long id) {
    this.subprojectId = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isPriority() {
    return priority;
  }

  public void setPriority(boolean priority) {
    this.priority = priority;
  }

}
