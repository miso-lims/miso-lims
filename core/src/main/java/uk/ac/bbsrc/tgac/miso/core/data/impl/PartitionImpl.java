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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03-Aug-2011
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "container" })
@Entity
@Table(name = "_Partition")
public class PartitionImpl implements Partition, Serializable {

  public static final Long UNSAVED_ID = 0L;
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "partitionId")
  private long id = PartitionImpl.UNSAVED_ID;

  @Column(nullable = false)
  private Integer partitionNumber;

  @ManyToOne(targetEntity = SequencerPartitionContainerImpl.class)
  @JoinTable(name = "SequencerPartitionContainer_Partition", joinColumns = {
      @JoinColumn(name = "partitions_partitionId") }, inverseJoinColumns = {
          @JoinColumn(name = "container_containerId") })
  @JsonBackReference
  private SequencerPartitionContainer sequencerPartitionContainer = null;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  @JsonBackReference
  Pool pool = null;

  public PartitionImpl() {
  }

  @Override
  public SequencerPartitionContainer getSequencerPartitionContainer() {
    return this.sequencerPartitionContainer;
  }

  @Override
  public void setSequencerPartitionContainer(SequencerPartitionContainer sequencerPartitionContainer) {
    this.sequencerPartitionContainer = sequencerPartitionContainer;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setPartitionNumber(Integer partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  @Override
  public Integer getPartitionNumber() {
    return partitionNumber;
  }

  @Override
  public boolean isDeletable() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public Pool getPool() {
    return pool;
  }

  @Override
  public void setPool(Pool pool) {
    this.pool = pool;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof PartitionImpl)) return false;
    PartitionImpl them = (PartitionImpl) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == PartitionImpl.UNSAVED_ID || them.getId() == PartitionImpl.UNSAVED_ID) {
      return getPartitionNumber().equals(them.getPartitionNumber())
          && getSequencerPartitionContainer().equals(them.getSequencerPartitionContainer());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != PartitionImpl.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getPartitionNumber() != null) hashcode = PRIME * hashcode + getPartitionNumber().hashCode();
      if (getSequencerPartitionContainer() != null) hashcode = PRIME * hashcode + getSequencerPartitionContainer().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Partition t) {
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else {
      if (getPartitionNumber() < t.getPartitionNumber()) return -1;
      if (getPartitionNumber() > t.getPartitionNumber()) return 1;
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getPartitionNumber());
    if (getPool() != null) {
      sb.append(" : ");
      sb.append(getPool().getId());
    }
    return sb.toString();
  }

}
