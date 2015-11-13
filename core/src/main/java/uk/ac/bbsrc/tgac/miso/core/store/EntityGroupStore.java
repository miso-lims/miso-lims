package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;

/**
 * uk.ac.bbsrc.tgac.miso.core.store
 * <p/>
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 04/11/13
 * @since version
 */
public interface EntityGroupStore
    extends Store<EntityGroup<? extends Nameable, ? extends Nameable>>, Remover<EntityGroup<? extends Nameable, ? extends Nameable>> {

  @Override
  EntityGroup<? extends Nameable, ? extends Nameable> lazyGet(long sampleId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<EntityGroup<? extends Nameable, ? extends Nameable>>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<EntityGroup<? extends Nameable, ? extends Nameable>> listAllWithLimit(long limit) throws IOException;

  <T extends Nameable, S extends Nameable> EntityGroup<T, S> getEntityGroupByParentTypeAndId(Class<? extends T> parentType, long parentId)
      throws IOException, SQLException;

  <T extends Nameable, S extends Nameable> EntityGroup<T, S> getEntityGroupByParent(T parent, Class<? extends T> parentClz)
      throws IOException, SQLException;
}
