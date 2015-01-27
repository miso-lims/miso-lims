package uk.ac.bbsrc.tgac.miso.core.workflow.impl;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.TypeProcessor;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcess;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowProcessDefinition;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of a WorkflowProcessDefinition
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1
 */
public class WorkflowProcessDefinitionImpl implements WorkflowProcessDefinition {
  private long workflowProcessDefinitionId;
  private String name;
  private String description;
  private boolean requiresPreviousProcessComplete;
  private Date creationDate;
  private User creator;
  private Set<String> stateFields = new HashSet<>();
  private Class<? extends Nameable> inputType;
  private Class<? extends Nameable> outputType;
  private TypeProcessor<? extends Nameable, ? extends Nameable> typeProcessor;
  public static final Long UNSAVED_ID = 0L;

  @Override
  public long getId() {
    return workflowProcessDefinitionId;
  }

  @Override
  public void setId(long workflowProcessDefinitionId) {
    this.workflowProcessDefinitionId = workflowProcessDefinitionId;
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
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
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
  public Set<String> getStateFields() {
    return stateFields;
  }

  @Override
  public void setStateFields(Set<String> stateFields) {
    this.stateFields = stateFields;
  }

  @Override
  public boolean requiresPreviousProcessComplete() {
    return requiresPreviousProcessComplete;
  }

  @Override
  public Class<? extends Nameable> getInputType() {
    return inputType;
  }

  @Override
  public void setInputType(Class<? extends Nameable> inputType) {
    this.inputType = inputType;
  }

  @Override
  public Class<? extends Nameable> getOutputType() {
    return outputType;
  }

  @Override
  public void setOutputType(Class<? extends Nameable> outputType) {
    this.outputType = outputType;
  }

  @Override
  public TypeProcessor<? extends Nameable, ? extends Nameable> getTypeProcessor() {
    return typeProcessor;
  }

  @Override
  public void setTypeProcessor(TypeProcessor<? extends Nameable, ? extends Nameable> typeProcessor) {
    /* check I/O types match processor types?
    Type in = typeProcessor.getClass().getTypeParameters()[0];
    Type out = typeProcessor.getClass().getTypeParameters()[1];

    boolean inOK = true;
    boolean outOK = true;

    if (getInputType() != null) {
      inOK = !getInputType().getGenericInterfaces()[0].equals(in);
    }

    if (getOutputType() != null) {
      outOK = !getOutputType().getGenericInterfaces()[0].equals(out);
    }

    if (inOK && outOK)
    */
    this.typeProcessor = typeProcessor;
  }

  @Override
  public WorkflowProcess getInstance() {
    return new WorkflowProcessImpl(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof WorkflowProcessDefinition))
      return false;
    WorkflowProcessDefinition them = (WorkflowProcessDefinition) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == WorkflowProcessDefinitionImpl.UNSAVED_ID
        || them.getId() == WorkflowProcessDefinitionImpl.UNSAVED_ID) {
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
    if (this.getId() != WorkflowProcessDefinitionImpl.UNSAVED_ID) {
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
    WorkflowProcessDefinition t = (WorkflowProcessDefinition)o;
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
      .append(this.getCreationDate());

    if (getInputType() != null) sb.append(" : ").append(getInputType().getSimpleName());
    if (getOutputType() != null) sb.append(" : ").append(getOutputType().getSimpleName());

    sb.append(" : keys [")
      .append(LimsUtils.join(this.getStateFields(), ","))
      .append("]");
    return sb.toString();
  }
}
