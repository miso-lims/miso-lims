package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.*;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * Implementation of a security profile specifying which users and groups can
 * read or write an object, and which user owns it. Has no meaning on its own,
 * and is usually associated with the object it describes as a field in that
 * object.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
@Entity
@Table(name = "`SecurityProfile`")
public class SecurityProfile implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * A constant referring to an unsaved profile.
   */
  public static final Long UNSAVED_ID = null;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long profileId = SecurityProfile.UNSAVED_ID;
  @ManyToOne
  private User owner = null;
  @ManyToMany
  private Collection<User> readUsers = new HashSet<User>();
  @ManyToMany
  private Collection<User> writeUsers = new HashSet<User>();
  @ManyToMany
  private Collection<Group> readGroups = new HashSet<Group>();
  @ManyToMany
  private Collection<Group> writeGroups = new HashSet<Group>();
  private boolean allowAllInternal = true;

  public SecurityProfile() {

  }

  public SecurityProfile(User owner) {
    setOwner(owner);
  }

  /**
   * Internal use only. Value is of no interest to the user.
   */
  public Long getProfileId() {
    return this.profileId;
  }

  public void setProfileId(Long profileId) {
    this.profileId = profileId;
  }

  public User getOwner() {
    return this.owner;
  }

  public Collection<Group> getReadGroups() {
    return this.readGroups;
  }

  public Collection<User> getReadUsers() {
    return this.readUsers;
  }

  public Collection<Group> getWriteGroups() {
    return this.writeGroups;
  }

  public Collection<User> getWriteUsers() {
    return this.writeUsers;
  }

  public boolean isAllowAllInternal() {
    return this.allowAllInternal;
  }

  public void setAllowAllInternal(boolean allowAllInternal) {
    this.allowAllInternal = allowAllInternal;
  }

  public void setOwner(User user) {
    this.owner = user;
  }

  public void setReadGroups(Collection<Group> groups) {
    this.readGroups = groups;
  }

  public void setReadUsers(Collection<User> users) {
    this.readUsers = users;
  }

  public void setWriteGroups(Collection<Group> groups) {
    this.writeGroups = groups;
  }

  public void setWriteUsers(Collection<User> users) {
    this.writeUsers = users;
  }

  /**
   * The rules of readability are: user is inactive, then no. User is admin,
   * then yes. User is internal and allowAllInternal set, then yes. User is
   * owner, then yes. User is in getReadUsers, or is a member of a group in
   * getReadGroups, then yes. User can write, then yes.
   */
  public boolean userCanRead(User user) {
    if (!user.isActive()) {
      return false;
    }
    if (user.isAdmin()) {
      return true;
    }
    if (isAllowAllInternal() && user.isInternal()) {
      return true;
    }
    if (getOwner() != null && getOwner().equals(user)) {
      return true;
    }
    return getReadUsers().contains(user) ||
           intersects(getReadGroups(), user.getGroups()) ||
           userCanWrite(user);
  }

  /**
   * The rules of writeability are: user is inactive, then no. User is admin,
   * then yes. User is internal and allowAllInternal set, then yes. User is
   * owner, then yes. User is in getWriteUsers, or is a member of a group in
   * getWriteGroups, then yes.
   */
  public boolean userCanWrite(User user) {
    if (!user.isActive()) {
      return false;
    }
    if (user.isAdmin()) {
      return true;
    }
    if (isAllowAllInternal() && user.isInternal()) {
      return true;
    }
    if (getOwner() != null && getOwner().equals(user)) {
      return true;
    }
    return getWriteUsers().contains(user) ||
           intersects(getWriteGroups(), user.getGroups());
  }

  /**
   * A utility class to see if any member of group a appears in group b.
   */
  private <T> boolean intersects(Collection<T> a, Collection<T> b) {
    for (T entry : a) {
      if (b.contains(entry)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Profiles are equal if owner and all read/write groups and users match, or
   * if the saved getProfileId() values match.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof SecurityProfile))
      return false;
    SecurityProfile them = (SecurityProfile) obj;
    if (getProfileId() == SecurityProfile.UNSAVED_ID
        || them.getProfileId() == SecurityProfile.UNSAVED_ID) {
      return this.getOwner().equals(them.getOwner())
             && this.getReadGroups().equals(them.getReadGroups())
             && this.getWriteGroups().equals(them.getWriteGroups())
             && this.getReadUsers().equals(them.getReadUsers())
             && this.getWriteUsers().equals(them.getWriteUsers());
    }
    else {
      return this.getProfileId() == them.getProfileId();
    }
  }

  @Override
  public int hashCode() {
    if (this.getProfileId() != SecurityProfile.UNSAVED_ID) {
      return this.getProfileId().intValue();
    }
    else {
      System.out.println(this.toString());
      int hashcode = 1;
      if (this.getOwner() != null) hashcode = 37 * hashcode + this.getOwner().hashCode();
      if (this.getReadGroups() != null) hashcode = 37 * hashcode + this.getReadGroups().hashCode();
      if (this.getWriteGroups() != null) hashcode = 37 * hashcode + this.getWriteGroups().hashCode();
      if (this.getReadUsers() != null) hashcode = 37 * hashcode + this.getReadUsers().hashCode();
      if (this.getWriteUsers() != null) hashcode = 37 * hashcode + this.getWriteUsers().hashCode();
      return hashcode;
    }
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(this.getProfileId());
    b.append(":");
    b.append(this.isAllowAllInternal());
    b.append(":");
    if (this.getOwner() != null) {
      b.append(this.getOwner().getUserId());
      b.append(":");
    }
    if (this.getReadUsers() != null) {
      for (User ru : this.getReadUsers()) {
        b.append("|");
        b.append(ru.getUserId());
        b.append("|");
      }
      b.append(":");
    }

    if (this.getWriteUsers() != null) {
      for (User wu : this.getWriteUsers()) {
        b.append("|");
        b.append(wu.getUserId());
        b.append("|");
      }
      b.append(":");
    }

    if (this.getReadGroups() != null) {
      for (Group rg : this.getReadGroups()) {
        b.append("|");
        b.append(rg.getGroupId());
        b.append("|");
      }
      b.append(":");
    }

    if (this.getWriteGroups() != null) {
      for (Group wg : this.getWriteGroups()) {
        b.append("|");
        b.append(wg.getGroupId());
        b.append("|");
      }
    }

    return b.toString();
  }
}
