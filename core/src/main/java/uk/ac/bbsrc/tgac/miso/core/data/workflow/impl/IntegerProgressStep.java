package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;

@Entity
@Table(name = "StepInteger")
public class IntegerProgressStep extends AbstractProgressStep {
  private static final long serialVersionUID = 1L;

  private int input;

  public int getInput() {
    return input;
  }

  public void setInput(int input) {
    this.input = input;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + input;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    IntegerProgressStep other = (IntegerProgressStep) obj;
    if (input != other.input)
      return false;
    return true;
  }

  @Override
  public void accept(WorkflowStep visitor) {
    visitor.processInput(this);
  }
}
