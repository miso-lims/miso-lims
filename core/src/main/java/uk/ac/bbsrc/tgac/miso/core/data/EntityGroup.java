package uk.ac.bbsrc.tgac.miso.core.data;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.workflow.WorkflowAware;

import java.util.Date;
import java.util.Set;

/**
 * A grouping object to provide abstraction over other homogeneous objects, in order to facilitate some kind of
 * management procedure. EntityGroups are assignable, so that a group can represent a portion of work assigned to a User
 *
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
public interface EntityGroup<S> extends Nameable, Deletable, Assignable {
  public void setId(long groupId);
  public User getCreator();
  public void setCreator(User user);
  public Date getCreationDate();
  public void setCreationDate(Date creationDate);

  public Set<S> getEntities();
  public void setEntities(Set<S> entities);
  public void addEntity(S entity);
}