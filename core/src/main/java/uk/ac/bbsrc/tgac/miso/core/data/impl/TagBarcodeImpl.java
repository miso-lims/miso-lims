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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 10-May-2011
 * @since 0.0.3
 */
public class TagBarcodeImpl implements TagBarcode {

  public static final Long UNSAVED_ID = null;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long tagBarcodeId = UNSAVED_ID;

  private static final long serialVersionUID = 1L;
  private String name;
  private String sequence;
  private PlatformType platformType;
  private String strategyName;

  @Override
  public Long getTagBarcodeId() {
    return tagBarcodeId;
  }

  @Override
  public void setTagBarcodeId(Long tagBarcodeId) {
    this.tagBarcodeId = tagBarcodeId;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getSequence() {
    return sequence;
  }

  @Override
  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  @Override
  public PlatformType getPlatformType() {
    return platformType;
  }

  @Override
  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  @Override
  public String getStrategyName() {
    return strategyName;
  }

  @Override
  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name,
   * description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof TagBarcode))
      return false;
    TagBarcode them = (TagBarcode) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getTagBarcodeId() == UNSAVED_ID
        || them.getTagBarcodeId() == UNSAVED_ID) {
      return getName().equals(them.getName()) &&
             getSequence().equals(them.getSequence()) &&
             getPlatformType().equals(them.getPlatformType()) &&
             getStrategyName().equals(them.getStrategyName());
    }
    else {
      return getTagBarcodeId().longValue() == them.getTagBarcodeId().longValue();
    }
  }

  @Override
  public int hashCode() {
    if (getTagBarcodeId() != UNSAVED_ID) {
      return getTagBarcodeId().intValue();
    }
    else {
      int hashcode = -1;
      if (getName() != null) hashcode = 37 * hashcode + getName().hashCode();
      if (getSequence() != null) hashcode = 37 * hashcode + getSequence().hashCode();
      if (getPlatformType() != null) hashcode = 37 * hashcode + getPlatformType().hashCode();
      if (getStrategyName() != null) hashcode = 37 * hashcode + getStrategyName().hashCode();
      return hashcode;
    }
  }

  public int compareTo(Object o) {
    TagBarcode t = (TagBarcode)o;
    if (getTagBarcodeId() < t.getTagBarcodeId()) return -1;
    if (getTagBarcodeId() > t.getTagBarcodeId()) return 1;
    return 0;
  }
}
