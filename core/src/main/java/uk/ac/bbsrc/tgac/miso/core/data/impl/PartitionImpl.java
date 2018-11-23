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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
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
@Entity
@Table(name = "_Partition")
public class PartitionImpl implements Partition {

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
  private SequencerPartitionContainer sequencerPartitionContainer = null;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  private Pool pool = null;

  private BigDecimal loadingConcentration;

  @Enumerated(EnumType.STRING)
  private ConcentrationUnit loadingConcentrationUnits;

  public PartitionImpl() {
  }

  public PartitionImpl(SequencerPartitionContainer sequencerPartitionContainer, Integer partitionNumber) {
    super();
    this.sequencerPartitionContainer = sequencerPartitionContainer;
    this.partitionNumber = partitionNumber;
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
  public Pool getPool() {
    return pool;
  }

  @Override
  public void setPool(Pool pool) {
    this.pool = pool;
  }

  @Override
  public BigDecimal getLoadingConcentration() {
    return loadingConcentration;
  }

  @Override
  public void setLoadingConcentration(BigDecimal loadingConcentration) {
    this.loadingConcentration = loadingConcentration;
  }

  @Override
  public ConcentrationUnit getLoadingConcentrationUnits() {
    return loadingConcentrationUnits;
  }

  @Override
  public void setLoadingConcentrationUnits(ConcentrationUnit loadingConcentrationUnits) {
    this.loadingConcentrationUnits = loadingConcentrationUnits;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PartitionImpl other = (PartitionImpl) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == PartitionImpl.UNSAVED_ID || other.getId() == PartitionImpl.UNSAVED_ID) {
      if (loadingConcentration == null) {
        if (other.loadingConcentration != null) return false;
      } else if (!loadingConcentration.equals(other.loadingConcentration)) return false;
      if (loadingConcentrationUnits != other.loadingConcentrationUnits) return false;
      if (partitionNumber == null) {
        if (other.partitionNumber != null) return false;
      } else if (!partitionNumber.equals(other.partitionNumber)) return false;
      if (pool == null) {
        if (other.pool != null) return false;
      } else if (!pool.equals(other.pool)) return false;
      if (sequencerPartitionContainer == null) {
        if (other.sequencerPartitionContainer != null) return false;
      } else if (!sequencerPartitionContainer.equals(other.sequencerPartitionContainer)) return false;
      return true;
    } else {
      return this.getId() == other.getId();
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
      if (getLoadingConcentration() != null) hashcode = PRIME * hashcode + getLoadingConcentration().hashCode();
      if (getLoadingConcentrationUnits() != null) hashcode = PRIME * hashcode + getLoadingConcentrationUnits().hashCode();
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
