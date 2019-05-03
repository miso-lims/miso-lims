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
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.SampleBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SampleChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Skeleton implementation of a Sample
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@MappedSuperclass
public abstract class AbstractSample extends AbstractBoxable implements Sample {

  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(AbstractSample.class);

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long sampleId = Sample.UNSAVED_ID;

  @ManyToOne(targetEntity = ProjectImpl.class)
  @JoinColumn(name = "project_projectId")
  private Project project;

  @OneToMany(targetEntity = LibraryImpl.class, mappedBy = "sample")
  private final Collection<Library> libraries = new HashSet<>();

  @OneToMany(targetEntity = SampleQC.class, mappedBy = "sample", cascade = CascadeType.ALL)
  private Collection<SampleQC> sampleQCs = new TreeSet<>();

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Sample_Note", joinColumns = {
      @JoinColumn(name = "sample_sampleId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  @OneToMany(targetEntity = SampleChangeLog.class, mappedBy = "sample", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  private String accession;
  private String name;
  private String alias;
  private String description;
  private String scientificName;
  private String taxonIdentifier;
  private String sampleType;

  @Temporal(TemporalType.DATE)
  private Date receivedDate;
  private Boolean qcPassed;
  private String identificationBarcode;
  private String locationBarcode;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private Double concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier", nullable = false)
  private User lastModifier;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private SampleBoxPosition boxPosition;

  @OneToMany(targetEntity = FileAttachment.class)
  @JoinTable(name = "Sample_Attachment", joinColumns = { @JoinColumn(name = "sampleId") }, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId") })
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Override
  public EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  @Override
  public Box getBox() {
    return boxPosition == null ? null : boxPosition.getBox();
  }

  @Override
  public String getBoxPosition() {
    return boxPosition == null ? null : boxPosition.getPosition();
  }

  @Override
  public void setBoxPosition(SampleBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public void removeFromBox() {
    this.boxPosition = null;
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
  public Date getLastModified() {
    return lastModified;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date created) {
    this.creationTime = created;
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
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
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
  public void addLibrary(Library l) {
    this.libraries.add(l);
  }

  @Override
  public Collection<Library> getLibraries() {
    return libraries;
  }

  @Override
  public Collection<SampleQC> getQCs() {
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
  public Long getPreMigrationId() {
    return null;
  }

  @Override
  public int compareTo(Sample s) {
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
  public int hashCode() {
    return new HashCodeBuilder(7, 37)
        .appendSuper(super.hashCode())
        .append(sampleId)
        .append(accession)
        .append(description)
        .append(identificationBarcode)
        .append(locationBarcode)
        .append(project)
        .append(qcPassed)
        .append(receivedDate)
        .append(sampleType)
        .append(scientificName)
        .append(taxonIdentifier)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractSample other = (AbstractSample) obj;
    return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(sampleId, other.sampleId)
        .append(accession, other.accession)
        .append(getAlias(), other.getAlias())
        .append(description, other.description)
        .append(identificationBarcode, other.identificationBarcode)
        .append(locationBarcode, other.locationBarcode)
        .append(project, other.project)
        .append(qcPassed, other.qcPassed)
        .append(receivedDate, other.receivedDate)
        .append(sampleType, other.sampleType)
        .append(scientificName, other.scientificName)
        .append(taxonIdentifier, other.taxonIdentifier)
        .isEquals();
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Sample;
  }

  @Override
  public Date getBarcodeDate() {
    return getReceivedDate() == null ? getCreationTime() : getReceivedDate();
  }

  @Override
  public String getBarcodeExtraInfo() {
    return getDescription();
  }

  @Override
  public String getBarcodeSizeInfo() {
    return LimsUtils.makeVolumeAndConcentrationLabel(getVolume(), getConcentration(), getVolumeUnits(),
        getConcentrationUnits());
  }

  @Override
  public String getDeleteType() {
    return "Sample";
  }

  @Override
  public String getDeleteDescription() {
    return getName()
        + (getAlias() == null ? "" : " (" + getAlias() + ")");
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  @Override
  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @Override
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  @Override
  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  @Override
  public Double getConcentration() {
    return concentration;
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  @Override
  public List<FileAttachment> getAttachments() {
    return attachments;
  }

  @Override
  public void setAttachments(List<FileAttachment> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String getAttachmentsTarget() {
    return "sample";
  }

  @Override
  public List<FileAttachment> getPendingAttachmentDeletions() {
    return pendingAttachmentDeletions;
  }

  @Override
  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions) {
    this.pendingAttachmentDeletions = pendingAttachmentDeletions;
  }

}
