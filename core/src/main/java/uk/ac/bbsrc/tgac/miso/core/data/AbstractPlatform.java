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

package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Skeleton implementation of a Platform
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Platform`")
public abstract class AbstractPlatform implements Platform {
  public static final Long UNSAVED_ID = 0L;

  private PlatformType platformType;
  private String description;
  private String instrumentModel;
  private Integer numContainers;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long platformId = AbstractPlatform.UNSAVED_ID;

  @OneToOne(targetEntity = AbstractRun.class, mappedBy = "platform")
  private Run run;

  public Run getRun() {
    return run;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  @Override
  public Long getPlatformId() {
    return platformId;
  }

  @Override
  public void setPlatformId(Long platformId) {
    this.platformId = platformId;
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
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getInstrumentModel() {
    return instrumentModel;
  }

  @Override
  public void setInstrumentModel(String instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  @Override
  public String getNameAndModel() {
    return platformType.getKey() + " - " + instrumentModel;
  }

  @Override
  public Integer getNumContainers() {
    return numContainers;
  }

  @Override
  public void setNumContainers(Integer numContainers) {
    this.numContainers = numContainers;
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Platform)) return false;
    Platform them = (Platform) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getPlatformId() == AbstractPlatform.UNSAVED_ID || them.getPlatformId() == AbstractPlatform.UNSAVED_ID) {
      return getPlatformType().equals(them.getPlatformType()) && getDescription().equals(them.getDescription());
    } else {
      return getPlatformId().longValue() == them.getPlatformId().longValue();
    }
  }

  @Override
  public int hashCode() {
    if (getPlatformId() != AbstractPlatform.UNSAVED_ID) {
      return getPlatformId().intValue();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getPlatformType() != null) hashcode = PRIME * hashcode + getPlatformType().hashCode();
      if (getDescription() != null) hashcode = PRIME * hashcode + getDescription().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Platform t = (Platform) o;
    if (getPlatformId() < t.getPlatformId()) return -1;
    if (getPlatformId() > t.getPlatformId()) return 1;
    return 0;
  }
}
