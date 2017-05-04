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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Entity
@Table(name = "RunQC")
public class RunQCImpl extends AbstractQC implements RunQC {

  private static final long serialVersionUID = 1L;
  private static final Logger log = LoggerFactory.getLogger(RunQCImpl.class);

  @ManyToOne(targetEntity = RunImpl.class)
  @JoinColumn(name = "run_runId")
  private Run run;
  private String information;

  @OneToMany(targetEntity = PartitionImpl.class)
  @JoinTable(name = "RunQC_Partition", joinColumns = {
      @JoinColumn(name = "runQc_runQcId", nullable = false, updatable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "partition_partitionId", nullable = false)
      })
  private List<Partition> partitionSelections;
  private boolean doNotProcess;

  /**
   * Construct a new RunQCImpl
   */
  public RunQCImpl() {

  }

  /**
   * Construct a new RunQC from a parent Run, checking that the given User can read that Run
   * 
   * @param run
   *          of type Run
   * @param user
   *          of type User
   */
  public RunQCImpl(Run run, User user) {
    if (run.userCanRead(user)) {
      try {
        setRun(run);
      } catch (MalformedRunException e) {
        log.error("constructor", e);
      }
    }
  }

  @Override
  public Run getRun() {
    return run;
  }

  @Override
  public void setRun(Run run) throws MalformedRunException {
    this.run = run;
  }

  @Override
  public String getInformation() {
    return information;
  }

  @Override
  public void setInformation(String information) {
    this.information = LimsUtils.findHyperlinks(information);
  }

  @Override
  public boolean getDoNotProcess() {
    return doNotProcess;
  }

  @Override
  public void setDoNotProcess(boolean doNotProcess) {
    this.doNotProcess = doNotProcess;
  }

  @Override
  public List<Partition> getPartitionSelections() {
    return partitionSelections;
  }

  @Override
  public void setPartitionSelections(List<Partition> partitionSelections) {
    this.partitionSelections = partitionSelections;
  }

  /**
   * Equivalency is based on getRunId() if set, otherwise on name
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof SampleQC)) return false;
    SampleQC them = (SampleQC) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (this.getId() == AbstractQC.UNSAVED_ID || them.getId() == AbstractQC.UNSAVED_ID) {
      return this.getQcCreator().equals(them.getQcCreator()) && this.getQcDate().equals(them.getQcDate())
          && this.getQcType().equals(them.getQcType());
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (getId() != AbstractQC.UNSAVED_ID) {
      return (int) getId();
    } else {
      int hashcode = getQcCreator().hashCode();
      hashcode = 37 * hashcode + getQcDate().hashCode();
      hashcode = 37 * hashcode + getQcType().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(QC t) {
    RunQC s = (RunQC) t;
    int compare = super.compareTo(t);
    if (compare != 0) {
      return compare;
    } else if (getInformation() != null && s.getInformation() != null) {
      compare = getInformation().compareTo(s.getInformation());
      return compare;
    }
    return compare;
  }
}
