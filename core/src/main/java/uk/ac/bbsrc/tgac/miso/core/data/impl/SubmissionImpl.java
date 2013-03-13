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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.*;

/**
 * Concrete implementation of a Submission that
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SubmissionImpl implements Submission<Submittable, Document, Document>, Serializable {
  protected static final Logger log = LoggerFactory.getLogger(SubmissionImpl.class);

  public static final Long UNSAVED_ID = 0L;

  private Long submissionId = UNSAVED_ID;
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
   * @param user of type User
   */
  public SubmissionImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Deprecated
  public Long getSubmissionId() {
    return submissionId;
  }

  @Deprecated
  public void setSubmissionId(Long submissionId) {
    this.submissionId = submissionId;
  }

  @Override
  public long getId() {
    return submissionId;
  }

  public void setId(long id) {
    this.submissionId = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getSubmissionDate() {
    return submissionDate;
  }

  public void setSubmissionDate(Date submissionDate) {
    this.submissionDate = submissionDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public void addSubmissionElement(Submittable s) {
    if (!submittables.contains(s)) {
        submittables.add(s);
    }
    else {
      log.debug(s.getClass().getSimpleName() + " already exists in the Submission payload. Not adding.");
    }
  }

  public Set<Submittable<Document>> getSubmissionElements() {
    return submittables;
  }

  public void setSubmissionElements(Set<Submittable<Document>> submittables) {
    this.submittables = submittables;
  }

  public SubmissionActionType getSubmissionActionType() {
    return submissionActionType;
  }

  public void setSubmissionActionType(SubmissionActionType submissionActionType) {
    this.submissionActionType = submissionActionType;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

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
    }
    else {
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
      //return this.userCanWrite(user);

  }

  @Override
  public void buildSubmission() {
  }

  @Override
  public Document submit(SubmissionManager manager) throws SubmissionException {
    submittables.add(this);
    return (Document)manager.submit(submittables);
  }
}
