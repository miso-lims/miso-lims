package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Abstract implementation of notes that provides all basic functionality.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
@Entity
@Table(name = "Note")
public class Note implements Serializable, Comparable<Note>, Identifiable {

  private static final long serialVersionUID = 1L;

  /**
   * Use this ID to indicate that a note has not yet been saved, and therefore
   * does not yet have a unique ID.
   */
  public static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long noteId = Note.UNSAVED_ID;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date creationDate = new Date();

  @Column(nullable = false)
  private String text = "";

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "owner_userId", nullable = false)
  private User owner = null;

  @Column(nullable = false)
  private boolean internalOnly = false;

  public Note() {
  }

  public Note(User user) {
    setOwner(user);
  }

  @Override
  public long getId() {
    return noteId;
  }

  @Override
  public void setId(long noteId) {
    this.noteId = noteId;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public String getText() {
    return text;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setText(String text) {
    this.text = text;
  }

  public User getOwner() {
    return owner;
  }

  public boolean isInternalOnly() {
    return internalOnly;
  }

  public void setInternalOnly(boolean internalOnly) {
    this.internalOnly = internalOnly;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  /**
   * Notes are equivalent based on creation date, owner and text.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Note))
      return false;
    Note them = (Note) obj;
    return this.getCreationDate().equals(them.getCreationDate())
        && this.getOwner().equals(them.getOwner())
        && this.getText().equals(them.getText());
  }

  @Override
  public int hashCode() {
    if (isSaved()) {
      return Long.valueOf(getId()).hashCode();
    }
    else {
      int hashcode = 1;
      if (getCreationDate() != null) hashcode = 37 * hashcode + getCreationDate().hashCode();
      if (getOwner() != null) hashcode = 37 * hashcode + getOwner().hashCode();
      if (getText() != null) hashcode = 37 * hashcode + getText().hashCode();
      return hashcode;
    }
  }

  /**
   * Format is "Date: Owner: Text".
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getCreationDate());
    sb.append(" : ");
    sb.append(getOwner());
    sb.append(" : ");
    sb.append(getText());
    return sb.toString();
  }

  @Override
  public int compareTo(Note o) {
    if (isSaved() && o.isSaved()) {
      if (getId() < o.getId()) return -1;
      if (getId() > o.getId()) return 1;
    }
    else if (getText() != null && o.getText() != null) {
      return getText().compareTo(o.getText());
    }
    return 0;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }
}
