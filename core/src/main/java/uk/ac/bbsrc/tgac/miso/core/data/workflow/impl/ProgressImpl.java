package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Progress;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.Workflow.WorkflowName;

@Entity
@Table(name = "WorkflowProgress")
public class ProgressImpl implements Progress {
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long workflowProgressId = UNSAVED_ID;

  @Enumerated(EnumType.STRING)
  @Column(name = "workflowName")
  private WorkflowName workflowName;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "userId", nullable = false, updatable = false)
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false, updatable = false)
  private Date creationTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "lastModified", nullable = false)
  private Date lastModified;

  @OneToMany(targetEntity = AbstractProgressStep.class, mappedBy = "id.progress", fetch = FetchType.EAGER)
  @Sort(type = SortType.NATURAL)
  private SortedSet<ProgressStep> steps;

  @Override
  public long getId() {
    return workflowProgressId;
  }

  @Override
  public void setId(long id) {
    this.workflowProgressId = id;
  }

  @Override
  public WorkflowName getWorkflowName() {
    return workflowName;
  }

  @Override
  public void setWorkflowName(WorkflowName workflowName) {
    this.workflowName = workflowName;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public SortedSet<ProgressStep> getSteps() {
    return steps;
  }

  @Override
  public void setSteps(Collection<ProgressStep> steps) {
    this.steps = new TreeSet<>(steps);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
    result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    result = prime * result + (int) (workflowProgressId ^ (workflowProgressId >>> 32));
    result = prime * result + ((workflowName == null) ? 0 : workflowName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ProgressImpl other = (ProgressImpl) obj;
    if (creationTime == null) {
      if (other.creationTime != null) return false;
    } else if (!creationTime.equals(other.creationTime)) return false;
    if (lastModified == null) {
      if (other.lastModified != null) return false;
    } else if (!lastModified.equals(other.lastModified)) return false;
    if (user == null) {
      if (other.user != null) return false;
    } else if (!user.equals(other.user)) return false;
    if (workflowProgressId != other.workflowProgressId) return false;
    return workflowName == other.workflowName;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }
}
