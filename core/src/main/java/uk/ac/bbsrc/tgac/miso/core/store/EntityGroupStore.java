package uk.ac.bbsrc.tgac.miso.core.store;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchicalEntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

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
public interface EntityGroupStore extends Store<HierarchicalEntityGroup<? extends Nameable, ? extends Nameable>>, Remover<HierarchicalEntityGroup<? extends Nameable, ? extends Nameable>> {
  HierarchicalEntityGroup<? extends Nameable, ? extends Nameable> lazyGet(long entityGroupId) throws IOException;

  /**
   * List all persisted objects
   *
   * @return Collection<EntityGroup<? extends Nameable>>
   * @throws IOException when the objects cannot be retrieved
   */
  Collection<HierarchicalEntityGroup<? extends Nameable, ? extends Nameable>> listAllWithLimit(long limit) throws IOException;

  Collection<HierarchicalEntityGroup<? extends Nameable, ? extends Nameable>> listByAssignee(User assignee) throws IOException;

  Collection<HierarchicalEntityGroup<? extends Nameable, ? extends Nameable>> listByCreator(User creator) throws IOException;

  <T extends Nameable, S extends Nameable> Collection<HierarchicalEntityGroup<T, S>> listByEntityType(Class<? extends T> parentType, Class<? extends S> entityType) throws IOException;

  <T extends Nameable, S extends Nameable> HierarchicalEntityGroup<T, S> getEntityGroupByParentTypeAndId(Class<? extends T> parentType, long parentId) throws IOException, SQLException;

  <T extends Nameable, S extends Nameable> HierarchicalEntityGroup<T, S> getEntityGroupByParent(T parent, Class<? extends T> parentClz) throws IOException, SQLException;
}
