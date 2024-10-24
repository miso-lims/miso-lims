package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;

@Entity
public class ProjectChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long projectChangeLogId;

  @ManyToOne(targetEntity = ProjectImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "projectId", nullable = false, updatable = false)
  private Project project;

  @Override
  public Long getId() {
    return project.getId();
  }

  @Override
  public void setId(Long id) {
    project.setId(id);
  }

  public Long getProjectChangeLogId() {
    return projectChangeLogId;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

}
