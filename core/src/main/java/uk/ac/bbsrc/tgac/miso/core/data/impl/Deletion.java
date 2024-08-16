package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Deletion {

  @Id
  @Column(name = "deletionId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String targetType;

  private long targetId;

  private String description;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "userId")
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  private Date changeTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTargetType() {
    return targetType;
  }

  public void setTargetType(String targetType) {
    this.targetType = targetType;
  }

  public long getTargetId() {
    return targetId;
  }

  public void setTargetId(long targetId) {
    this.targetId = targetId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(Date changeTime) {
    this.changeTime = changeTime;
  }

}
