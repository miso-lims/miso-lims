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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
public class SequencerPartitionContainerImpl extends AbstractSequencerPartitionContainer<SequencerPoolPartition> implements Serializable {
  protected static final Logger log = LoggerFactory.getLogger(SequencerPartitionContainerImpl.class);

  @OneToMany(targetEntity = PartitionImpl.class, cascade = CascadeType.ALL)
  @JoinTable(name = "SequencerPartitionContainer_Partition", joinColumns = {
      @JoinColumn(name = "container_containerId", updatable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "partitions_partitionId", updatable = false) })
  private List<SequencerPoolPartition> partitions = new AutoPopulatingList<SequencerPoolPartition>(PartitionImpl.class);

  @Transient
  private int partitionLimit = 8;

  /**
   * Construct a new SequencerPartitionContainer with a default empty SecurityProfile
   */
  public SequencerPartitionContainerImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new SequencerPartitionContainer with a SecurityProfile owned by the given User
   * 
   * @param user of type User
   */
  public SequencerPartitionContainerImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public List<SequencerPoolPartition> getPartitions() {
    return partitions;
  }

  @Override
  public void setPartitions(List<SequencerPoolPartition> partitions) {
    this.partitions = partitions;
  }

  @Override
  public SequencerPoolPartition getPartitionAt(int partitionNumber) throws IndexOutOfBoundsException {
    return partitions.get(partitionNumber - 1);
  }

  @Override
  public void setPartitionLimit(int partitionLimit) {
    this.partitionLimit = partitionLimit;
  }

  @Override
  public void initEmptyPartitions() {
    getPartitions().clear();
    for (int i = 0; i < partitionLimit; i++) {
      addNewPartition();
    }
  }

  @Override
  public void addNewPartition() {
    if (getPartitions().size() < partitionLimit) {
      PartitionImpl partition = new PartitionImpl();
      partition.setSequencerPartitionContainer(this);
      partition.setPartitionNumber(getPartitions().size() + 1);
      partition.setSecurityProfile(getSecurityProfile());
      getPartitions().add(partition);
    } else {
      log.warn("This sequencing container is limited to " + partitionLimit + " lanes");
    }
  }

  public void addPartition(SequencerPoolPartition partition) {
    if (getPartitions().size() < partitionLimit) {
      if (!getPartitions().contains(partition)) {
        if (partition.getSequencerPartitionContainer() == null) partition.setSequencerPartitionContainer(this);
        if (partition.getPartitionNumber() == null) partition.setPartitionNumber(getPartitions().size() + 1);
        if (partition.getSecurityProfile() == null) partition.setSecurityProfile(getSecurityProfile());
        getPartitions().add(partition);
      } else {
        log.warn("This sequencing container already contains that lane");
      }
    } else {
      log.warn("This sequencing container is limited to " + partitionLimit + " lanes");
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());
    if (getPartitions() != null) {
      sb.append(" : [");
      sb.append(LimsUtils.join(getPartitions(), ","));
      sb.append("]");
    } else {
      sb.append(" : ");
      sb.append("!!!!! NULL PARTITIONS !!!!!");
    }
    return sb.toString();
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
}
