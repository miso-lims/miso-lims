package com.eaglegenomics.simlims.core;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * Abstract implementation of notes that provides all basic functionality.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
@Entity
public class Note implements Securable, Serializable, Comparable {

	private static final long serialVersionUID = 1L;

	/**
	 * Use this ID to indicate that a note has not yet been saved, and therefore
	 * does not yet have a unique ID.
	 */
	public static final Long UNSAVED_ID = null;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long noteId = Note.UNSAVED_ID;
	private Date creationDate = new Date();
	private String text = "";
	@ManyToOne(cascade = CascadeType.ALL)
	private Request request = null;
	@ManyToOne
	private User owner = null;
	private boolean internalOnly = false;

    public Note() {
    }

	public Note(Request request, User user) {
		setRequest(request);
		setOwner(user);
	}

	/**
	 * Internal use only.
	 */
	public Long getNoteId() {
		return noteId;
	}

	public void setNoteId(Long noteId) {
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

	public Request getRequest() {
		return request;
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

	public void setRequest(Request request) {
		this.request = request;
		if (!request.getNotes().contains(this)) {
			request.getNotes().add(this);
		}
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Users can read if they are active and are the owner, or can read the
	 * request and either the user is internal or the note is not internal only.
	 */
	public boolean userCanRead(User user) {
		if (!user.isActive()) {
			return false;
		}
		return user.equals(this.getOwner())
				|| (request.userCanRead(user) && (!isInternalOnly() || user
						.isInternal()));
	}

	/**
	 * Users can write if they can write to the request.
	 */
	public boolean userCanWrite(User user) {
		return request.userCanWrite(user);
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
      if (getNoteId() != null && !getNoteId().equals(Note.UNSAVED_ID)) {
        return getNoteId().hashCode();
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

  public int compareTo(Object o) {
    Note s = (Note)o;
    if (getNoteId() != null && s.getNoteId() != null) {
      if (getNoteId() < s.getNoteId()) return -1;
      if (getNoteId() > s.getNoteId()) return 1;
    }
    else if (getText() != null && s.getText() != null) {
      return getText().compareTo(s.getText());
    }
    return 0;
  }
}
