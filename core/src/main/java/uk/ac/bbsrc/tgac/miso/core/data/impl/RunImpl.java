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
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AutoPopulatingList;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.1.0
 */
public class RunImpl extends AbstractRun implements Serializable {
  protected static final Logger log = LoggerFactory.getLogger(RunImpl.class);

  @OneToMany(cascade = CascadeType.ALL)
  private List<SequencerPartitionContainer<SequencerPoolPartition>> containers = new AutoPopulatingList<SequencerPartitionContainer<SequencerPoolPartition>>(
      SequencerPartitionContainerImpl.class);

  /**
   * Construct a new Run with a default empty SecurityProfile
   */
  public RunImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Run with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public RunImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  public RunImpl(Experiment experiment, User user) {
    if (experiment.userCanRead(user)) {
      setSecurityProfile(experiment.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  @Override
  public List<SequencerPartitionContainer<SequencerPoolPartition>> getSequencerPartitionContainers() {
    if (this.containers != null) Collections.sort(this.containers);
    return containers;
  }

  @Override
  public void setSequencerPartitionContainers(List<SequencerPartitionContainer<SequencerPoolPartition>> containers) {
    this.containers = containers;
  }

  @Override
  public void addSequencerPartitionContainer(SequencerPartitionContainer<SequencerPoolPartition> f) {
    f.setSecurityProfile(getSecurityProfile());
    if (f.getId() == 0L && f.getIdentificationBarcode() == null) {
      // can't validate it so add it anyway. this will only usually be the case for new run population.
      this.containers.add(f);
    } else {
      if (!this.containers.contains(f)) {
        this.containers.add(f);
      }
    }
  }

  @Override
  public void buildSubmission() {
  }

  /**
   * Method buildReport ...
   */
  @Override
  public void buildReport() {
  }
}
