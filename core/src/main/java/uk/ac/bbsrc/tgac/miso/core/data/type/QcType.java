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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcCorrespondingField;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
public class QcType implements Comparable<QcType>, Serializable, Aliasable, Deletable {
  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long qcTypeId = QcType.UNSAVED_ID;
  private String name;
  private String description;
  /** Refers to the entity to which this QcType can be applied (e.g. Sample, Pool, Run) */
  @Enumerated(EnumType.STRING)
  private QcTarget qcTarget;
  private String units;
  private Integer precisionAfterDecimal;
  private boolean archived;
  @Enumerated(EnumType.STRING)
  private QcCorrespondingField correspondingField;
  private boolean autoUpdateField;

  @ManyToOne
  @JoinColumn(name = "instrumentModelId")
  private InstrumentModel instrumentModel;

  @ManyToMany
  @JoinTable(name = "QCType_KitDescriptor", joinColumns = { @JoinColumn(name = "qcTypeId") }, inverseJoinColumns = {
      @JoinColumn(name = "kitDescriptorId") })
  private Set<KitDescriptor> kitDescriptors;

  @OneToMany(mappedBy = "qcType", cascade = CascadeType.REMOVE)
  private Set<QcControl> controls;

  /**
   * Returns the qcTypeId of this QcType object.
   * 
   * @return Long qcTypeId.
   */
  @Override
  public long getId() {
    return qcTypeId;
  }

  /**
   * Sets the qcTypeId of this QcType object.
   * 
   * @param qcTypeId
   *          qcTypeId.
   */
  @Override
  public void setId(long qcTypeId) {
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
  public QcTarget getQcTarget() {
    return qcTarget;
  }

  /**
   * Sets the qcTarget of this QcType object.
   * 
   * @param qcTarget
   *          qcTarget.
   */
  public void setQcTarget(QcTarget qcTarget) {
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

  public QcCorrespondingField getCorrespondingField() {
    return correspondingField;
  }

  public void setCorrespondingField(QcCorrespondingField correspondingField) {
    this.correspondingField = correspondingField;
  }

  public boolean isAutoUpdateField() {
    return autoUpdateField;
  }

  public void setAutoUpdateField(boolean autoUpdateField) {
    this.autoUpdateField = autoUpdateField;
  }

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public Set<KitDescriptor> getKitDescriptors() {
    if (kitDescriptors == null) {
      kitDescriptors = new HashSet<>();
    }
    return kitDescriptors;
  }

  public Set<QcControl> getControls() {
    if (controls == null) {
      controls = new HashSet<>();
    }
    return controls;
  }

  @Override
  public boolean equals(Object obj) {
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (isSaved()) {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof QcType)) return false;
      QcType them = (QcType) obj;
      if (them.isSaved()) {
        return qcTypeId == them.qcTypeId;
      }
    }

    return LimsUtils.equals(this, obj,
        QcType::getName,
        QcType::getQcTarget,
        QcType::getDescription,
        QcType::getUnits,
        QcType::getPrecisionAfterDecimal,
        QcType::isArchived,
        QcType::getCorrespondingField,
        QcType::isAutoUpdateField,
        QcType::getInstrumentModel,
        QcType::getKitDescriptors,
        QcType::getControls);
  }

  @Override
  public int hashCode() {
    if (isSaved()) {
      return (int) getId();
    } else {
      return Objects.hash(name,
          qcTarget,
          description,
          units,
          precisionAfterDecimal,
          archived,
          correspondingField,
          autoUpdateField,
          instrumentModel,
          kitDescriptors,
          controls);
    }
  }

  @Override
  public int compareTo(QcType t) {
    if (getName() != null && t.getName() != null) {
      int name = getName().compareTo(t.getName());
      if (name != 0) return name;
    }

    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
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

  @Override
  public String getAlias() {
    return getName();
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return getQcTarget().getLabel() + " QC Type";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }
}
