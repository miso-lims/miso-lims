package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import static uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep.ProgressStepId;

import java.io.Serializable;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;

/**
 * Holds the data for a single workflow step.  Each input should have its own step.
 */
public interface ProgressStep extends Serializable, Comparable<ProgressStep> {
  ProgressStepId getId();

  Progress getProgress();

  void setProgress(Progress progress);

  int getStepNumber();

  void setStepNumber(int stepNumber);

  /**
   * Part of the Visitor Pattern to use WorkflowStep to validate ProgressStep.
   * All implementations of this method should call {@code visitor.processInput(this)}
   * @param visitor WorkflowStep used to validate {@code this}
   */
  void accept(WorkflowStep visitor);

  enum InputType {
    POOL("Pool", FactoryType.BARCODABLE, EntityType.POOL), INTEGER("Integer", FactoryType.INTEGER, null), SEQUENCER_PARTITION_CONTAINER(
        "Sequencing Container", FactoryType.BARCODABLE, EntityType.CONTAINER), STRING("String", FactoryType.STRING, null);

    private String name;
    private FactoryType factoryType;
    private EntityType entityType;

    InputType(String name, FactoryType factoryType, EntityType entityType) {
      this.name = name;
      this.factoryType = factoryType;
      this.entityType = entityType;
    }

    public EntityType getEntityType() {
      return entityType;
    }

    public FactoryType getFactoryType() {
      return factoryType;
    }

    public String getName() {
      return name;
    }
  }

  enum FactoryType {
    // Must be declared in the intended order to be applied to construct a ProgressStep
    BARCODABLE, INTEGER, STRING
  }
}
