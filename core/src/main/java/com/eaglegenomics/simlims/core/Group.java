package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p/>
 * A logical group of users who want to share stuff or do similar things.
 *
 * @author Richard Holland
 * @since 0.0.1
 */
@Entity
@Table(name = "_Group")
public class Group implements Serializable, Comparable {

  private static final long serialVersionUID = 1L;

  /**
   * The ID for unsaved groups.
   */
  public static final Long UNSAVED_ID = null;

  private String description = "";
  private String name = "";
  @ManyToMany(targetEntity = UserImpl.class)
  @JoinTable(name = "User_Group", joinColumns = { @JoinColumn(name = "users_userId") }, inverseJoinColumns = {
      @JoinColumn(name = "groups_groupId")
  })
  private Collection<User> users = new HashSet<>();
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long groupId = Group.UNSAVED_ID;

  /**
   * Internal use only.
   */
  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public Collection<User> getUsers() {
    return users;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUsers(Collection<User> users) {
    this.users = users;
  }

  /**
   * Groups match on name.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Group))
      return false;
    Group them = (Group) obj;
    return this.getName().equals(them.getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  /**
   * Same as getName().
   */
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int compareTo(Object o) {
    return this.equals(o) ? 0 : 1;
  }
}
