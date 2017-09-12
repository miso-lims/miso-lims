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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;

/**
 * Abstract class to provide basic methods to encapsulate a reference to a physical machine attached to a sequencer
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@MappedSuperclass
public abstract class AbstractSequencerReference implements SequencerReference {

  private static final long serialVersionUID = 1L;

  private static final Logger log = LoggerFactory.getLogger(AbstractSequencerReference.class);

  public static final Long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "referenceId")
  private long id = AbstractSequencerReference.UNSAVED_ID;

  @Column(nullable = false)
  private String name;

  @ManyToOne(targetEntity = PlatformImpl.class)
  @JoinColumn(name = "platformId", nullable = false)
  private Platform platform;

  private String ip;

  private String serialNumber;
  @Temporal(TemporalType.DATE)
  private Date dateCommissioned;
  @Temporal(TemporalType.DATE)
  private Date dateDecommissioned = null;

  @OneToOne(targetEntity = SequencerReferenceImpl.class, optional = true)
  @JoinColumn(name = "upgradedSequencerReferenceId")
  private SequencerReference upgradedSequencerReference;

  @Transient
  private Date lastServicedDate;

  @Override
  public void setId(Long id) {
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
  public void setUpgradedSequencerReference(SequencerReference sequencer) {
    this.upgradedSequencerReference = sequencer;
  }

  @Override
  public SequencerReference getUpgradedSequencerReference() {
    return upgradedSequencerReference;
  }

  @Override
  public String getFQDN() throws UnknownHostException {
    return getIpAddress() == null ? null : InetAddress.getByName(getIpAddress()).getCanonicalHostName();
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractSequencerReference.UNSAVED_ID;
  }

  @Override
  public String toString() {
    return "AbstractSequencerReference [id=" + id
        + ", name=" + name
        + ", platform=" + platform.getId()
        + ", ip=" + ip
        + ", serialNumber=" + serialNumber
        + ", dateCommissioned=" + dateCommissioned
        + ", dateDecommissioned=" + dateDecommissioned
        + ", upgradedSequencerReference=" + (upgradedSequencerReference == null ? null : upgradedSequencerReference.getId()) + "]";
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
  
  @OneToMany(targetEntity = Run.class, mappedBy = "sequencerReference")
  private Set<Run> runs = new HashSet<>();

  @Override
  public Set<Run> getRuns() {
    return runs;
  }

  @Override
  public void setRuns(Set<Run> runs) {
    this.runs = runs;
  }

  @OneToMany(targetEntity = SequencerServiceRecord.class, mappedBy = "sequencerReference")
  private Set<SequencerServiceRecord> serviceRecords = new HashSet<>();

  @Override
  public Set<SequencerServiceRecord> getServiceRecords() {
    return serviceRecords;
  }

  @Override
  public void setServiceRecords(Set<SequencerServiceRecord> serviceRecords) {
    this.serviceRecords = serviceRecords;
  }

}
