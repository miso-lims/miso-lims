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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
public class RunQCImpl extends AbstractRunQC implements Serializable {
  protected static final Logger log = LoggerFactory.getLogger(RunQCImpl.class);
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
  public boolean userCanRead(User user) {
    return true;
  }

  @Override
  public boolean userCanWrite(User user) {
    return true;
  }
}
