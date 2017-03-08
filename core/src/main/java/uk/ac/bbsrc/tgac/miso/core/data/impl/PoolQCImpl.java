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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;


/**
 * Concrete implementation of a PoolQC
 * 
 * @author Rob Davey
 * @since 0.1.9
 */
@Entity
@Table(name = "PoolQC")
public class PoolQCImpl extends AbstractQC implements PoolQC, Serializable {
  private static final long serialVersionUID = 1L;
  private static final Logger log = LoggerFactory.getLogger(PoolQCImpl.class);
  public static final String UNITS = "nM";

  @ManyToOne(targetEntity = PoolImpl.class)
  @JoinColumn(name = "pool_poolId")
  @JsonBackReference
  private Pool pool;

  private Double results;

  /**
   * Construct a new PoolQC
   */
  public PoolQCImpl() {
  }

  /**
   * Construct a new PoolQC from a parent Pool, checking that the given User can read that Pool
   * 
   * @param pool
   *          of type Pool
   * @param user
   *          of type User
   */
  public PoolQCImpl(Pool pool, User user) {
    if (pool.userCanRead(user)) {
      try {
        setPool(pool);
      } catch (MalformedPoolException e) {
        log.error("constructor", e);
      }
    }
  }

  /**
   * Equivalency is based on getQcId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof PoolQCImpl)) return false;
    PoolQC them = (PoolQC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == AbstractQC.UNSAVED_ID || them.getId() == AbstractQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType()) && this.getResults().equals(them.getResults());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public Pool getPool() {
    return pool;
  }

  @Override
  public Double getResults() {
    return results;
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = getQcCreator().hashCode();
      hashcode = 37 * hashcode + getQcDate().hashCode();
      hashcode = 37 * hashcode + getQcType().hashCode();
      hashcode = 37 * hashcode + getResults().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(QC t) {
    PoolQC s = (PoolQC) t;
    if (getId() != 0L && t.getId() != 0L) {
      if (getId() < s.getId()) return -1;
      if (getId() > s.getId()) return 1;
    } else if (getQcType() != null && s.getQcType() != null && getResults() != null && s.getResults() != null && getQcDate() != null
        && s.getQcDate() != null) {
      int type = getQcType().compareTo(s.getQcType());
      if (type != 0) return type;
      int results = getResults().compareTo(s.getResults());
      if (results != 0) return results;
      int creator = getQcDate().compareTo(s.getQcDate());
      if (creator != 0) return creator;
    }
    return 0;
  }

  @Override
  public void setPool(Pool pool) throws MalformedPoolException {
    this.pool = pool;
  }
  @Override
  public void setResults(Double results) {
    this.results = results;
  }

  @Override
  public boolean userCanRead(User user) {
    return true;
  }

  @Override
  public boolean userCanWrite(User user) {
    return true;
  }
}
