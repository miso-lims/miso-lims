package uk.ac.bbsrc.tgac.miso.core.data.workflow.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.WorkflowStep;

/**
 * Indicates that a user has skipped the current step
 */
@Entity
@Table(name = "StepSkip")
public class SkipProgressStep extends AbstractProgressStep {
  private static final long serialVersionUID = 1L;

  @Override
  public void accept(WorkflowStep visitor) {
    visitor.processInput(this);
  }
}
