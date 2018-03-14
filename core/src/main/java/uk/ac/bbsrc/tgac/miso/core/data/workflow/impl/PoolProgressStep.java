package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;

@Entity
@Table(name = "StepPool")
public class PoolProgressStep extends AbstractProgressStep {
  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "poolId")
  private Pool input;

  public Pool getInput() {
    return input;
  }

  public void setInput(Pool input) {
    this.input = input;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((input == null) ? 0 : input.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PoolProgressStep other = (PoolProgressStep) obj;
    if (input == null) {
      if (other.input != null) return false;
    } else if (!input.equals(other.input)) return false;
    return true;
  }

  @Override
  public void accept(WorkflowStep visitor) {
    visitor.processInput(this);
  }
}