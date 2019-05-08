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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

@MappedSuperclass
public abstract class AbstractInstrument implements Instrument {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "instrumentId")
  private long id = AbstractInstrument.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = InstrumentModel.class)
  @JoinColumn(name = "instrumentModelId", nullable = false)
  private InstrumentModel instrumentModel;

  private String serialNumber;
  @Temporal(TemporalType.DATE)
  private Date dateCommissioned;
  @Temporal(TemporalType.DATE)
  private Date dateDecommissioned = null;

  @OneToOne(targetEntity = InstrumentImpl.class, optional = true)
  @JoinColumn(name = "upgradedInstrumentId")
  private Instrument upgradedInstrument;

  @Transient
  private Date lastServicedDate;

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  @Override
  public InstrumentModel getInstrumentModel() {
    return this.instrumentModel;
  }

  @Override
  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  @Override
  public String getSerialNumber() {
    return serialNumber;
  }

  @Override
  public void setDateCommissioned(Date date) {
    this.dateCommissioned = date;
  }

  @Override
  public Date getDateCommissioned() {
    return dateCommissioned;
  }

  @Override
  public void setDateDecommissioned(Date date) {
    this.dateDecommissioned = date;
  }

  @Override
  public Date getDateDecommissioned() {
    return dateDecommissioned;
  }

  @Override
  public void setUpgradedInstrument(Instrument instrument) {
    this.upgradedInstrument = instrument;
  }

  @Override
  public Instrument getUpgradedInstrument() {
    return upgradedInstrument;
  }

  @Override
  public String toString() {
    return "AbstractInstrument [id=" + id
        + ", name=" + name
        + ", instrumentModel=" + instrumentModel.getId()
        + ", serialNumber=" + serialNumber
        + ", dateCommissioned=" + dateCommissioned
        + ", dateDecommissioned=" + dateDecommissioned
        + ", upgradedInstrument=" + (upgradedInstrument == null ? null : upgradedInstrument.getId()) + "]";
  }

  @Override
  public boolean isActive() {
    return dateDecommissioned == null;
  }

  @Override
  public void setLastServicedDate(Date date) {
    this.lastServicedDate = date;
  }

  @Override
  public Date getLastServicedDate() {
    return lastServicedDate;
  }

  @OneToMany(targetEntity = Run.class, mappedBy = "sequencer")
  private Set<Run> runs = new HashSet<>();

  @Override
  public Set<Run> getRuns() {
    return runs;
  }

  @Override
  public void setRuns(Set<Run> runs) {
    this.runs = runs;
  }

  @OneToMany(targetEntity = ServiceRecord.class, mappedBy = "instrument")
  private Set<ServiceRecord> serviceRecords = new HashSet<>();

  @Override
  public Set<ServiceRecord> getServiceRecords() {
    return serviceRecords;
  }

  @Override
  public void setServiceRecords(Set<ServiceRecord> serviceRecords) {
    this.serviceRecords = serviceRecords;
  }

  @Override
  public boolean isOutOfService() {
    if (getServiceRecords() == null) {
      return false;
    }
    return getServiceRecords().stream().anyMatch(sr -> sr.isOutOfService() && sr.getEndTime() == null && sr.getStartTime() != null
        && sr.getStartTime().before(new Date()) && sr.getPosition() == null);
  }

  @Override
  public Set<InstrumentPosition> getOutOfServicePositions() {
    if (isOutOfService()) {
      return getInstrumentModel().getPositions();
    }
    return getInstrumentModel().getPositions().stream()
        .filter(pos -> getServiceRecords().stream().anyMatch(sr -> sr.isOutOfService() && sr.getEndTime() == null
            && sr.getStartTime() != null && sr.getStartTime().before(new Date()) && sr.getPosition().getAlias().equals(pos.getAlias())))
        .collect(Collectors.toSet());
  }

  @Override
  public String getOutOfServicePositionsLabel() {
    if (isOutOfService()) {
      return "All positions";
    }
    Set<InstrumentPosition> positions = getOutOfServicePositions();
    if (positions.isEmpty()) {
      return null;
    } else {
      return "Position" + (positions.size() == 1 ? "" : "a") + " "
          + positions.stream().map(InstrumentPosition::getAlias).collect(Collectors.joining(", "));
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateCommissioned == null) ? 0 : dateCommissioned.hashCode());
    result = prime * result + ((dateDecommissioned == null) ? 0 : dateDecommissioned.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((lastServicedDate == null) ? 0 : lastServicedDate.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((instrumentModel == null) ? 0 : instrumentModel.hashCode());
    result = prime * result + ((runs == null) ? 0 : runs.hashCode());
    result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
    result = prime * result + ((serviceRecords == null) ? 0 : serviceRecords.hashCode());
    result = prime * result + ((upgradedInstrument == null) ? 0 : upgradedInstrument.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractInstrument other = (AbstractInstrument) obj;
    if (dateCommissioned == null) {
      if (other.dateCommissioned != null) return false;
    } else if (!dateCommissioned.equals(other.dateCommissioned)) return false;
    if (dateDecommissioned == null) {
      if (other.dateDecommissioned != null) return false;
    } else if (!dateDecommissioned.equals(other.dateDecommissioned)) return false;
    if (id != other.id) return false;
    if (lastServicedDate == null) {
      if (other.lastServicedDate != null) return false;
    } else if (!lastServicedDate.equals(other.lastServicedDate)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (instrumentModel == null) {
      if (other.instrumentModel != null) return false;
    } else if (!instrumentModel.equals(other.instrumentModel)) return false;
    if (runs == null) {
      if (other.runs != null) return false;
    } else if (!runs.equals(other.runs)) return false;
    if (serialNumber == null) {
      if (other.serialNumber != null) return false;
    } else if (!serialNumber.equals(other.serialNumber)) return false;
    if (serviceRecords == null) {
      if (other.serviceRecords != null) return false;
    } else if (!serviceRecords.equals(other.serviceRecords)) return false;
    if (upgradedInstrument == null) {
      if (other.upgradedInstrument != null) return false;
    } else if (!upgradedInstrument.equals(other.upgradedInstrument)) return false;
    return true;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

}
