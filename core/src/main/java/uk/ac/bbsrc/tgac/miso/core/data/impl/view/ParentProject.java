package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "Project")
public class ParentProject implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long projectId;

  private String name;
  private String code;
  private String title;

  public long getId() {
    return projectId;
  }

  public void setId(long id) {
    this.projectId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
