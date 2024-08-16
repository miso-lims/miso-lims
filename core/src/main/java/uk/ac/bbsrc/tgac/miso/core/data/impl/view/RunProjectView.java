package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@Immutable
public class RunProjectView implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private long runId;

  private String projects;

  public long getRunId() {
    return runId;
  }

  public void setRunId(long runId) {
    this.runId = runId;
  }

  public String getProjects() {
    return projects;
  }

  public void setProjects(String projects) {
    this.projects = projects;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((projects == null) ? 0 : projects.hashCode());
    result = prime * result + (int) (runId ^ (runId >>> 32));
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
    RunProjectView other = (RunProjectView) obj;
    if (projects == null) {
      if (other.projects != null)
        return false;
    } else if (!projects.equals(other.projects))
      return false;
    if (runId != other.runId)
      return false;
    return true;
  }

}
