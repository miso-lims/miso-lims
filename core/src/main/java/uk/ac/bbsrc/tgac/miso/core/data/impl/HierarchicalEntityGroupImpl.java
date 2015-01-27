package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractEntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchicalEntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;

/**
 * Info
 *
 * @author Rob Davey
 * @date 20/06/14
 * @since 0.2.1-SNAPSHOT
 */
public class HierarchicalEntityGroupImpl<T extends Nameable, S extends Nameable> extends AbstractEntityGroup<S> implements HierarchicalEntityGroup<T, S> {
  private T parent;

  @Override
  public T getParent() {
    return parent;
  }

  @Override
  public void setParent(T parent) {
    this.parent = parent;
  }
}
