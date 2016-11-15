package com.eaglegenomics.simlims.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2009 The Genome Analysis Center, Norwich, UK.
 * <p>
 * An instantiation of a protocol within a project.
 * 
 * @author Richard Holland
 * @since 0.0.1
 */
@Entity
public class Request implements Securable, Serializable {
	protected static final Logger log = LoggerFactory.getLogger(Request.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Use this ID to indicate that a request has not yet been saved, and
	 * therefore does not yet have a unique ID.
	 */
	public static final Long UNSAVED_ID = null;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long requestId = Request.UNSAVED_ID;
	private String name = "";
	private String description = "";
	private Date creationDate = new Date();
	private Date lastExecutionDate;
	private int executionCount = 0;
	private String protocolUniqueIdentifier;
	@ManyToOne(cascade = CascadeType.ALL)
	private Project project = null;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "request")
	private Collection<Note> notes = new HashSet<Note>();
	@OneToOne(cascade = CascadeType.ALL)
	private SecurityProfile securityProfile = null;

    public Request() {

    }

	public Request(Project project, User owner) {
		setProject(project);
		setSecurityProfile(new SecurityProfile(owner));
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Defaults to 0 until the first execution takes place.
	 */
	public int getExecutionCount() {
		return executionCount;
	}

	public Date getLastExecutionDate() {
		return lastExecutionDate;
	}

	public String getName() {
		return name;
	}

	public Collection<Note> getNotes() {
		return notes;
	}

	public Project getProject() {
		return project;
	}

	public String getProtocolUniqueIdentifier() {
		return protocolUniqueIdentifier;
	}

	/**
	 * Internal use only.
	 */
	public Long getRequestId() {
		return requestId;
	}

	/**
	 * Only those that can write to this request can create notes.
	 */
	public Note createNote(User owner) throws SecurityException {
		if (!userCanWrite(owner)) {
			throw new SecurityException();
		}
		Note note = new Note(this, owner);
		getNotes().add(note);
		return note;
	}

	/**
	 * Executing a request means taking the initial input data and setting up
	 * the input to the first activity in the protocol the request refers to.
	 * Only users that can write to the protocol can use this method.
	 * 
	 * @param protocolManager
	 *            used for identifying the start point to feed input into.
	 */
	public void execute(User user, Collection<ActivityData> inputData,
			ProtocolManager protocolManager) throws SecurityException,
			IOException {
		if (log.isInfoEnabled()) {
			log.info("Executing request " + name + " with execution count "
					+ executionCount);
		}
		Protocol protocol = protocolManager
				.getProtocol(getProtocolUniqueIdentifier());
		if (!protocol.userCanWrite(user)) {
			throw new SecurityException();
		}
		setLastExecutionDate(new Date());
		setExecutionCount(getExecutionCount() + 1);
		protocolManager.setupInputData(user, inputData);
		if (log.isInfoEnabled()) {
			log.info("Done executing request " + name
					+ " with execution count " + executionCount);
		}
	}

	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Must be at least 1. Only exception is that at first instantiation, the
	 * default value is 0 until the first execution.
	 */
	public void setExecutionCount(int executionCount) {
		if (executionCount <= 0) {
			throw new IllegalArgumentException("ExecutionCount must be >= 1");
		}
		this.executionCount = executionCount;
	}

	/**
	 * Can be null until executed for the first time.
	 */
	public void setLastExecutionDate(Date date) {
		this.lastExecutionDate = date;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNotes(Collection<Note> notes) {
		this.notes = notes;
	}

	public void setProject(Project project) {
		this.project = project;
		if (!project.getRequests().contains(this)) {
			project.getRequests().add(this);
		}
	}

	public void setProtocolUniqueIdentifier(String protocolUniqueIdentifier) {
		this.protocolUniqueIdentifier = protocolUniqueIdentifier;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public SecurityProfile getSecurityProfile() {
		return securityProfile;
	}

	public void setSecurityProfile(SecurityProfile profile) {
		this.securityProfile = profile;
	}

	/**
	 * Delegates to the security profile, but also users who can read the
	 * project or write this request can read this request.
	 */
	public boolean userCanRead(User user) {
		return securityProfile.userCanRead(user)
				|| getProject().userCanRead(user) || userCanWrite(user);
	}

	/**
	 * Delegates to the security profile.
	 */
	public boolean userCanWrite(User user) {
		return securityProfile.userCanWrite(user);
	}

	/**
	 * Equality based on getRequestId() if set, otherwise a combination of name,
	 * description, creation date, project, and protocol unique identifier.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Request))
			return false;
		Request them = (Request) obj;
		// If not saved, then compare resolved actual objects. Otherwise
		// just compare IDs.
		if (getRequestId() == Request.UNSAVED_ID
				|| them.getRequestId() == Request.UNSAVED_ID) {
			return this.getName().equals(them.getName())
					&& this.getDescription().equals(them.getDescription())
					&& this.getCreationDate().equals(them.getCreationDate())
					&& this.getProject().equals(them.getProject())
					&& this.getProtocolUniqueIdentifier().equals(
							them.getProtocolUniqueIdentifier());
		} else {
			return this.getRequestId() == them.getRequestId();
		}
	}

	@Override
	public int hashCode() {
		if (this.getRequestId() != Request.UNSAVED_ID) {
			return this.getRequestId().intValue();
		} else {
			int hashcode = this.getName().hashCode();
			hashcode = 37 * hashcode + this.getDescription().hashCode();
			hashcode = 37 * hashcode + this.getCreationDate().hashCode();
			hashcode = 37 * hashcode + this.getProject().hashCode();
			hashcode = 37 * hashcode
					+ this.getProtocolUniqueIdentifier().hashCode();
			return hashcode;
		}
	}

	/**
	 * Format is
	 * "Project -> (ProtocolUniqueId) CreationDate : Name : Description"
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getProject().toString());
		sb.append(" -> (");
		sb.append(getProtocolUniqueIdentifier());
		sb.append(") ");
		sb.append(getCreationDate());
		sb.append(" : ");
		sb.append(getName());
		sb.append(" : ");
		sb.append(getDescription());
		return sb.toString();
	}

}
