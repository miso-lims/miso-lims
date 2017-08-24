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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * A QC that is specifically carried out on a given {@link uk.ac.bbsrc.tgac.miso.core.data.Run}
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Entity
@Table(name = "RunQC")
public class RunQC implements Serializable, Deletable, Comparable<RunQC> {

  private static final long serialVersionUID = 1L;
  public static final long UNSAVED_ID = 0L;
  private boolean doNotProcess;

  private String information;

  @OneToMany(targetEntity = PartitionImpl.class)
  @JoinTable(name = "RunQC_Partition", joinColumns = {
      @JoinColumn(name = "runQc_runQcId", nullable = false, updatable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "partition_partitionId", nullable = false)
      })
  private List<Partition> partitionSelections;

  private String qcCreator;

  @Temporal(TemporalType.DATE)
  private Date qcDate = new Date();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long qcId = RunQC.UNSAVED_ID;

  @ManyToOne(targetEntity = QcType.class)
  @JoinColumn(name = "qcMethod")
  private QcType qcType;

  @ManyToOne(targetEntity = Run.class)
  @JoinColumn(name = "run_runId")
  private Run run;

  /**
   * Construct a new RunQCImpl
   */
  public RunQC() {

  }

  /**
   * Construct a new RunQC from a parent Run, checking that the given User can read that Run
   * 
   * @param run
   *          of type Run
   * @param user
   *          of type User
   */
  public RunQC(Run run, User user) {
    if (run.userCanRead(user)) {
      setRun(run);
    }
  }

  @Override
  public int compareTo(RunQC t) {
    if (getId() != RunQC.UNSAVED_ID && t.getId() != RunQC.UNSAVED_ID) {
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
    } else if (getQcType() != null && t.getQcType() != null && getQcDate() != null && t.getQcDate() != null) {
      int type = getQcType().compareTo(t.getQcType());
      if (type != 0) return type;
      int creator = getQcDate().compareTo(t.getQcDate());
      if (creator != 0) return creator;
      if (getInformation() != null && t.getInformation() != null) {
        return getInformation().compareTo(t.getInformation());
      }
    }
    return 0;
  }

  /**
   * Equivalency is based on getQcId() if set, otherwise on name
   */

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof RunQC)) return false;
    RunQC them = (RunQC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == RunQC.UNSAVED_ID || them.getId() == RunQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType());
    } else {
      return this.getId() == them.getId();
    }
  }

  public boolean getDoNotProcess() {
    return doNotProcess;
  }

  public long getId() {
    return qcId;
  }

  public String getInformation() {
    return information;
  }

  public List<Partition> getPartitionSelections() {
    return partitionSelections;
  }

  public String getQcCreator() {
    return qcCreator;
  }

  public Date getQcDate() {
    return qcDate;
  }

  public QcType getQcType() {
    return qcType;
  }

  public Run getRun() {
    return run;
  }

  @Override
  public int hashCode() {
    if (getId() != RunQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = -1;
      if (getQcCreator() != null) hashcode = PRIME * hashcode + getQcCreator().hashCode();
      if (getQcDate() != null) hashcode = PRIME * hashcode + getQcDate().hashCode();
      if (getQcType() != null) hashcode = PRIME * hashcode + getQcType().hashCode();
      return hashcode;
    }
  }

  @Override
  public boolean isDeletable() {
    return getId() != RunQC.UNSAVED_ID;
  }

  public void setDoNotProcess(boolean doNotProcess) {
    this.doNotProcess = doNotProcess;
  }

  public void setId(long qcId) {
    this.qcId = qcId;
  }

  public void setInformation(String information) {
    this.information = LimsUtils.findHyperlinks(information);
  }

  public void setPartitionSelections(List<Partition> partitionSelections) {
    this.partitionSelections = partitionSelections;
  }

  public void setQcCreator(String qcCreator) {
    this.qcCreator = qcCreator;
  }

  public void setQcDate(Date qcDate) {
    this.qcDate = qcDate;
  }

  public void setQcType(QcType qcType) {
    this.qcType = qcType;
  }

  public void setRun(Run run) {
    this.run = run;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getId());
    sb.append(" : ");
    sb.append(getQcCreator());
    sb.append(" : ");
    sb.append(getQcDate());
    sb.append(" : ");
    sb.append(getQcType());
    return sb.toString();
  }

  public boolean userCanRead(User user) {
    return true;
  }

  public boolean userCanWrite(User user) {
    return true;
  }
}
