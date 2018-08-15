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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.BatchSize;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14-May-2012
 * @since 0.1.6
 */
@Entity
@Table(name = "SequencerPartitionContainer")
@Inheritance(strategy = InheritanceType.JOINED)
public class SequencerPartitionContainerImpl implements SequencerPartitionContainer {

  private static final long serialVersionUID = 1L;
  public static final long UNSAVED_ID = 0L;

  public static final int DEFAULT_PARTITION_LIMIT = 8;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long containerId = SequencerPartitionContainerImpl.UNSAVED_ID;

  /**
   * identificationBarcode is displayed as "serial number" to the user
   */
  private String identificationBarcode;

  private String description;

  @ManyToMany(targetEntity = Run.class, mappedBy = "containers")
  @BatchSize(size = 10)
  private Collection<Run> runs = null;

  @ManyToOne(targetEntity = SecurityProfile.class, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "securityProfile_profileId")
  private SecurityProfile securityProfile;

  @OneToMany(targetEntity = SequencerPartitionContainerChangeLog.class, mappedBy = "sequencerPartitionContainer", cascade = CascadeType.REMOVE)
  private final Collection<ChangeLog> changeLog = new ArrayList<>();

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

  @OneToMany(targetEntity = PartitionImpl.class, mappedBy = "sequencerPartitionContainer", cascade = CascadeType.ALL)
  @OrderBy("partitionNumber")
  private List<Partition> partitions = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "clusteringKit")
  private KitDescriptor clusteringKit;

  @ManyToOne
  @JoinColumn(name = "multiplexingKit")
  private KitDescriptor multiplexingKit;

  @ManyToOne
  @JoinColumn(name = "sequencingContainerModelId")
  private SequencingContainerModel model;

  @OneToMany(targetEntity = ContainerQC.class, mappedBy = "container")
  private final Collection<ContainerQC> containerQCs = new TreeSet<>();

  /**
   * Construct a new SequencerPartitionContainer with a default empty SecurityProfile
   */
  public SequencerPartitionContainerImpl() {
    this(new SecurityProfile());
  }

  /**
   * Construct a new SequencerPartitionContainer with a SecurityProfile owned by the given User
   * 
   * @param user of type User
   */
  public SequencerPartitionContainerImpl(User user) {
    this(new SecurityProfile(user));
  }

  private SequencerPartitionContainerImpl(SecurityProfile securityProfile) {
    setSecurityProfile(securityProfile);
    setPartitionLimit(DEFAULT_PARTITION_LIMIT);
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
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public long getId() {
    return containerId;
  }

  @Override
  public void setId(long id) {
    this.containerId = id;
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
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getLabelText() {
    return getIdentificationBarcode() + " (" + getModel().getAlias() + ")";
  }

  /**
   * Containers don't have names, but they implement an interface which requires this method.
   */
  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Run> getRuns() {
    return runs;
  }

  public void setRuns(Collection<Run> runs) {
    this.runs = runs;
  }

  @Override
  public Run getLastRun() {
    return getRuns().stream().filter(r -> r.getStartDate() != null).max((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
        .orElse(null);
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
  public List<Partition> getPartitions() {
    return partitions;
  }

  @Override
  public void setPartitions(List<Partition> partitions) {
    this.partitions = partitions;
    for (Partition p : partitions) {
      p.setSequencerPartitionContainer(this);
    }
    Collections.sort(partitions, partitionNumberComparator);
  }

  @Override
  public Partition getPartitionAt(int partitionNumber) throws IndexOutOfBoundsException {
    return partitions.get(partitionNumber - 1);
  }

  @Override
  public void setPartitionLimit(int partitionLimit) {
    getPartitions().clear();
    for (int i = 0; i < partitionLimit; i++) {
      PartitionImpl partition = new PartitionImpl();
      partition.setSequencerPartitionContainer(this);
      partition.setPartitionNumber(i + 1);
      getPartitions().add(partition);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof SequencerPartitionContainer)) return false;
    SequencerPartitionContainer them = (SequencerPartitionContainer) obj;
    // If not saved, then compare resolved actual objects. Otherwise just compare IDs.
    if (getId() == SequencerPartitionContainerImpl.UNSAVED_ID || them.getId() == SequencerPartitionContainerImpl.UNSAVED_ID) {
      return getIdentificationBarcode().equals(them.getIdentificationBarcode());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != SequencerPartitionContainerImpl.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = -1;
      if (getIdentificationBarcode() != null) hashcode = 37 * hashcode + getIdentificationBarcode().hashCode();
      return hashcode;
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getIdentificationBarcode());
    return sb.toString();
  }

  @Override
  public int compareTo(SequencerPartitionContainer t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public ChangeLog createChangeLog(String summary, String columnsChanged, User user) {
    SequencerPartitionContainerChangeLog changeLog = new SequencerPartitionContainerChangeLog();
    changeLog.setSequencerPartitionContainer(this);
    changeLog.setSummary(summary);
    changeLog.setColumnsChanged(columnsChanged);
    changeLog.setUser(user);
    return changeLog;
  }

  @Override
  public Date getBarcodeDate() {
    return getCreationTime();
  }

  @Override
  public String getBarcodeExtraInfo() {
    return getModel().getAlias();
  }

  @Override
  public String getAlias() {
    return getIdentificationBarcode();
  }

  @Override
  public KitDescriptor getClusteringKit() {
    return clusteringKit;
  }

  @Override
  public void setClusteringKit(KitDescriptor clusteringKit) {
    this.clusteringKit = clusteringKit;
  }

  @Override
  public KitDescriptor getMultiplexingKit() {
    return multiplexingKit;
  }

  @Override
  public void setMultiplexingKit(KitDescriptor multiplexingKit) {
    this.multiplexingKit = multiplexingKit;
  }

  private static final Comparator<Partition> partitionNumberComparator = new Comparator<Partition>() {

    @Override
    public int compare(Partition o1, Partition o2) {
      return o1.getPartitionNumber().compareTo(o2.getPartitionNumber());
    }

  };

  @Override
  public Collection<ContainerQC> getQCs() {
    return containerQCs;
  }

  @Override
  public QcTarget getQcTarget() {
    return QcTarget.Container;
  }

  @Override
  public String getBarcodeSizeInfo() {
    return String.format("%s: %d", getModel().getPlatformType().getPluralPartitionName(), getPartitions().size());
  }

  @Override
  public SequencingContainerModel getModel() {
    return model;
  }

  @Override
  public void setModel(SequencingContainerModel model) {
    this.model = model;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return getModel().getPlatformType().getContainerName();
  }

  @Override
  public String getDeleteDescription() {
    return getIdentificationBarcode() + " (" + getModel().getAlias() + ")";
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return getSecurityProfile();
  }

}
