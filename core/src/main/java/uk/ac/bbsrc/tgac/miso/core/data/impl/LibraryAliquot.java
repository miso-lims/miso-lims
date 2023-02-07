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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.nullifyStringIfBlank;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.LibraryAliquotChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "LibraryAliquot")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator")
@DiscriminatorValue("LibraryAliquot")
public class LibraryAliquot extends AbstractBoxable
    implements Comparable<LibraryAliquot>, Deletable, HierarchyEntity, Serializable {

  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long aliquotId = LibraryAliquot.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  private String alias;
  private String description;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date creationDate;

  @Column(name = "created", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  private BigDecimal concentration;

  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private ConcentrationUnit concentrationUnits;

  private Integer dnaSize;

  @ManyToOne
  @JoinColumn(name = "kitDescriptorId")
  private KitDescriptor kitDescriptor;

  private String kitLot;

  @Enumerated(EnumType.STRING)
  @Column(nullable = true)
  private VolumeUnit volumeUnits;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false, updatable = false)
  private User creator;

  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId")
  private Library library;

  @ManyToOne
  @JoinColumn(name = "parentAliquotId")
  private LibraryAliquot parentAliquot;

  @ManyToOne
  @JoinColumn(name = "targetedSequencingId")
  private TargetedSequencing targetedSequencing;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "lastModifier")
  private User lastModifier;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  private String identificationBarcode;

  @Column(updatable = false)
  private Long preMigrationId;

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private LibraryAliquotBoxPosition boxPosition;

  private BigDecimal ngUsed;
  private BigDecimal volumeUsed;

  @ManyToOne(targetEntity = DetailedQcStatusImpl.class)
  @JoinColumn(name = "detailedQcStatusId")
  private DetailedQcStatus detailedQcStatus;
  private String detailedQcStatusNote;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "qcUser")
  private User qcUser;

  @Temporal(TemporalType.DATE)
  private Date qcDate;

  @OneToMany(targetEntity = LibraryAliquotChangeLog.class, mappedBy = "libraryAliquot", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Immutable
  @ManyToMany
  @JoinTable(name = "Transfer_LibraryAliquot", joinColumns = { @JoinColumn(name = "aliquotId") }, inverseJoinColumns = {
      @JoinColumn(name = "transferId") })
  private Set<ListTransferView> listTransferViews;

  @Override
  public Boxable.EntityType getEntityType() {
    return Boxable.EntityType.LIBRARY_ALIQUOT;
  }

  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  public LibraryAliquot getParentAliquot() {
    return parentAliquot;
  }

  public void setParentAliquot(LibraryAliquot parentAliquot) {
    this.parentAliquot = parentAliquot;
  }

  public TargetedSequencing getTargetedSequencing() {
    return targetedSequencing;
  }

  public void setTargetedSequencing(TargetedSequencing targetedSequencing) {
    this.targetedSequencing = targetedSequencing;
  }

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastUpdated) {
    this.lastModifier = lastUpdated;
  }

  @Override
  public long getId() {
    return aliquotId;
  }

  @Override
  public void setId(long id) {
    this.aliquotId = id;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public User getCreator() {
    return creator;
  }

  @Override
  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreationDate() {
    return this.creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public BigDecimal getConcentration() {
    return this.concentration;
  }

  public void setConcentration(BigDecimal concentration) {
    this.concentration = concentration;
  }

  public ConcentrationUnit getConcentrationUnits() {
    return this.concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  public KitDescriptor getKitDescriptor() {
    return kitDescriptor;
  }

  public void setKitDescriptor(KitDescriptor kitDescriptor) {
    this.kitDescriptor = kitDescriptor;
  }

  public String getKitLot() {
    return kitLot;
  }

  public void setKitLot(String kitLot) {
    this.kitLot = kitLot;
  }

  public VolumeUnit getVolumeUnits() {
    return this.volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @Override
  public String getIdentificationBarcode() {
    return this.identificationBarcode;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = nullifyStringIfBlank(identificationBarcode);
  }

  @Override
  @CoverageIgnore
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getCreationDate());
    sb.append(" : ");
    sb.append(getConcentration());
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equalsByIdFirst(this, obj,
        LibraryAliquot::getIdentificationBarcode,
        LibraryAliquot::getConcentration,
        LibraryAliquot::getCreationDate,
        LibraryAliquot::getCreator,
        LibraryAliquot::getLibrary,
        LibraryAliquot::getTargetedSequencing,
        LibraryAliquot::getDetailedQcStatus,
        LibraryAliquot::getDetailedQcStatusNote,
        LibraryAliquot::getKitDescriptor,
        LibraryAliquot::getKitLot);
  }

  @CoverageIgnore
  @Override
  public int hashCode() {
    return LimsUtils.hashCodeByIdFirst(this,
        identificationBarcode,
        concentration,
        creationDate,
        creator,
        library,
        targetedSequencing,
        detailedQcStatus,
        detailedQcStatusNote,
        kitDescriptor,
        kitLot);
  }

  @Override
  public int compareTo(LibraryAliquot o) {
    if (getId() < o.getId()) return -1;
    if (getId() > o.getId()) return 1;
    return 0;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public Box getBox() {
    return boxPosition == null ? null : boxPosition.getBox();
  }

  @Override
  public String getBoxPosition() {
    return boxPosition == null ? null : boxPosition.getPosition();
  }

  public void setBoxPosition(LibraryAliquotBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public void removeFromBox() {
    this.boxPosition = null;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  @Override
  public void setLastModified(Date lastModified) {
    this.lastUpdated = lastModified;
  }

  @Override
  public String getLocationBarcode() {
    return null;
  }

  @Override
  public Date getBarcodeDate() {
    return getCreationDate();
  }


  @Override
  public String getDeleteType() {
    return "Library Aliquot";
  }

  @Override
  public String getDeleteDescription() {
    return getName()
        + (getLibrary() == null || getLibrary().getAlias() == null ? "" : " (" + getLibrary().getAlias() + ")");
  }

  public BigDecimal getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(BigDecimal ngUsed) {
    this.ngUsed = ngUsed;
  }

  @Override
  public BigDecimal getVolumeUsed() {
    return volumeUsed;
  }

  @Override
  public void setVolumeUsed(BigDecimal volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  @Override
  public DetailedQcStatus getDetailedQcStatus() {
    return detailedQcStatus;
  }

  @Override
  public void setDetailedQcStatus(DetailedQcStatus detailedQcStatus) {
    this.detailedQcStatus = detailedQcStatus;
  }

  @Override
  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  @Override
  public void setDetailedQcStatusNote(String detailedQcStatusNote) {
    this.detailedQcStatusNote = detailedQcStatusNote;
  }

  @Override
  public User getQcUser() {
    return qcUser;
  }

  @Override
  public void setQcUser(User qcUser) {
    this.qcUser = qcUser;
  }

  @Override
  public Date getQcDate() {
    return qcDate;
  }

  @Override
  public void setQcDate(Date qcDate) {
    this.qcDate = qcDate;
  }

  @Override
  public Date getCreationTime() {
    return creationTime;
  }

  @Override
  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    LibraryAliquotChangeLog change = new LibraryAliquotChangeLog();
    change.setLibraryAliquot(this);
    change.setSummary(summary);
    change.setColumnsChanged(columnsChanged);
    change.setUser(user);
    return change;
  }

  @Override
  public HierarchyEntity getParent() {
    return getParentAliquot() != null ? getParentAliquot() : getLibrary();
  }

  @Override
  public Set<ListTransferView> getTransferViews() {
    if (listTransferViews == null) {
      listTransferViews = new HashSet<>();
    }
    return listTransferViews;
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitLibraryAliquot(this);
  }

}
