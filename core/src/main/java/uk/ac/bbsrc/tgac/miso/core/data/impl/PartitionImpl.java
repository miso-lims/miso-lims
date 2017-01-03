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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03-Aug-2011
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile", "container" })
@Entity
@Table(name = "`_Partition`")
public class PartitionImpl extends AbstractPartition implements SequencerPoolPartition, Serializable {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  Pool pool = null;

  public PartitionImpl() {
  }

  @Override
  public void buildSubmission() {
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
