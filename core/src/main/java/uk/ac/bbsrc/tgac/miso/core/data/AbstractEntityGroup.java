package uk.ac.bbsrc.tgac.miso.core.data;

import com.eaglegenomics.simlims.core.User;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Info
 *
 * @author Rob Davey
 * @date 20/06/14
 * @since 0.2.1-SNAPSHOT
 */
public abstract class AbstractEntityGroup<S> implements EntityGroup<S> {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long groupId;
  private String name;
  private Set<S> entities = new HashSet<>();
  private User assignee;
  private User creator;
  private Date creationDate;

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

  public void setId(long groupId) {
    this.groupId = groupId;
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
    return getId() != AbstractEntityGroup.UNSAVED_ID;
  }

  @Override
  public User getAssignee() {
    return assignee;
  }

  @Override
  public void setAssignee(User assignee) {
    this.assignee = assignee;
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
  public String getAssignableIdentifier() {
    return getName();
  }
}

