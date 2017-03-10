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

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

/**
 * Skeleton implementation of a QC
 * 
 * @author Rob Davey
 * @date 25-Jul-2011
 * @since 0.0.3
 */
@MappedSuperclass
public abstract class AbstractQC implements QC {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long qcId = AbstractQC.UNSAVED_ID;

  private String qcCreator;

  @ManyToOne(targetEntity = QcType.class)
  @JoinColumn(name = "qcMethod")
  private QcType qcType;

  @Temporal(TemporalType.DATE)
  private Date qcDate = new Date();

  @Override
  public long getId() {
    return qcId;
  }

  @Override
  public void setId(long qcId) {
    this.qcId = qcId;
  }

  @Override
  public String getQcCreator() {
    return qcCreator;
  }

  @Override
  public void setQcCreator(String qcCreator) {
    this.qcCreator = qcCreator;
  }

  @Override
  public QcType getQcType() {
    return qcType;
  }

  @Override
  public void setQcType(QcType qcType) {
    this.qcType = qcType;
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
  public boolean isDeletable() {
    return getId() != AbstractQC.UNSAVED_ID;
  }

  @Override
  public boolean userCanRead(User user) {
    return true;
  }

  @Override
  public boolean userCanWrite(User user) {
    return true;
  }

  /**
   * Equivalency is based on getQcId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof QC)) return false;
    QC them = (QC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == AbstractQC.UNSAVED_ID || them.getId() == AbstractQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getQcCreator() != null) hashcode = PRIME * hashcode + getQcCreator().hashCode();
      if (getQcDate() != null) hashcode = PRIME * hashcode + getQcDate().hashCode();
      if (getQcType() != null) hashcode = PRIME * hashcode + getQcType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(QC t) {
    if (getId() != AbstractQC.UNSAVED_ID && t.getId() != AbstractQC.UNSAVED_ID) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else if (getQcType() != null && t.getQcType() != null && getQcDate() != null && t.getQcDate() != null) {
      int type = getQcType().compareTo(t.getQcType());
      if (type != 0) return type;
      int creator = getQcDate().compareTo(t.getQcDate());
      if (creator != 0) return creator;
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getQcCreator());
    sb.append(" : ");
    sb.append(getQcDate());
    sb.append(" : ");
    sb.append(getQcType());
    return sb.toString();
  }
}
