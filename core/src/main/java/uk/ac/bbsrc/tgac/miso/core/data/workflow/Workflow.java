package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.LoadSequencerWorkflow;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.impl.SamplesReceivedWorkflow;

public interface Workflow {
  Progress getProgress();

  /**
   * setProgress must be called exactly once immediately after creating a Workflow
   */
  void setProgress(Progress progress);

  /**
   * @param stepNumber step index
   */
  WorkflowStepPrompt getStep(int stepNumber);

  /**
   * @return whether input has been received for all steps
   */
  boolean isComplete();

  /**
   * @return list of log messages for each step
   */
  List<String> getLog();

  /**
   * Validate and store input for a step identified by the 0-indexed stepNumber.
   * If stepNumber refers to a previous step, an implementation may or may not choose to invalidate future steps.
   */
  void processInput(int stepNumber, ProgressStep step);

  /**
   * Removes the latest step and any effects it caused.
   * Has no effect if no input has been processed.
   */
  void cancelInput();

  /**
   * @return short message describing what will be executed
   */
  String getConfirmMessage();

  void execute(WorkflowExecutor workflowExecutor) throws IOException;


  /**
   * @return the stepNumber of the next step that requires user input or null if the workflow is complete
   */
  Integer getNextStepNumber();

  /**
   * Represents a type of Workflow.  Should have a one-to-one correspondence with every implementation of Workflow.
   * All Workflows should be created through a call to createWorkflow(Progress)
   */
  enum WorkflowName {
    LOAD_SEQUENCER {
      @Override
      public Workflow createWorkflow() {
        return new LoadSequencerWorkflow();
      }

      @Override
      public final String getDescription() {
        return "Load Sequencer Workflow";
      }
    },
    SAMPLES_RECEIVED {
      @Override
      public Workflow createWorkflow() {
        return new SamplesReceivedWorkflow();
      }

      @Override
      public final String getDescription() {
        return "Samples Received Workflow";
      }
    };

    public Workflow createWorkflow(Progress progress) {
      Workflow workflow = createWorkflow();
      workflow.setProgress(progress);
      return workflow;
    }

    protected abstract Workflow createWorkflow();

    public abstract String getDescription();
  }
}
