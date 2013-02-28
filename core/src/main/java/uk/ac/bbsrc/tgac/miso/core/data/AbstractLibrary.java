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
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.*;

/**
 * Skeleton implementation of a Library
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractLibrary implements Library {
  public static final Long UNSAVED_ID = 0L;
  public static final String UNITS = "nM";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long libraryId = AbstractLibrary.UNSAVED_ID;

  private String name;
  private String description;
  private String accession;
  private Date creationDate = new Date();
  private String identificationBarcode;
  private String locationBarcode;
  private TagBarcode tagBarcode;

  private HashMap<Integer, TagBarcode> tagBarcodes = new HashMap<Integer, TagBarcode>();

  private Boolean paired;

  private Collection<LibraryQC> libraryQCs = new HashSet<LibraryQC>();
  private Collection<LibraryDilution> libraryDilutions = new HashSet<LibraryDilution>();
  private Set<Plate<? extends LinkedList<Library>, Library>> plates = new HashSet<Plate<? extends LinkedList<Library>, Library>>();

  private SecurityProfile securityProfile;
  private Sample sample;
  private LibraryType libraryType;
  private LibrarySelectionType librarySelectionType;
  private LibraryStrategyType libraryStrategyType;
  private String platformName;
  private Double initialConcentration;
  private Integer libraryQuant;
  private String alias;
  private Boolean qcPassed;

  private Collection<Note> notes = new HashSet<Note>();

  private Date lastUpdated;

  @Deprecated
  public Long getLibraryId() {
    return libraryId;
  }

  @Deprecated
  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  @Override
  public long getId() {
    return libraryId;
  }

  public void setId(long id) {
    this.libraryId = id;
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

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
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

  @Deprecated
  public TagBarcode getTagBarcode() {
    return tagBarcode;
  }

  @Deprecated
  public void setTagBarcode(TagBarcode tagBarcode) {
    this.tagBarcode = tagBarcode;
  }

  public HashMap<Integer, TagBarcode> getTagBarcodes() {
    return tagBarcodes;
  }

  public void setTagBarcodes(HashMap<Integer, TagBarcode> tagBarcodes) {
    this.tagBarcodes = tagBarcodes;
  }

  public Boolean getPaired() {
    return paired;
  }

  public void setPaired(Boolean paired) {
    this.paired = paired;
  }

  public void addQc(LibraryQC libraryQc) throws MalformedLibraryQcException {
    this.libraryQCs.add(libraryQc);
    try {
      libraryQc.setLibrary(this);
    }
    catch (MalformedLibraryException e) {
      e.printStackTrace();
    }
  }

  public Collection<LibraryQC> getLibraryQCs() {
    return libraryQCs;
  }

  public void addDilution(LibraryDilution libraryDilution) throws MalformedDilutionException {
    this.libraryDilutions.add(libraryDilution);
    libraryDilution.setLibrary(this);
  }

  public Collection<LibraryDilution> getLibraryDilutions() {
    return libraryDilutions;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public LibraryType getLibraryType() {
    return libraryType;
  }

  public void setLibraryType(LibraryType libraryType) {
    this.libraryType = libraryType;
  }

  public LibrarySelectionType getLibrarySelectionType() {
    return librarySelectionType;
  }

  public void setLibrarySelectionType(LibrarySelectionType librarySelectionType) {
    this.librarySelectionType = librarySelectionType;
  }

  public LibraryStrategyType getLibraryStrategyType() {
    return libraryStrategyType;
  }

  public void setLibraryStrategyType(LibraryStrategyType libraryStrategyType) {
    this.libraryStrategyType = libraryStrategyType;
  }
  
  public String getPlatformName() {
    return platformName;
  }

  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }

  public Double getInitialConcentration() {
    return initialConcentration;
  }

  public void setInitialConcentration(Double initialConcentration) {
    this.initialConcentration = initialConcentration;
  }

  public Integer getLibraryQuant() {
    return libraryQuant;
  }

  public void setLibraryQuant(Integer libraryQuant) {
    this.libraryQuant = libraryQuant;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public Collection<Note> getNotes() {
    return notes;
  }

  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }  

  @Override
  public Set<Plate<? extends LinkedList<Library>, Library>> getPlates() {
    return plates;
  }

  public void addPlate(Plate<? extends LinkedList<Library>, Library> plate) {
    this.plates.add(plate);
  }

  public void setPlates(Set<Plate<? extends LinkedList<Library>, Library>> plates) {
    this.plates = plates;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public boolean isDeletable() {
    return getId() != AbstractLibrary.UNSAVED_ID &&
           getLibraryDilutions().isEmpty() &&
           getLibraryQCs().isEmpty();    
  }

  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    setSecurityProfile(parent.getSecurityProfile());
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name,
   * description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof AbstractLibrary))
      return false;
    Library them = (Library) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractLibrary.UNSAVED_ID
        || them.getId() == AbstractLibrary.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      }
      else {
        return getAlias().equals(them.getAlias());
      }
    }
    else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (AbstractLibrary.UNSAVED_ID != getId()) {
      return (int)getId();
    }
    else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
//      if (getLibraryDilutions() != null && !getLibraryDilutions().isEmpty()) hashcode = PRIME * hashcode + getLibraryDilutions().hashCode();
//      if (getLibraryQCs() != null && !getLibraryQCs().isEmpty()) hashcode = PRIME * hashcode + getLibraryQCs().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Library l = (Library)o;
    if (getId() != 0L && l.getId() != 0L) {
      if (getId() < l.getId()) return -1;
      if (getId() > l.getId()) return 1;
    }
    else if (getName() != null && l.getName() != null) {
      return getName().compareTo(l.getName());
    }
    else if (getAlias() != null && l.getAlias() != null) {
      return getAlias().compareTo(l.getAlias());
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    return sb.toString();
  }
}
