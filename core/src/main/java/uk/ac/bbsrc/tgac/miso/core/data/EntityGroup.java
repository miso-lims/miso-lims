package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;
import java.util.Set;

import com.eaglegenomics.simlims.core.User;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
public interface EntityGroup<T, S> extends Nameable, Deletable {
  public void setId(long groupId);

  public T getParent();

  public void setParent(T overview);

  public Set<S> getEntities();

  public void setEntities(Set<S> entities);

  public void addEntity(S entity);
}