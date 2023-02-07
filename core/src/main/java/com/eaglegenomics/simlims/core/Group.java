package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
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
public class Group implements Serializable, Comparable<Group>, Deletable, Identifiable {

  private static final long serialVersionUID = 1L;
  private static final long UNSAVED_ID = 0L;

  public static final String RUN_REVIEWERS = "Run Reviewers";

  private String description = "";
  private String name = "";
  private boolean builtIn;

  @ManyToMany(targetEntity = UserImpl.class)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "User_Group", joinColumns = { @JoinColumn(name = "groups_groupId") }, inverseJoinColumns = {
      @JoinColumn(name = "users_userId")
  })
  private Set<User> users = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long groupId = Group.UNSAVED_ID;

  @Override
  public long getId() {
    return groupId;
  }

  @Override
  public void setId(long groupId) {
    this.groupId = groupId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isBuiltIn() {
    return builtIn;
  }

  public void setBuiltIn(boolean builtIn) {
    this.builtIn = builtIn;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
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
  public int compareTo(Group t) {
    return this.equals(t) ? 0 : 1;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "User Group";
  }

  @Override
  public String getDeleteDescription() {
    return getName();
  }
}
