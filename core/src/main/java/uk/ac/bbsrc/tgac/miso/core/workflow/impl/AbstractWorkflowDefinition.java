package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowDefinition;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.util.*;

/**
 * Default implementation of a WorkflowDefinition
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1
 */
public abstract class AbstractWorkflowDefinition implements WorkflowDefinition {
  private long workflowDefinitionId;
  private String name;
  private String description;
  private Date creationDate;
  private User creator;
  private Set<String> stateFields = new HashSet<>();

  protected SortedMap<Integer, WorkflowProcessDefinition> workflowProcessDefinitions = new TreeMap<>();

  public static final Long UNSAVED_ID = 0L;

  public AbstractWorkflowDefinition(SortedMap<Integer, WorkflowProcessDefinition> workflowProcessDefinitions) {
    this.workflowProcessDefinitions = workflowProcessDefinitions;
  }

  @Override
  public long getId() {
    return workflowDefinitionId;
  }

  @Override
  public void setId(long workflowDefinitionId) {
    this.workflowDefinitionId = workflowDefinitionId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Set<String> getStateFields() {
    return stateFields;
  }

  @Override
  public void setStateFields(Set<String> stateFields) {
    this.stateFields = stateFields;
  }

  @Override
  public SortedMap<Integer, WorkflowProcessDefinition> getWorkflowProcessDefinitions() {
    return workflowProcessDefinitions;
  }

  public int getProcessStage(WorkflowProcessDefinition wpd) {
    for (Map.Entry<Integer, WorkflowProcessDefinition> entry : getWorkflowProcessDefinitions().entrySet()) {
      if (wpd.equals(entry.getValue())) {
        return entry.getKey()+1;
      }
    }
    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof WorkflowDefinition))
      return false;
    WorkflowDefinition them = (WorkflowDefinition) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractWorkflowDefinition.UNSAVED_ID
        || them.getId() == AbstractWorkflowDefinition.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      }
      return false;
    }
    else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (this.getId() != AbstractWorkflowDefinition.UNSAVED_ID) {
      return (int)getId();
    }
    else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    WorkflowDefinition t = (WorkflowDefinition)o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getName())
      .append(" : ")
      .append(this.getDescription())
      .append(" : ")
      .append(this.getCreator())
      .append(" : ")
      .append(this.getCreationDate())
      .append(" : keys [")
      .append(LimsUtils.join(this.getStateFields(), ","))
      .append("] : processes [")
      .append(LimsUtils.join(this.getWorkflowProcessDefinitions().values(), ","))
      .append("]");
    return sb.toString();
  }
}