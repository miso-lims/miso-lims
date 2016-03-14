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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAnalyteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
  private final Collection<Library> libraries = new HashSet<Library>();

  @Transient
  private Collection<SampleQC> sampleQCs = new TreeSet<SampleQC>();

  @Transient
  private Collection<Note> notes = new HashSet<Note>();

  @Transient
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Transient
  public Document submissionDocument;

  @OneToOne(targetEntity = SecurityProfile.class)
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

  @Transient
  private Date lastUpdated;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @OneToOne(targetEntity = SampleAnalyteImpl.class)
  @JoinColumn(name = "sampleAnalyteId")
  @Cascade({ CascadeType.SAVE_UPDATE })
  private SampleAnalyte sampleAnalyte;

  @OneToOne(targetEntity = SampleTissueImpl.class)
  @JoinColumn(name = "sampleTissueId")
  @Cascade({ CascadeType.SAVE_UPDATE })
  private SampleTissue sampleTissue;

  @OneToOne(targetEntity = IdentityImpl.class)
  @JoinColumn(name = "identityId")
  @Cascade({ CascadeType.SAVE_UPDATE })
  private Identity identity;

  @OneToOne(targetEntity = SampleAdditionalInfoImpl.class)
  @JoinColumn(name = "sampleAdditionalInfoId")
  @Cascade({ CascadeType.SAVE_UPDATE })
  private SampleAdditionalInfo sampleAdditionalInfo;

  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "parentId")
  @Cascade({ CascadeType.SAVE_UPDATE })
  private Sample parent;

  @OneToMany(targetEntity = SampleImpl.class, fetch = FetchType.LAZY)
  @JoinTable(name = "SampleHierarchy", joinColumns = @JoinColumn(name = "parentId") , inverseJoinColumns = @JoinColumn(name = "childId") )
  private Set<Sample> children = new HashSet<Sample>();

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
  @Deprecated
  public Long getSampleId() {
    return sampleId;
  }

  @Override
  @Deprecated
  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
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
    this.taxonIdentifier = taxonIdentifier;
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
    this.identificationBarcode = identificationBarcode;
  }

  @Override
  public String getLocationBarcode() {
    return locationBarcode;
  }

  @Override
  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
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
    this.sampleType = sampleType;
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
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
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
  public SampleAdditionalInfo getSampleAdditionalInfo() {
    return sampleAdditionalInfo;
  }

  @Override
  public void setSampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo) {
    this.sampleAdditionalInfo = sampleAdditionalInfo;
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
  public SampleAnalyte getSampleAnalyte() {
    return sampleAnalyte;
  }

  @Override
  public void setSampleAnalyte(SampleAnalyte sampleAnalyte) {
    this.sampleAnalyte = sampleAnalyte;
  }

  @Override
  public Identity getIdentity() {
    return identity;
  }

  @Override
  public void setIdentity(Identity identity) {
    this.identity = identity;
  }

  @Override
  public Sample getParent() {
    return parent;
  }

  @Override
  public void setParent(Sample parent) {
    this.parent = parent;
  }

  @Override
  public Set<Sample> getChildren() {
    return children;
  }

  @Override
  public void setChildren(Set<Sample> children) {
    this.children = children;
  }

  @Override
  public SampleTissue getSampleTissue() {
    return sampleTissue;
  }

  @Override
  public void setSampleTissue(SampleTissue sampleTissue) {
    this.sampleTissue = sampleTissue;
  }

  public static class SampleFactoryBuilder {
    private String description;
    private String sampleType;
    private Project project;
    private String scientificName;

    /** User is needed to create a SecurityProfile. */
    private User user;

    private SampleAdditionalInfo sampleAdditionalInfo;
    private Identity identity;
    private SampleAnalyte sampleAnalyte;
    private SampleTissue sampleTissue;
    private String accession;
    private String name;
    private String identificationBarcode;
    private String locationBarcode;
    private Date receivedDate;
    private Boolean qcPassed;
    private String alias;
    private String taxonIdentifier;
    private SampleClass rootSampleClass;
    private Sample parent;

    public SampleFactoryBuilder parent(Sample parent) {
      this.parent = parent;
      return this;
    }

    public SampleFactoryBuilder rootSampleClass(SampleClass rootSampleClass) {
      this.rootSampleClass = rootSampleClass;
      return this;
    }

    public SampleFactoryBuilder identity(Identity identity) {
      this.identity = identity;
      return this;
    }

    public SampleFactoryBuilder sampleAnalyte(SampleAnalyte sampleAnalyte) {
      this.sampleAnalyte = sampleAnalyte;
      return this;
    }

    public SampleFactoryBuilder accession(String accession) {
      this.accession = accession;
      return this;
    }

    public SampleFactoryBuilder name(String name) {
      this.name = name;
      return this;
    }

    public SampleFactoryBuilder identificationBarcode(String identificationBarcode) {
      this.identificationBarcode = identificationBarcode;
      return this;
    }

    public SampleFactoryBuilder locationBarcode(String locationBarcode) {
      this.locationBarcode = locationBarcode;
      return this;
    }

    public SampleFactoryBuilder receivedDate(Date receivedDate) {
      this.receivedDate = receivedDate;
      return this;
    }

    public SampleFactoryBuilder qcPassed(Boolean qcPassed) {
      this.qcPassed = qcPassed;
      return this;
    }

    public SampleFactoryBuilder alias(String alias) {
      this.alias = alias;
      return this;
    }

    public SampleFactoryBuilder taxonIdentifier(String taxonIdentifier) {
      this.taxonIdentifier = taxonIdentifier;
      return this;
    }

    public Identity getIdentity() {
      return identity;
    }

    public String getDescription() {
      return description;
    }

    public String getSampleType() {
      return sampleType;
    }

    public Project getProject() {
      return project;
    }

    public String getScientificName() {
      return scientificName;
    }

    public User getUser() {
      return user;
    }

    public Sample getParent() {
      return parent;
    }

    public SampleAdditionalInfo getSampleAdditionalInfo() {
      return sampleAdditionalInfo;
    }

    public SampleFactoryBuilder description(String description) {
      this.description = description;
      return this;
    }

    public SampleFactoryBuilder sampleType(String sampleType) {
      this.sampleType = sampleType;
      return this;
    }

    public SampleFactoryBuilder project(Project project) {
      this.project = project;
      return this;
    }

    public SampleFactoryBuilder scientificName(String scientificName) {
      this.scientificName = scientificName;
      return this;
    }

    public SampleFactoryBuilder user(User user) {
      this.user = user;
      return this;
    }

    public SampleFactoryBuilder sampleAdditionalInfo(SampleAdditionalInfo sampleAdditionalInfo) {
      this.sampleAdditionalInfo = sampleAdditionalInfo;
      return this;
    }

    public SampleFactoryBuilder sampleTissue(SampleTissue sampleTissue) {
      this.sampleTissue = sampleTissue;
      return this;
    }

    public SampleAnalyte getSampleAnalyte() {
      return sampleAnalyte;
    }

    public String getAccession() {
      return accession;
    }

    public String getName() {
      return name;
    }

    public String getIdentificationBarcode() {
      return identificationBarcode;
    }

    public String getLocationBarcode() {
      return locationBarcode;
    }

    public Date getReceivedDate() {
      return receivedDate;
    }

    public Boolean getQcPassed() {
      return qcPassed;
    }

    public String getAlias() {
      return alias;
    }

    public String getTaxonIdentifier() {
      return taxonIdentifier;
    }

    public SampleTissue getSampleTissue() {
      return this.sampleTissue;
    }

    public Sample build() {
      checkArgument(user != null, "A User must be provided to create a Sample.");
      checkArgument(project != null, "A Project must be provided to create a Sample.");
      checkArgument(!LimsUtils.isStringEmptyOrNull(description), "Must provide a description to create a Sample");
      checkArgument(!LimsUtils.isStringEmptyOrNull(sampleType), "Must provide a sampleType to create a Sample");
      checkArgument(!LimsUtils.isStringEmptyOrNull(scientificName), "Must provide a scientificName to create a Sample");

      if (sampleAdditionalInfo == null) {
        log.debug("Create a simple unparented MISO Sample.");
        return new SampleImpl(this);
      } else {
        checkArgument(sampleAdditionalInfo != null, "SampleAdditionalInfo must be provided to create a Sample.");
        checkArgument(sampleAdditionalInfo.getSampleClass() != null,
            "SampleAdditionalInfo.sampleClass must be provided to create a Sample.");
        if (identity != null) {
          log.debug("Create an Identity Sample.");
          checkArgument(rootSampleClass != null, "A root SampleClass must be provided to create an Identity Sample.");
          return new SampleIdentityNode(this);
        } else if (sampleAnalyte != null) {
          log.debug("Create an Analyte Sample.");
          checkArgument(parent != null, "parent must be provided to create a Sample.");
          checkArgument(sampleAdditionalInfo.getTissueOrigin() != null,
              "SampleAdditionalInfo.tissueOrigin must be provided to create a Sample.");
          checkArgument(sampleAdditionalInfo.getTissueType() != null,
              "SampleAdditionalInfo.tissueType must be provided to create a Sample.");
          return SampleImpl.sampleAnalyte(this);
        } else if (sampleTissue != null) {
          return SampleImpl.sampleTissue(this);
        }
      }
      throw new IllegalArgumentException("No sample can be built with the specified parameters.");
    }
  }

}
