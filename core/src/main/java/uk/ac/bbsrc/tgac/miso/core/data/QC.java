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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

@MappedSuperclass
public abstract class QC implements Serializable, Comparable<QC> {
  private static final long serialVersionUID = 1L;

  public static final Long UNSAVED_ID = 0L;

  @Column(name = "created", nullable = false, updatable = false)

  @Temporal(TemporalType.TIMESTAMP)
  private Date creationTime;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator")
  private User creator;

  @Temporal(TemporalType.DATE)
  private Date date = new Date();

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long qcId = QC.UNSAVED_ID;

  private BigDecimal results;

  @ManyToOne
  @JoinColumn(name = "type")
  private QcType type;

  private String description;

  @Override
  public int compareTo(QC o) {
    if (type != null && !type.equals(o.getType())) {
      return type.compareTo(o.getType());
    }
    return date.compareTo(o.getDate());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    QC other = (QC) obj;
    if (date == null) {
      if (other.date != null) return false;
    } else if (!date.equals(other.date)) return false;
    if (qcId != other.qcId) return false;
    if (results == null) {
      if (other.results != null) return false;
    } else if (!results.equals(other.results)) return false;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    return true;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public User getCreator() {
    return creator;
  }

  public Date getDate() {
    return date;
  }

  public abstract QualityControlEntity getEntity();

  public long getId() {
    return qcId;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public BigDecimal getResults() {
    return results;
  }

  public QcType getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + (int) (qcId ^ (qcId >>> 32));
    result = prime * result + ((results == null) ? 0 : results.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    return result;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setId(long qcId) {
    this.qcId = qcId;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public void setResults(BigDecimal results) {
    this.results = results;
  }

  public void setType(QcType type) {
    this.type = type;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
