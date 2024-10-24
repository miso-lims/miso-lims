package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Timestamped;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Immutable
public class ListWorksetView implements Aliasable, Serializable, Timestamped {

  private static final long serialVersionUID = 1L;

  @Id
  private long worksetId;

  private String alias;
  private int itemCount;
  private String description;
  private String category;
  private String stage;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Override
  public long getId() {
    return worksetId;
  }

  @Override
  public void setId(long id) {
    this.worksetId = id;
  }

  @Override
  public boolean isSaved() {
    return true;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public int getItemCount() {
    return itemCount;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User user) {
    this.creator = user;
  }

  @Override
  public Date getCreationTime() {
    return created;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.created = creationTime;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User user) {
    this.lastModifier = user;
  }

  @Override
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

}
