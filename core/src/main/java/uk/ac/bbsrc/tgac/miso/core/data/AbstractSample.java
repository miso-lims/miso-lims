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

package uk.ac.bbsrc.tgac.miso.core.data;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonManagedReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleDerivedInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Skeleton implementation of a Sample
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@MappedSuperclass
public abstract class AbstractSample extends AbstractBoxable implements Sample {

  protected static final Logger log = LoggerFactory.getLogger(AbstractSample.class);
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long sampleId = AbstractSample.UNSAVED_ID;

  @ManyToOne(targetEntity = ProjectImpl.class)
  private Project project;

  @Transient
  private final Collection<Experiment> experiments = new HashSet<Experiment>();

  @Transient
  @JsonManagedReference
  private final Collection<Library> libraries = new HashSet<Library>();

  @Transient
  private Collection<SampleQC> sampleQCs = new TreeSet<SampleQC>();

  @Transient
  private Collection<Note> notes = new HashSet<Note>();

  @Transient
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Transient
  public Document submissionDocument;

  @Transient
  private SecurityProfile securityProfile = null;

  private String accession;
  private String name;
  private String description;
  private String scientificName;
  private String taxonIdentifier;
  private String sampleType;
  private Date receivedDate;
  private Boolean qcPassed;
  private String identificationBarcode;
  private String locationBarcode;
  private String alias;
  private Long securityProfile_profileId;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @OneToOne(targetEntity = SampleDerivedInfo.class)
  @PrimaryKeyJoinColumn
  private SampleDerivedInfo derivedInfo;

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public long getId() {
    return sampleId;
  }

  @Override
  public void setId(long id) {
    this.sampleId = id;
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
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
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
  public String getScientificName() {
    return scientificName;
  }

  @Override
  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  @Override
  public String getTaxonIdentifier() {
    return taxonIdentifier;
  }

  @Override
  public void setTaxonIdentifier(String taxonIdentifier) {
    this.taxonIdentifier = nullifyStringIfBlank(taxonIdentifier);
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
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = nullifyStringIfBlank(identificationBarcode);
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = nullifyStringIfBlank(locationBarcode);
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public void addLibrary(Library l) throws MalformedLibraryException {
    this.libraries.add(l);
  }

  @Override
  public Collection<Library> getLibraries() {
    return libraries;
  }

  @Override
  public void addQc(SampleQC sampleQc) throws MalformedSampleQcException {
    this.sampleQCs.add(sampleQc);
    try {
      sampleQc.setSample(this);
    } catch (MalformedSampleException e) {
      log.error("add QC", e);
    }
  }

  @Override
  public Collection<SampleQC> getSampleQCs() {
    return sampleQCs;
  }

  @Override
  public void setQCs(Collection<SampleQC> qcs) {
    this.sampleQCs = qcs;
  }

  @Override
  public String getSampleType() {
    return sampleType;
  }

  @Override
  public Date getReceivedDate() {
    return receivedDate;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public void setSampleType(String sampleType) {
    this.sampleType = nullifyStringIfBlank(sampleType);
  }

  @Override
  public void setReceivedDate(Date receivedDate) {
    this.receivedDate = receivedDate;
  }

  @Override
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  /*
   * public Document getSubmissionData() { return submissionDocument; }
   * 
   * public void accept(SubmittableVisitor v) { v.visit(this); }
   */

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractSample.UNSAVED_ID && getLibraries().isEmpty() && getNotes().isEmpty() && getSampleQCs().isEmpty();
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
    if (securityProfile != null) {
      this.securityProfile_profileId = securityProfile.getProfileId();
    }
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
  public abstract void buildSubmission();

  @Override
  public abstract void buildReport();

  @Override
  public Date getLastModified() {
    return (derivedInfo == null ? null : derivedInfo.getLastModified());
  }

  /**
   * Equivalency is based on getSampleId() if set, otherwise on name, otherwise on alias
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Sample)) return false;
    Sample them = (Sample) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractSample.UNSAVED_ID || them.getId() == AbstractSample.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      } else if (getAlias() != null && them.getAlias() != null) {
        return getAlias().equals(them.getAlias());
      } else {
        return false;
      }
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != 0L && getId() != AbstractSample.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Sample s = (Sample) o;
    if (getId() != 0L && s.getId() != 0L) {
      if (getId() < s.getId()) return -1;
      if (getId() > s.getId()) return 1;
    } else if (getAlias() != null && s.getAlias() != null) {
      return getAlias().compareTo(s.getAlias());
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getScientificName());
    sb.append(" : ");
    sb.append(getSampleType());

    return sb.toString();
  }

  @Override
  public Long getSecurityProfileId() {
    return securityProfile_profileId;
  }

  @Override
  public void setSecurityProfileId(Long securityProfileId) {
    securityProfile_profileId = securityProfileId;
  }

}
