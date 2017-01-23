/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.util.List;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDerivedInfo;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * Skeleton implementation of a Library
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@MappedSuperclass
public abstract class AbstractLibrary extends AbstractBoxable implements Library {

  protected static final Logger log = LoggerFactory.getLogger(AbstractLibrary.class);

  public static final String UNITS = "nM";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long libraryId = AbstractLibrary.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  private String description;
  private String accession;

  @Column(nullable = false)
  private Date creationDate = new Date();

  private String identificationBarcode;
  private String locationBarcode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlatformType platformName;

  private Boolean qcPassed;

  @Column(nullable = false)
  private boolean lowQuality = false;

  @Column(nullable = false)
  private boolean paired;

  @Column(name = "concentration")
  private Double initialConcentration;

  @ManyToMany(targetEntity = Index.class)
  @JoinTable(name = "Library_Index", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "index_indexId", nullable = false) })
  private List<Index> indices = new ArrayList<>();

  @OneToMany(targetEntity = LibraryQCImpl.class, mappedBy = "library", cascade = CascadeType.ALL)
  private final Collection<LibraryQC> libraryQCs = new TreeSet<>();

  @OneToMany(targetEntity = LibraryDilution.class, mappedBy = "library", cascade = CascadeType.ALL)
  private final Collection<LibraryDilution> libraryDilutions = new HashSet<>();

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile;

  @ManyToOne(targetEntity = SampleImpl.class)
  @JoinColumn(name = "sample_sampleId")
  @JsonBackReference
  private Sample sample;

  @ManyToOne
  @JoinColumn(name = "libraryType")
  private LibraryType libraryType;

  @ManyToOne
  @JoinColumn(name = "librarySelectionType")
  private LibrarySelectionType librarySelectionType;

  @ManyToOne
  @JoinColumn(name = "libraryStrategyType")
  private LibraryStrategyType libraryStrategyType;

  @OneToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Library_Note", joinColumns = {
      @JoinColumn(name = "library_libraryId", nullable = false, updatable = false) }, inverseJoinColumns = {
      @JoinColumn(name = "notes_noteId", nullable = false, updatable = false) })
  private Collection<Note> notes = new HashSet<>();

  @OneToMany(targetEntity = LibraryChangeLog.class, mappedBy = "library")
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @OneToOne(targetEntity = LibraryDerivedInfo.class)
  @PrimaryKeyJoinColumn
  private LibraryDerivedInfo derivedInfo;

  @ManyToOne(targetEntity = BoxImpl.class)
  @JoinFormula("(SELECT boxId FROM BoxPosition WHERE targetId = id AND targetType = 'L')")
  private Box box;
  @Formula("(SELECT position FROM BoxPosition WHERE targetId = id AND targetType = 'L')")
  private String position;

  @Override
  public String getBoxPosition() {
    return position;
  }

  @Override
  public long getId() {
    return libraryId;
  }

  @Override
  public void setId(long id) {
    this.libraryId = id;
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
  public String getAccession() {
    return accession;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public Box getBox() {
    return box;
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

  @CoverageIgnore
  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public List<Index> getIndices() {
    return indices;
  }

  @Override
  public void setIndices(List<Index> originalIndices) {
    List<Index> indices = new ArrayList<>();
    for (Index index : originalIndices) {
      if (index != null) {
        indices.add(index);
      }
    }
    Index.sort(indices);
    IndexFamily current = null;
    for (Index index : indices) {
      if (index == null) continue;
      if (current == null) {
        current = index.getFamily();
      } else {
        if (current.getId() != index.getFamily().getId()) {
          throw new IllegalArgumentException(String.format(
              "Indices not all from the same family. (%d:%s vs %d:%s)",
              current.getId(),
              current.getName(),
              index.getFamily().getId(),
              index.getFamily().getName()));
        }
      }
    }
    this.indices = indices;
  }

  @Override
  public Boolean getPaired() {
    return paired;
  }

  @Override
  public void setPaired(Boolean paired) {
    this.paired = paired;
  }

  @Override
  public void addQc(LibraryQC libraryQc) throws MalformedLibraryQcException {
    this.libraryQCs.add(libraryQc);
    try {
      libraryQc.setLibrary(this);
    } catch (final MalformedLibraryException e) {
      // TODO : This doesn't throw any exceptions. Remove.
      log.error("add QC", e);
    }
  }

  @Override
  public Collection<LibraryQC> getLibraryQCs() {
    return libraryQCs;
  }

  @Override
  public void addDilution(LibraryDilution libraryDilution) throws MalformedDilutionException {
    this.libraryDilutions.add(libraryDilution);
    libraryDilution.setLibrary(this);
  }

  @Override
  public Collection<LibraryDilution> getLibraryDilutions() {
    return libraryDilutions;
  }

  @Override
  public Sample getSample() {
    return sample;
  }

  @Override
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  @Override
  public LibraryType getLibraryType() {
    return libraryType;
  }

  @Override
  public void setLibraryType(LibraryType libraryType) {
    this.libraryType = libraryType;
  }

  @Override
  public LibrarySelectionType getLibrarySelectionType() {
    return librarySelectionType;
  }

  @Override
  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  @Override
  public LibraryStrategyType getLibraryStrategyType() {
    return libraryStrategyType;
  }

  @Override
  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }

  @Override
  public String getPlatformName() {
    return platformName == null ? null : platformName.getKey();
  }

  @Override
  public void setPlatformName(String platformName) {
    this.platformName = PlatformType.get(platformName);
  }

  @Override
  public Double getInitialConcentration() {
    return initialConcentration;
  }

  @Override
  public void setInitialConcentration(Double initialConcentration) {
    this.initialConcentration = initialConcentration;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

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
  public Date getLastModified() {
    return (derivedInfo == null ? null : derivedInfo.getLastModified());
  }

  @Override
  public void setLowQuality(boolean lowquality) {
    lowQuality = lowquality;
  }

  @Override
  public boolean isLowQuality() {
    return lowQuality;
  }

  @CoverageIgnore
  @Override
  public boolean isDeletable() {
    return getId() != AbstractLibrary.UNSAVED_ID && getLibraryDilutions().isEmpty();
  }

  @CoverageIgnore
  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @CoverageIgnore
  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    } else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

  @CoverageIgnore
  @Override
  public int compareTo(Library l) {
    if (getId() != 0L && l.getId() != 0L) {
      if (getId() < l.getId()) return -1;
      if (getId() > l.getId()) return 1;
    } else if (getName() != null && l.getName() != null) {
      return getName().compareTo(l.getName());
    } else if (getAlias() != null && l.getAlias() != null) {
      return getAlias().compareTo(l.getAlias());
    }
    return 0;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }


  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public IndexFamily getCurrentFamily() {
    if (indices == null) {
      return IndexFamily.NULL;
    }
    for (Index index : indices) {
      if (index != null) {
        return index.getFamily();
      }
    }
    return IndexFamily.NULL;
  }

  @Override
  public SampleTissue getSampleTissue() {
    if (this.getSample() instanceof DetailedSample) {

      for (DetailedSample parent = (DetailedSample) this.getSample(); parent != null; parent = parent.getParent()) {
        if (parent instanceof SampleTissue) return (SampleTissue) parent;
      }
    }
    return null;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(3, 33)
        .appendSuper(super.hashCode())
        .append(accession)
        .append(getAlias())
        .append(description)
        .append(identificationBarcode)
        .append(indices)
        .append(initialConcentration)
        .append(librarySelectionType)
        .append(libraryStrategyType)
        .append(libraryType)
        .append(locationBarcode)
        .append(lowQuality)
        .append(paired)
        .append(platformName)
        .append(qcPassed)
        .toHashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractLibrary other = (AbstractLibrary) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(accession, other.accession)
        .append(getAlias(), other.getAlias())
        .append(description, other.description)
        .append(identificationBarcode, other.identificationBarcode)
        .append(indices, other.indices)
        .append(initialConcentration, other.initialConcentration)
        .append(librarySelectionType, other.librarySelectionType)
        .append(libraryStrategyType, other.libraryStrategyType)
        .append(libraryType, other.libraryType)
        .append(locationBarcode, other.locationBarcode)
        .append(lowQuality, other.lowQuality)
        .append(paired, other.paired)
        .append(platformName, other.platformName)
        .append(qcPassed, other.qcPassed)
        .isEquals();
  }
}
