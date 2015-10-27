package uk.ac.bbsrc.tgac.miso.core.workflow;

import net.sf.json.JSONObject;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowProcess {
  public long getId();

  public JSONObject getState();

  public WorkflowProcessDefinition getDefinition();

  public boolean isStarted();

  public boolean isPaused();

  public boolean isCompleted();

  public boolean isFailed();
}
