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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBoxable;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.PoolBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Pool")
public class PoolImpl extends AbstractBoxable implements Pool {
  private static final int CONCENTRATION_LENGTH = 17;
  private static final int DESCRIPTION_LENGTH = 255;
  private static final int ID_BARCODE_LENGTH = 255;
  private static final int NAME_LENGTH = 255;
  public static final String PREFIX = "IPO";
  private static final long serialVersionUID = 1L;
  public static final Long UNSAVED_ID = 0L;

  @OneToMany(targetEntity = PoolChangeLog.class, mappedBy = "pool", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

  @Column(length = CONCENTRATION_LENGTH)
  private Double concentration;

  @Temporal(TemporalType.DATE)
  private Date creationDate = new Date();

  @Column(length = DESCRIPTION_LENGTH)
  private String description;

  @Column(length = ID_BARCODE_LENGTH)
  private String identificationBarcode;

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

  @Column(length = NAME_LENGTH)
  private String name;

  private String alias;

  @OneToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
  @JoinTable(name = "Pool_Note", joinColumns = {
      @JoinColumn(name = "pool_poolId") }, inverseJoinColumns = {
          @JoinColumn(name = "notes_noteId") })
  private Collection<Note> notes = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  @OneToMany(mappedBy = "pool", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<PoolDilution> poolDilutions = new HashSet<>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long poolId = PoolImpl.UNSAVED_ID;

  @OneToMany(targetEntity = PoolQC.class, mappedBy = "pool", cascade = CascadeType.REMOVE)
  private final Collection<PoolQC> poolQCs = new TreeSet<>();

  @OneToOne(optional = true)
  @PrimaryKeyJoinColumn
  private PoolBoxPosition boxPosition;

  private Boolean qcPassed;

  @ManyToOne
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;

  @Transient
  // not Hibernate-managed
  private Group watchGroup;

  @ManyToMany(targetEntity = UserImpl.class)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(name = "Pool_Watcher", joinColumns = { @JoinColumn(name = "poolId") }, inverseJoinColumns = { @JoinColumn(name = "userId") })
  private Set<User> watchUsers = new HashSet<>();

  @OneToMany(targetEntity = FileAttachment.class, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "Pool_Attachment", joinColumns = { @JoinColumn(name = "poolId") }, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId") })
  private List<FileAttachment> attachments;

  public PoolImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  public PoolImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public Boxable.EntityType getEntityType() {
    return Boxable.EntityType.POOL;
  }

  @Override
  public void addNote(Note note) {
    this.notes.add(note);
  }

  @Override
  public void addWatcher(User user) {
    watchUsers.add(user);
  }

  @Override
  public int compareTo(Pool t) {
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else if (getName() != null && t.getName() != null) {
      return getName().compareTo(t.getName());
    } else if (getAlias() != null && t.getAlias() != null) {
      return getAlias().compareTo(t.getAlias());
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Pool)) return false;
    Pool other = (Pool) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(description, other.getDescription())
        .append(poolDilutions, other.getPoolDilutions())
        .append(concentration, other.getConcentration())
        .append(identificationBarcode, other.getIdentificationBarcode())
        .append(qcPassed, other.getQcPassed())
        .isEquals();
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
  public void setBoxPosition(PoolBoxPosition boxPosition) {
    this.boxPosition = boxPosition;
  }

  @Override
  public void removeFromBox() {
    this.boxPosition = null;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public Double getConcentration() {
    return this.concentration;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean getHasLowQualityMembers() {
    return poolDilutions.stream().map(PoolDilution::getPoolableElementView).anyMatch(PoolableElementView::isLowQualityLibrary);
  }

  @Override
  public long getId() {
    return poolId;
  }

  @Override
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @Override
  public String getLabelText() {
    return getAlias();
  }

  @Override
  public String getLocationBarcode() {
    return "";
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Collection<Note> getNotes() {
    return notes;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public Collection<PoolQC> getQCs() {
    return poolQCs;
  }

  @Override
  public Boolean getQcPassed() {
    return qcPassed;
  }

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public String getWatchableIdentifier() {
    return getName();
  }

  @Override
  public Set<User> getWatchers() {
    Set<User> allWatchers = new HashSet<>();
    if (watchGroup != null) allWatchers.addAll(watchGroup.getUsers());
    if (watchUsers != null) allWatchers.addAll(watchUsers);
    return allWatchers;
  }

  public Group getWatchGroup() {
    return watchGroup;
  }

  public Set<User> getWatchUsers() {
    return watchUsers;
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
    return "pool";
  }

  @Override
  public Set<String> getDuplicateIndicesSequences() {
    return getIndexSequencesWithMinimumEditDistance(1);
  }

  @Override
  public Set<String> getNearDuplicateIndicesSequences() {
    return getIndexSequencesWithMinimumEditDistance(3);
  }

  private Set<String> getIndexSequencesWithMinimumEditDistance(int minimumDistance) {
    Set<String> sequences = new HashSet<>();
    List<PoolableElementView> views = getPoolDilutions().stream().map(PoolDilution::getPoolableElementView).collect(Collectors.toList());
    if (minimumDistance > 1 && views.stream().allMatch(PoolImpl::hasFakeSequence)) return Collections.emptySet();
    for (int i = 0; i < views.size(); i++) {
      String sequence1 = getCombinedIndexSequences(views.get(i));
      if (sequence1.length() == 0) {
        continue;
      }
      for (int j = i + 1; j < views.size(); j++) {
        String sequence2 = getCombinedIndexSequences(views.get(j));
        if (sequence2.length() == 0 || !isCheckNecessary(views.get(i), views.get(j), minimumDistance)) {
          continue;
        }
        if (Index.checkEditDistance(sequence1, sequence2) < minimumDistance) {
          sequences.add(sequence1);
          sequences.add(sequence2);
        }
      }
    }
    return sequences;
  }

  private static boolean isCheckNecessary(PoolableElementView view1, PoolableElementView view2, int minimumDistance) {
    return !((hasFakeSequence(view1) || hasFakeSequence(view2))
        && (minimumDistance > 1 || getCombinedIndexSequences(view1).length() != getCombinedIndexSequences(view2).length()));
  }

  private static String getCombinedIndexSequences(PoolableElementView view) {
    return view.getIndices().stream()
        .sorted((i1, i2) -> Integer.compare(i1.getPosition(), i2.getPosition()))
        .map(Index::getSequence)
        .collect(Collectors.joining());
  }

  private static boolean hasFakeSequence(PoolableElementView view) {
    return view.getIndices().stream()
        .map(Index::getFamily)
        .anyMatch(f -> f.hasFakeSequence());
  }

  @Override
  public boolean hasLibrariesWithoutIndex() {
    return getPoolDilutions().stream().map(PoolDilution::getPoolableElementView).anyMatch(v -> v.getIndices().isEmpty());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 47).appendSuper(super.hashCode()).append(description).append(poolDilutions)
        .append(concentration).append(identificationBarcode).append(qcPassed).toHashCode();
  }

  @Override
  public void removeWatcher(User user) {
    watchUsers.remove(user);
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setId(long id) {
    this.poolId = id;
  }

  @Override
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = nullifyStringIfBlank(identificationBarcode);
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public void setNotes(Collection<Note> notes) {
    this.notes = notes;
  }

  @Override
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @Override
  public Long getPreMigrationId() {
    return null;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
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
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  @Override
  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @Override
  public void setWatchGroup(Group group) {
    this.watchGroup = group;
  }

  public void setWatchUsers(Set<User> watchUsers) {
    this.watchUsers = watchUsers;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    if (!getPoolDilutions().isEmpty()) {
      sb.append(" : ");
      sb.append(getPoolDilutions());
    }
    return sb.toString();
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    PoolChangeLog changeLog = new PoolChangeLog();
    changeLog.setPool(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public Set<PoolDilution> getPoolDilutions() {
    return poolDilutions;
  }

  @Override
  public void setPoolDilutions(Set<PoolDilution> poolDilutions) {
    this.poolDilutions = poolDilutions;
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
  public String getLongestIndex() {
    Map<Integer, Integer> lengths = poolDilutions.stream()
        .flatMap(element -> element.getPoolableElementView().getIndices().stream())
        .collect(Collectors.toMap(Index::getPosition, index -> index.getSequence().length(), Integer::max));
    if (lengths.isEmpty()) {
      return "0";
    }
    return lengths.entrySet().stream()
        .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
        .map(Entry<Integer, Integer>::getValue)
        .map(length -> length.toString())
        .collect(Collectors.joining(","));
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Pool;
  }

  @Override
  public Date getBarcodeDate() {
    return getCreationDate();
  }

  @Override
  public String getBarcodeExtraInfo() {
    return getDescription();
  }

  @Override
  public String getBarcodeSizeInfo() {
    return LimsUtils.makeVolumeAndConcentrationLabel(getVolume(), getConcentration(), getVolumeUnits().getUnits(),
        getConcentrationUnits().getUnits());
  }

  @Override
  public String getDeleteType() {
    return "Pool";
  }

  @Override
  public String getDeleteDescription() {
    return getName() + (getAlias() == null ? "" : " (" + getAlias() + ")");
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return getSecurityProfile();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
