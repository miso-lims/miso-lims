package uk.ac.bbsrc.tgac.miso.core.workflow;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowProcessDefinition {
  public String getName();

  public String getDescription();

  public boolean requiresPreviousProcessComplete();

  public WorkflowProcessDefinition processBefore();

  public WorkflowProcessDefinition processAfter();
}
