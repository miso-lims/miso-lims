package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;

@Entity
@Table(name = "StepPositiveDouble")
public class PositiveDoubleProgressStep extends AbstractProgressStep {
  private static final long serialVersionUID = 1L;

  private double input;

  public double getInput() {
    return input;
  }

  public void setInput(double input) {
    this.input = input;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) input;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    PositiveDoubleProgressStep other = (PositiveDoubleProgressStep) obj;
    if (input != other.input) return false;
    return true;
  }

  @Override
  public void accept(WorkflowStep visitor) {
    visitor.processInput(this);
  }
}
