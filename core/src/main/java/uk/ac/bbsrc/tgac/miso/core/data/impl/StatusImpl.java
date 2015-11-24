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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Status`")
public class StatusImpl implements Status, Serializable {
  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long statusId = StatusImpl.UNSAVED_ID;

  @Enumerated(EnumType.STRING)
  private HealthType health;
  private Date startDate;
  private Date completionDate;
  private String instrumentName;
  private String xml;
  private String runName;
  private Date lastUpdated;

  /**
   * Construct a new Status with HealthType.Unknown
   */
  public StatusImpl() {
    setHealth(HealthType.Unknown);
    setStartDate(new Date());
  }

  /**
   * Construct a new Status with HealthType.Unknown and a given run name
   * 
   * @param runName
   *          of type String
   */
  public StatusImpl(String runName) {
    setRunName(runName);
    setHealth(HealthType.Unknown);
    setStartDate(new Date());
  }

  @Override
  public Long getStatusId() {
    return statusId;
  }

  @Override
  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  @Override
  public String getXml() {
    return xml;
  }

  @Override
  public void setXml(String xml) {
    this.xml = xml;
  }

  @Override
  public HealthType getHealth() {
    return health;
  }

  @Override
  public Date getStartDate() {
    return startDate;
  }

  @Override
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @Override
  public Date getCompletionDate() {
    return completionDate;
  }

  @Override
  public void setCompletionDate(Date completionDate) {
    this.completionDate = completionDate;
  }

  @Override
  public void setHealth(HealthType health) {
    this.health = health;
  }

  @Override
  public String getInstrumentName() {
    return instrumentName;
  }

  @Override
  public void setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
  }

  @Override
  public String getRunName() {
    return runName;
  }

  @Override
  public void setRunName(String runName) {
    this.runName = runName;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean userCanRead(User user) {
    return true;
  }

  @Override
  public boolean userCanWrite(User user) {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Status)) return false;
    Status them = (Status) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getStatusId().equals(StatusImpl.UNSAVED_ID) || them.getStatusId().equals(StatusImpl.UNSAVED_ID)) {
      return getRunName().equals(them.getRunName());
    } else {
      return getStatusId().equals(them.getStatusId()) && getHealth().equals(them.getHealth()) && getStartDate().equals(them.getStartDate())
          && getCompletionDate().equals(them.getCompletionDate());
    }
  }

  @Override
  public int hashCode() {
    if (getStatusId() != null && !getStatusId().equals(StatusImpl.UNSAVED_ID)) {
      return getStatusId().hashCode();
    } else {
      final int PRIME = 31;
      int result = 1;
      result = (result * PRIME) + (getRunName() == null ? 0 : getRunName().hashCode());
      return result;
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getStatusId());
    sb.append(" : ");
    sb.append(getStartDate());
    sb.append(" : ");
    sb.append(getHealth());
    sb.append(" : ");
    sb.append(getInstrumentName());
    return sb.toString();
  }
}
