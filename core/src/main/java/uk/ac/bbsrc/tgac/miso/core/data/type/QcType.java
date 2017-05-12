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

package uk.ac.bbsrc.tgac.miso.core.data.type;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Provides model access to the underlying MISO QcType lookup table. These types should hold manufacturer platform information for QC
 * analysis.
 * <p/>
 * See:
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "QCType")
public class QcType implements Comparable<QcType> {

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long qcTypeId = QcType.UNSAVED_ID;
  private String name;
  private String description;
  /** Refers to the entity to which this QcType can be applied (e.g. Sample, Pool, Run) */
  private String qcTarget;
  private String units;
  private Integer precisionAfterDecimal;
  private boolean archived;

  /**
   * Returns the qcTypeId of this QcType object.
   * 
   * @return Long qcTypeId.
   */
  public Long getQcTypeId() {
    return qcTypeId;
  }

  /**
   * Sets the qcTypeId of this QcType object.
   * 
   * @param qcTypeId
   *          qcTypeId.
   */
  public void setQcTypeId(Long qcTypeId) {
    this.qcTypeId = qcTypeId;
  }

  /**
   * Returns the name of this QcType object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this QcType object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the description of this LibraryType object.
   * 
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of this LibraryType object.
   * 
   * @param description
   *          description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the qcTarget of this QcType object.
   * 
   * @return String qcTarget.
   */
  public String getQcTarget() {
    return qcTarget;
  }

  /**
   * Sets the qcTarget of this QcType object.
   * 
   * @param qcTarget
   *          qcTarget.
   */
  public void setQcTarget(String qcTarget) {
    this.qcTarget = qcTarget;
  }

  /**
   * Returns the units of this QcType object.
   * 
   * @return String units.
   */
  public String getUnits() {
    return units;
  }

  /**
   * Sets the units of this QcType object.
   * 
   * @param units
   *          units.
   */
  public void setUnits(String units) {
    this.units = units;
  }

  /**
   * Represents the number of digits after a decimal that this QcType is capable of offering.
   * A precision of zero represents an Integer.
   * A precision of -1 represents a boolean. A QC performed with a boolean precision QcType should have a result of either 1 or 0.
   * 
   * @return Integer precisionAfterDecimal
   */
  public Integer getPrecisionAfterDecimal() {
    return precisionAfterDecimal;
  }

  /**
   * Sets the precision of this QcType object.
   * 
   * @param precisionAfterDecimal
   */
  public void setPrecisionAfterDecimal(Integer precisionAfterDecimal) {
    this.precisionAfterDecimal = precisionAfterDecimal;
  }

  /**
   * Returns whether this QcType is archived
   * 
   * @return boolean archived
   */
  public boolean isArchived() {
    return archived;
  }

  /**
   * Sets the archived value for this QcType object.
   * 
   * @param archived
   */
  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof QcType)) return false;
    QcType them = (QcType) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    return getName().equals(them.getName()) && getQcTarget().equals(them.getQcTarget());
  }

  @Override
  public int hashCode() {
    if (getQcTypeId() != UNSAVED_ID) {
      return getQcTypeId().intValue();
    } else {
      int hashcode = -1;
      if (getName() != null) hashcode = 37 * hashcode + getName().hashCode();
      if (getDescription() != null) hashcode = 37 * hashcode + getDescription().hashCode();
      if (getQcTarget() != null) hashcode = 37 * hashcode + getQcTarget().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(QcType t) {
    if (getName() != null && t.getName() != null) {
      int name = getName().compareTo(t.getName());
      if (name != 0) return name;
    }

    if (getQcTypeId() < t.getQcTypeId()) return -1;
    if (getQcTypeId() > t.getQcTypeId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getQcTypeId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getQcTarget());
    sb.append(" : ");
    sb.append(getUnits());
    return sb.toString();
  }
}
