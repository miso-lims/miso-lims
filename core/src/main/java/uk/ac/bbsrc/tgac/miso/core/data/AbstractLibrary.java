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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

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
   private long libraryId = AbstractLibrary.UNSAVED_ID;

   private String name;
   private String description;
   private String accession;
   private Date creationDate = new Date();
   private String identificationBarcode;
   private String locationBarcode;
   private TagBarcode tagBarcode;

   private HashMap<Integer, TagBarcode> tagBarcodes = new HashMap<Integer, TagBarcode>();

   private Boolean paired;

   private final Collection<LibraryQC> libraryQCs = new TreeSet<LibraryQC>();
   private final Collection<LibraryDilution> libraryDilutions = new HashSet<LibraryDilution>();
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

   @Override
   @CoverageIgnore
   @Deprecated
   public Long getLibraryId() {
      return libraryId;
   }

   @Override
   @CoverageIgnore
   @Deprecated
   public void setLibraryId(Long libraryId) {
      this.libraryId = libraryId;
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

   @CoverageIgnore
   @Override
   public String getLabelText() {
      return getAlias();
   }

   @CoverageIgnore
   @Override
   @Deprecated
   public TagBarcode getTagBarcode() {
      return tagBarcode;
   }

   @CoverageIgnore
   @Override
   @Deprecated
   public void setTagBarcode(TagBarcode tagBarcode) {
      this.tagBarcode = tagBarcode;
   }

   @Override
   public HashMap<Integer, TagBarcode> getTagBarcodes() {
      return tagBarcodes;
   }

   @Override
   public void setTagBarcodes(HashMap<Integer, TagBarcode> tagBarcodes) {
      this.tagBarcodes = tagBarcodes;
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
         //TODO : This doesn't throw any exceptions.  Remove.
         e.printStackTrace();
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
      return platformName;
   }

   @Override
   public void setPlatformName(String platformName) {
      this.platformName = platformName;
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
   public Integer getLibraryQuant() {
      return libraryQuant;
   }

   @Override
   public void setLibraryQuant(Integer libraryQuant) {
      this.libraryQuant = libraryQuant;
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
   public void setNotes(Collection<Note> notes) {
      this.notes = notes;
   }

   @Override
   public Set<Plate<? extends LinkedList<Library>, Library>> getPlates() {
      return plates;
   }

   @CoverageIgnore
   public void addPlate(Plate<? extends LinkedList<Library>, Library> plate) {
      this.plates.add(plate);
   }

   public void setPlates(Set<Plate<? extends LinkedList<Library>, Library>> plates) {
      this.plates = plates;
   }

   @Override
   public Date getLastUpdated() {
      return lastUpdated;
   }

   @Override
   public void setLastUpdated(Date lastUpdated) {
      this.lastUpdated = lastUpdated;
   }

   @CoverageIgnore
   @Override
   public boolean isDeletable() {
      return getId() != AbstractLibrary.UNSAVED_ID && getLibraryDilutions().isEmpty();
      // && getLibraryQCs().isEmpty();
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

   /**
    * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
    */
   @CoverageIgnore
   @Override
   public boolean equals(Object obj) {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof AbstractLibrary)) return false;
      final Library them = (Library) obj;
      // If not saved, then compare resolved actual objects. Otherwise
      // just compare IDs.
      if (getId() == AbstractLibrary.UNSAVED_ID || them.getId() == AbstractLibrary.UNSAVED_ID) {
         if (getName() != null && them.getName() != null) {
            return getName().equals(them.getName());
         } else {
            return getAlias().equals(them.getAlias());
         }
      } else {
         return getId() == them.getId();
      }
   }

   @CoverageIgnore
   @Override
   public int hashCode() {
      if (AbstractLibrary.UNSAVED_ID != getId()) {
         return (int) getId();
      } else {
         final int PRIME = 37;
         int hashcode = 1;
         if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
         if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
         // if (getLibraryDilutions() != null && !getLibraryDilutions().isEmpty()) hashcode = PRIME * hashcode +
         // getLibraryDilutions().hashCode();
         // if (getLibraryQCs() != null && !getLibraryQCs().isEmpty()) hashcode = PRIME * hashcode + getLibraryQCs().hashCode();
         return hashcode;
      }
   }

   @CoverageIgnore
   @Override
   public int compareTo(Object o) {
      final Library l = (Library) o;
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
      sb.append(" : ");
      return sb.toString();
   }
}
