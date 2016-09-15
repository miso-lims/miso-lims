/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

package uk.ac.bbsrc.tgac.miso.core.data.impl.kit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A KitDescriptor handles information about a consumable element that is generally typed by a name, manufacturer and part number. Kits use
 * KitDescriptors, coupled with a lot number, to represent a real-world manifestation of a consumable kit.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "KitDescriptor")
public class KitDescriptor implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Field UNSAVED_ID */
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long kitDescriptorId = KitDescriptor.UNSAVED_ID;
  private String name = "";
  private Double version = 0.0D;
  private String manufacturer = "";
  private String partNumber = "";
  private Integer stockLevel = 0;
  private String description = "";
  @Transient
  private final Collection<ChangeLog> changelog = new ArrayList<>();
  @Transient
  private User lastModifier;

  @Enumerated(EnumType.STRING)
  private KitType kitType;

  @Enumerated(EnumType.STRING)
  private PlatformType platformType;

  /**
   * Returns the kitDescriptorId of this KitDescriptor object.
   * 
   * @return Long kitDescriptorId.
   */
  public Long getId() {
    return kitDescriptorId;
  }

  /**
   * Sets the kitDescriptorId of this KitDescriptor object.
   * 
   * @param kitDescriptorId
   *          kitDescriptorId.
   */
  public void setId(Long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  /**
   * Returns the name of this KitDescriptor object.
   * 
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this KitDescriptor object.
   * 
   * @param name
   *          name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the version of this KitDescriptor object.
   *
   * @return Double version.
   */
  public Double getVersion() {
    return version;
  }

  /**
   * Sets the version of this KitDescriptor object.
   * 
   * @param version
   *          version.
   */
  public void setVersion(Double version) {
    this.version = version;
  }

  /**
   * Returns the manufacturer of this KitDescriptor object.
   * 
   * @return String manufacturer.
   */
  public String getManufacturer() {
    return manufacturer;
  }

  /**
   * Sets the manufacturer of this KitDescriptor object.
   * 
   * @param manufacturer
   *          manufacturer.
   */
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  /**
   * Returns the partNumber of this KitDescriptor object.
   * 
   * @return String partNumber.
   */
  public String getPartNumber() {
    return partNumber;
  }

  /**
   * Sets the partNumber of this KitDescriptor object.
   * 
   * @param partNumber
   *          partNumber.
   */
  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }

  /**
   * Returns the stockLevel of this KitDescriptor object.
   * 
   * @return Integer stockLevel.
   */
  public Integer getStockLevel() {
    return stockLevel;
  }

  /**
   * Sets the stockLevel of this KitDescriptor object.
   * 
   * @param stockLevel
   *          stockLevel.
   */
  public void setStockLevel(Integer stockLevel) {
    this.stockLevel = stockLevel;
  }

  /**
   * Sets the description of this KitDescriptor object.
   *
   * @param description
   *          description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the description of this KitDescriptor object.
   *
   * @return String description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the kitType of this KitDescriptor object.
   * 
   * @return KitType kitType.
   */
  public KitType getKitType() {
    return kitType;
  }

  /**
   * Sets the kitType of this KitDescriptor object.
   * 
   * @param kitType
   *          kitType.
   * 
   */
  public void setKitType(KitType kitType) {
    this.kitType = kitType;
  }

  /**
   * Returns the platformType of this KitDescriptor object.
   * 
   * @return PlatformType platformType.
   */
  public PlatformType getPlatformType() {
    return platformType;
  }

  /**
   * Sets the platformType of this KitDescriptor object.
   * 
   * @param platformType
   *          platformType.
   */
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public Collection<ChangeLog> getChangeLog() {
    return changelog;
  }

  public User getLastModifier() {
    return lastModifier;
   }

  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  /**
   * Method toString ...
   * 
   * @return String
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getName());
    sb.append(" : ");
    sb.append(getPartNumber());
    sb.append(" : ");
    sb.append(getKitType());
    sb.append(" : ");
    sb.append(getPlatformType());
    sb.append(" : ");
    sb.append(getStockLevel());
    sb.append(" : ");
    sb.append(getDescription());
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(21, 51)
        .append(description)
        .append(kitType)
        .append(manufacturer)
        .append(name)
        .append(partNumber)
        .append(platformType)
        .append(version)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    KitDescriptor other = (KitDescriptor) obj;
    return new EqualsBuilder()
        .append(description, other.description)
        .append(kitType, other.kitType)
        .append(manufacturer, other.manufacturer)
        .append(name, other.name)
        .append(partNumber, other.partNumber)
        .append(platformType, other.platformType)
        .append(version, other.version)
        .isEquals();
  }
}
