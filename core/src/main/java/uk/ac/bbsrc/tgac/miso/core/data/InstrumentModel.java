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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * A Platform describes metadata about potentially any hardware item, but is usully linked to a sequencer implementation.
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "InstrumentModel")
public class InstrumentModel implements Comparable<InstrumentModel>, Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final Long UNSAVED_ID = 0L;

  @Enumerated(EnumType.STRING)
  @Column(name = "platform")
  private PlatformType platformType;

  @Column(nullable = true)
  private String description;

  @Enumerated(EnumType.STRING)
  private InstrumentType instrumentType;

  @Column(nullable = false)
  private String alias;

  private int numContainers;

  @Enumerated(EnumType.STRING)
  private InstrumentDataManglingPolicy dataManglingPolicy;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long instrumentModelId = InstrumentModel.UNSAVED_ID;

  @OneToMany(mappedBy = "instrumentModel")
  private Set<InstrumentPosition> positions;

  @ManyToMany
  @JoinTable(name = "SequencingContainerModel_InstrumentModel", joinColumns = {
      @JoinColumn(name = "instrumentModelId", nullable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "sequencingContainerModelId", nullable = false) })
  private Set<SequencingContainerModel> containerModels;

  @Override
  public long getId() {
    return instrumentModelId;
  }

  @Override
  public void setId(long instrumentModelId) {
    this.instrumentModelId = instrumentModelId;
  }

  public PlatformType getPlatformType() {
    return platformType;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getPlatformAndAlias() {
    return platformType.getKey() + " - " + alias;
  }

  public int getNumContainers() {
    return numContainers;
  }

  public void setNumContainers(int numContainers) {
    this.numContainers = numContainers;
  }

  public InstrumentType getInstrumentType() {
    return instrumentType;
  }

  public void setInstrumentType(InstrumentType instrumentType) {
    this.instrumentType = instrumentType;
  }

  public Set<InstrumentPosition> getPositions() {
    if (positions == null) {
      positions = new HashSet<>();
    }
    return positions;
  }

  public Set<SequencingContainerModel> getContainerModels() {
    if (containerModels == null) {
      containerModels = new HashSet<>();
    }
    return containerModels;
  }

  /**
   * Equivalency is based on id if set, otherwise on name, description and creation date.
   */

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof InstrumentModel)) return false;
    InstrumentModel them = (InstrumentModel) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == InstrumentModel.UNSAVED_ID || them.getId() == InstrumentModel.UNSAVED_ID) {
      return getPlatformType().equals(them.getPlatformType()) && getDescription().equals(them.getDescription());
    } else {
      return getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != InstrumentModel.UNSAVED_ID) {
      return Long.valueOf(getId()).intValue();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getPlatformType() != null) hashcode = PRIME * hashcode + getPlatformType().hashCode();
      if (getDescription() != null) hashcode = PRIME * hashcode + getDescription().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(InstrumentModel t) {
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  public InstrumentDataManglingPolicy getDataManglingPolicy() {
    return dataManglingPolicy;
  }

  public void setDataManglingPolicy(InstrumentDataManglingPolicy dataManglingPolicy) {
    this.dataManglingPolicy = dataManglingPolicy;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Instrument Model";
  }

  @Override
  public String getDeleteDescription() {
    return getPlatformAndAlias();
  }

}
