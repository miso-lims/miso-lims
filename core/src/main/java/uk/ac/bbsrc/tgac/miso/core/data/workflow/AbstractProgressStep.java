package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.ProgressImpl;

@Entity
@Table(name = "WorkflowProgressStep")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractProgressStep implements ProgressStep {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private ProgressStepId id;

  public AbstractProgressStep() {
    this.id = new ProgressStepId();
  }

  @Override
  public ProgressStepId getId() {
    return id;
  }

  public void setId(ProgressStepId id) {
    this.id = id;
  }

  @Override
  public Progress getProgress() {
    return id.getProgress();
  }

  @Override
  public void setProgress(Progress progress) {
    this.id.setProgress(progress);
  }

  @Override
  public int getStepNumber() {
    return id.getStepNumber();
  }

  @Override
  public void setStepNumber(int stepNumber) {
    this.id.setStepNumber(stepNumber);
  }

  @Override
  public int compareTo(ProgressStep progressStep) {
    return Integer.compare(this.id.getStepNumber(), progressStep.getStepNumber());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    AbstractProgressStep other = (AbstractProgressStep) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Embeddable
  public static class ProgressStepId implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = ProgressImpl.class)
    @JoinColumn(name = "workflowProgressId")
    private Progress progress;

    @Column(nullable = false)
    private int stepNumber;

    public ProgressStepId() {

    }

    public ProgressStepId(Progress progress, int stepNumber) {
      this.progress = progress;
      this.stepNumber = stepNumber;
    }

    public Progress getProgress() {
      return progress;
    }

    public void setProgress(Progress progress) {
      this.progress = progress;
    }

    public int getStepNumber() {
      return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
      this.stepNumber = stepNumber;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((progress == null) ? 0 : progress.hashCode());
      result = prime * result + stepNumber;
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
      ProgressStepId other = (ProgressStepId) obj;
      if (progress == null) {
        if (other.progress != null)
          return false;
      } else if (!progress.equals(other.progress))
        return false;
      if (stepNumber != other.stepNumber)
        return false;
      return true;
    }
  }
}
