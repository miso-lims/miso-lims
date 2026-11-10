package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;

@Entity
@Immutable
@Table(name = "Project")
public class ProjectView implements Nameable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  private long projectId = UNSAVED_ID;
  private String name;
  private String code;

  @Enumerated(EnumType.STRING)
  private StatusType status;

  @Override
  public long getId() {
    return projectId;
  }

  @Override
  public void setId(long id) {
    this.projectId = id;
  }

  @Override
  public boolean isSaved() {
    return projectId != UNSAVED_ID;
  }

  @Override
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

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

}
