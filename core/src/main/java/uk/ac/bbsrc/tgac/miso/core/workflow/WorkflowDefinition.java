package uk.ac.bbsrc.tgac.miso.core.workflow;

import com.eaglegenomics.simlims.core.User;

import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowDefinition extends Comparable {
  public long getId();
  public void setId(long workflowDefinitionId);

  public String getName();
  public void setName(String name);

  public String getDescription();
  public void setDescription(String description);

  public Date getCreationDate();
  public void setCreationDate(Date creationDate);

  public User getCreator();
  public void setCreator(User user);

  public Set<String> getStateFields();
  //public Set<String> getRequiredStateFields();
  public void setStateFields(Set<String> stateFields);

  public SortedMap<Integer, WorkflowProcessDefinition> getWorkflowProcessDefinitions();
  public void setWorkflowProcessDefinitions(SortedMap<Integer, WorkflowProcessDefinition> processMap);
  public int getProcessStage(WorkflowProcessDefinition wpd);
}
