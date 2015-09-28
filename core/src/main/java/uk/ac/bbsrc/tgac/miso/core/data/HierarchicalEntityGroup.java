package uk.ac.bbsrc.tgac.miso.core.data;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
public interface HierarchicalEntityGroup<T, S> extends EntityGroup<S> {
  public T getParent();
  public void setParent(T parent);
}