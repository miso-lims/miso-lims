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

import javax.persistence.Entity;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractExperiment;
import uk.ac.bbsrc.tgac.miso.core.data.Study;

/**
 * Concrete implementation of an Experiment
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
public class ExperimentImpl extends AbstractExperiment implements Serializable {
  /**
   * Construct a new Experiment with a default empty SecurityProfile
   */
  public ExperimentImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Experiment with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public ExperimentImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  /**
   * If the given User can read the parent Study, construct a new Experiment with a SecurityProfile inherited from the parent Study. If not,
   * construct a new Experiment with a SecurityProfile owned by the given User
   * 
   * @param study
   *          of type Study
   * @param user
   *          of type User
   */
  public ExperimentImpl(Study study, User user) {
    if (study.userCanRead(user)) {
      setStudy(study);
      setSecurityProfile(study.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  @Override
  public void buildSubmission() {
  }
}
