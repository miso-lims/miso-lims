package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;

/**
 * uk.ac.bbsrc.tgac.miso.core.data
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
public class EntityGroupImpl<T extends Nameable, S extends Nameable> implements EntityGroup<T, S> {
  public static final Long UNSAVED_ID = 0L;
  private Set<S> entities = new HashSet<>();

  private long groupId;
  private String name;
  private T parent;

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public long getId() {
    return groupId;
  }

  @Override
  public void setId(long groupId) {
    this.groupId = groupId;
  }

  @Override
  public T getParent() {
    return parent;
  }

  @Override
  public void setParent(T parent) {
    this.parent = parent;
  }

  @Override
  public Set<S> getEntities() {
    return entities;
  }

  @Override
  public void setEntities(Set<S> entities) {
    this.entities = entities;
  }

  @Override
  public void addEntity(S entity) {
    this.entities.add(entity);
  }

  @Override
  public boolean isDeletable() {
    return getId() != EntityGroupImpl.UNSAVED_ID && getEntities().isEmpty();
  }
}
