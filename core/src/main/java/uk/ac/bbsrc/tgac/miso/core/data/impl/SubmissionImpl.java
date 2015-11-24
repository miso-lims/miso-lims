/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Concrete implementation of a Submission that
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class SubmissionImpl implements Submission<Submittable, Document, Document>, Serializable {
  protected static final Logger log = LoggerFactory.getLogger(SubmissionImpl.class);

  public static final Long UNSAVED_ID = 0L;

  private long submissionId = UNSAVED_ID;
  private String name;
  private String alias;
  private String accession;
  private Date creationDate;
  private Date submissionDate;
  private boolean verified;
  private boolean completed;
  private String description;
  private String title;
  private SubmissionActionType submissionActionType;
  private Date lastUpdated;

  private Set<Submittable<Document>> submittables = new HashSet<Submittable<Document>>();
  public Document submissionDocument;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile = null;

  /**
   * Construct a new Submission with a default empty SecurityProfile
   */
  public SubmissionImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Submission with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public SubmissionImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  @Deprecated
  public Long getSubmissionId() {
    return submissionId;
  }

  @Override
  @Deprecated
  public void setSubmissionId(Long submissionId) {
    this.submissionId = submissionId;
  }

  @Override
  public long getId() {
    return submissionId;
  }

  @Override
  public void setId(long id) {
    this.submissionId = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
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
  public Date getSubmissionDate() {
    return submissionDate;
  }

  @Override
  public void setSubmissionDate(Date submissionDate) {
    this.submissionDate = submissionDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean isVerified() {
    return verified;
  }

  @Override
  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public void addSubmissionElement(Submittable s) {
    if (!submittables.contains(s)) {
      submittables.add(s);
    } else {
      log.debug(s.getClass().getSimpleName() + " already exists in the Submission payload. Not adding.");
    }
  }

  @Override
  public Set<Submittable<Document>> getSubmissionElements() {
    return submittables;
  }

  public void setSubmissionElements(Set<Submittable<Document>> submittables) {
    this.submittables = submittables;
  }

  @Override
  public SubmissionActionType getSubmissionActionType() {
    return submissionActionType;
  }

  @Override
  public void setSubmissionActionType(SubmissionActionType submissionActionType) {
    this.submissionActionType = submissionActionType;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    } else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  @Override
  public void buildSubmission() {
  }

  @Override
  public Document submit(SubmissionManager manager) throws SubmissionException {
    submittables.add(this);
    return (Document) manager.submit(submittables);
  }

  @Override
  public int compareTo(Object o) {
    Submission t = (Submission) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }
}
