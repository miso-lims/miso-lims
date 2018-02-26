package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

@Entity
public class Deletion implements SecurableByProfile {

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

  @ManyToOne
  @JoinColumn(name = "securityProfileId")
  private SecurityProfile securityProfile = null;

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

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile profile) {
    this.securityProfile = profile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    this.securityProfile = parent.getSecurityProfile();
  }

}
