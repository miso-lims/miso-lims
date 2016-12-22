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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Skeleton implementation of a RunQC
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@MappedSuperclass
public abstract class AbstractRunQC extends AbstractQC implements RunQC {

  @ManyToOne(targetEntity = RunImpl.class)
  @Column(name = "run_runId")
  private Run run;
  private String information;

  @OneToMany(targetEntity = Partition.class)
  @JoinTable(name = "RunQC_Partition", joinColumns = {
      @JoinColumn(name = "runQc_runQcId", nullable = false, updatable = false) }, inverseJoinColumns = {
          @JoinColumn(name = "partition_partitionId", nullable = false)
      })
  private List<Partition> partitionSelections;
  private boolean doNotProcess;

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
}
