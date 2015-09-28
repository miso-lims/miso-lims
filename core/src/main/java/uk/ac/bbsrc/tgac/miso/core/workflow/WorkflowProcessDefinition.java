package uk.ac.bbsrc.tgac.miso.core.workflow;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.TypeProcessor;

import java.util.Date;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.workflow
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 15/03/13
 * @since 0.2.0
 */
public interface WorkflowProcessDefinition extends Comparable {
  public long getId();
  public void setId(long workflowProcessDefinitionId);

  public String getName();
  public void setName(String name);

  public String getDescription();
  public void setDescription(String description);

  public User getCreator();
  public void setCreator(User user);

  public Date getCreationDate();
  public void setCreationDate(Date creationDate);

  public Set<String> getStateFields();
  //public Set<String> getRequiredStateFields();
  public void setStateFields(Set<String> stateFields);

  public boolean requiresPreviousProcessComplete();

  public Class<? extends Nameable> getInputType();
  public void setInputType(Class<? extends Nameable> inputType);
  public Class<? extends Nameable> getOutputType();
  public void setOutputType(Class<? extends Nameable> outputType);

  public TypeProcessor<? extends Nameable, ? extends Nameable> getTypeProcessor();
  public void setTypeProcessor(TypeProcessor<? extends Nameable, ? extends Nameable> typeProcessor);

  public WorkflowProcess getInstance();
}
