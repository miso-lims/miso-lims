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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

@MappedSuperclass
public abstract class AbstractInstrument implements Instrument {

  private static final long serialVersionUID = 1L;

  private static final Logger log = LoggerFactory.getLogger(AbstractInstrument.class);

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "instrumentId")
  private long id = AbstractInstrument.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = Platform.class)
  @JoinColumn(name = "platformId", nullable = false)
  private Platform platform;

  private String ip;

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
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  @Override
  public Platform getPlatform() {
    return this.platform;
  }

  @Override
  public void setIpAddress(String ip) {
    if (ip == null) {
      this.ip = null;
    } else {
      try {
        InetAddress inet = InetAddress.getByName(ip);
        this.ip = (inet != null ? inet.getHostAddress() : null);
      } catch (IOException e) {
        log.error("Error getting InetAddress from given ip " + ip, e);
        throw new IllegalArgumentException("Error getting InetAddress from given ip " + ip, e);
      }
    }
  }

  @Override
  public String getIpAddress() {
    return this.ip;
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
  public String getFQDN() throws UnknownHostException {
    return getIpAddress() == null ? null : InetAddress.getByName(getIpAddress()).getCanonicalHostName();
  }

  @Override
  public String toString() {
    return "AbstractInstrument [id=" + id
        + ", name=" + name
        + ", platform=" + platform.getId()
        + ", ip=" + ip
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
  public Set<PlatformPosition> getOutOfServicePositions() {
    if (isOutOfService()) {
      return getPlatform().getPositions();
    }
    return getPlatform().getPositions().stream()
        .filter(pos -> getServiceRecords().stream().anyMatch(sr -> sr.isOutOfService() && sr.getEndTime() == null
            && sr.getStartTime() != null && sr.getStartTime().before(new Date()) && sr.getPosition().getAlias().equals(pos.getAlias())))
        .collect(Collectors.toSet());
  }

  @Override
  public String getOutOfServicePositionsLabel() {
    if (isOutOfService()) {
      return "All positions";
    }
    Set<PlatformPosition> positions = getOutOfServicePositions();
    if (positions.isEmpty()) {
      return null;
    } else {
      return "Position" + (positions.size() == 1 ? "" : "a") + " "
          + positions.stream().map(PlatformPosition::getAlias).collect(Collectors.joining(", "));
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateCommissioned == null) ? 0 : dateCommissioned.hashCode());
    result = prime * result + ((dateDecommissioned == null) ? 0 : dateDecommissioned.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((ip == null) ? 0 : ip.hashCode());
    result = prime * result + ((lastServicedDate == null) ? 0 : lastServicedDate.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((platform == null) ? 0 : platform.hashCode());
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
    if (ip == null) {
      if (other.ip != null) return false;
    } else if (!ip.equals(other.ip)) return false;
    if (lastServicedDate == null) {
      if (other.lastServicedDate != null) return false;
    } else if (!lastServicedDate.equals(other.lastServicedDate)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (platform == null) {
      if (other.platform != null) return false;
    } else if (!platform.equals(other.platform)) return false;
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

}
