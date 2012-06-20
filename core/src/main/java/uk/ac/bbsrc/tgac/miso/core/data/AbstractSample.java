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

import com.eaglegenomics.simlims.core.Note;
import net.sourceforge.fluxion.spi.ServiceProvider;
import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.visitor.SubmittableVisitor;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Skeleton implementation of a Sample
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Sample`")
public abstract class AbstractSample implements Sample {
  public static final Long UNSAVED_ID = null;
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long sampleId = AbstractSample.UNSAVED_ID;

  private Project project;

  @ManyToMany(targetEntity = AbstractExperiment.class, mappedBy = "samples")
  private Collection<Experiment> experiments = new HashSet<Experiment>();

  private Collection<Library> libraries = new HashSet<Library>();

  private Collection<SampleQC> sampleQCs = new HashSet<SampleQC>();

  private Collection<Note> notes = new HashSet<Note>();

  @Transient
  public Document submissionDocument;

  @OneToOne(cascade = CascadeType.ALL)
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
  private Date lastUpdated;

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  public String getTaxonIdentifier() {
    return taxonIdentifier;
  }

  public void setTaxonIdentifier(String taxonIdentifier) {
    this.taxonIdentifier = taxonIdentifier;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public String getLabelText() {
    return getAlias();
  }

  public void addLibrary(Library l) throws MalformedLibraryException {
    this.libraries.add(l);
  }

  public Collection<Library> getLibraries() {
    return libraries;
  }  

  public void addQc(SampleQC sampleQc) throws MalformedSampleQcException {
    this.sampleQCs.add(sampleQc);
    try {
      sampleQc.setSample(this);
    }
    catch (MalformedSampleException e) {
      e.printStackTrace();
    }
  }

  public Collection<SampleQC> getSampleQCs() {
    return sampleQCs;
  }

  public void setQCs(Collection<SampleQC> qcs) {
    this.sampleQCs = qcs;
  }

  public String getSampleType() {
    return sampleType;
  }

  public Date getReceivedDate() {
    return receivedDate;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  public void setReceivedDate(Date receivedDate) {
    this.receivedDate = receivedDate;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  /*
  public Document getSubmissionData() {
    return submissionDocument;
  }

  public void accept(SubmittableVisitor v) {
    v.visit(this);
  }    
*/

  public Collection<Note> getNotes() {
    return notes;
  }

  public void addNote(Note note) {
    this.notes.add(note);
  }   

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }  

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public boolean isDeletable() {
    return getSampleId() != AbstractSample.UNSAVED_ID &&
           getLibraries().isEmpty() &&
           getNotes().isEmpty() &&
           getSampleQCs().isEmpty();    
  }

  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    setSecurityProfile(parent.getSecurityProfile());
  }  

  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  public abstract void buildSubmission();


  public abstract void buildReport();
  

  /**
   * Equivalency is based on getSampleId() if set, otherwise on name, otherwise on alias
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Sample))
      return false;
    Sample them = (Sample) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getSampleId().equals(AbstractSample.UNSAVED_ID) || them.getSampleId().equals(AbstractSample.UNSAVED_ID)) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      }
      else {
        return getAlias().equals(them.getAlias());
      }
    }
    else {
      return getSampleId().longValue() == them.getSampleId().longValue();
    }
  }

  @Override
  public int hashCode() {
    if (getSampleId() != null && !getSampleId().equals(AbstractSample.UNSAVED_ID)) {
      return getSampleId().intValue();
    }
    else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
//      if (getDescription() != null) hashcode = 37 * hashcode + getDescription().hashCode();
//      if (getLibraries() != null && !getLibraries().isEmpty()) hashcode = 37 * hashcode + getLibraries().hashCode();
//      if (getSampleQCs() != null && !getSampleQCs().isEmpty()) hashcode = 37 * hashcode + getSampleQCs().hashCode();
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }  

  @Override
  public int compareTo(Object o) {
    Sample s = (Sample)o;
    if (getSampleId() != null && s.getSampleId() != null) {
      if (getSampleId() < s.getSampleId()) return -1;
      if (getSampleId() > s.getSampleId()) return 1;
    }
    else if (getAlias() != null && s.getAlias() != null) {
      return getAlias().compareTo(s.getAlias());
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getSampleId());
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
}
