package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.Serializable;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.AbstractProgressStep.ProgressStepId;

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
        "Sequencing Container", FactoryType.BARCODABLE,
        EntityType.CONTAINER), STRING("String", FactoryType.STRING, null), SKIP("SKIP", FactoryType.SKIP, null), SEQUENCING_CONTAINER_MODEL(
            "Sequencing Container Model", FactoryType.BARCODABLE, EntityType.CONTAINER_MODEL), SAMPLE("Sample", FactoryType.BARCODABLE, EntityType.SAMPLE),
    POSITIVE_DOUBLE("Positive Double", FactoryType.POSITIVE_DOUBLE, null), POSITIVE_INTEGER("Positive Integer", FactoryType.POSITIVE_INTEGER, null), SAMPLE_STOCK("Stock", FactoryType.STOCK, EntityType.SAMPLE);

    private final String name;
    private final FactoryType factoryType;
    private final EntityType entityType;

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
    SKIP, STOCK, BARCODABLE, POSITIVE_INTEGER, INTEGER, POSITIVE_DOUBLE, STRING
  }
}
