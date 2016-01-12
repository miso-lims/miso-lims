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

package uk.ac.bbsrc.tgac.miso.core.event.impl;

import java.io.IOException;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;

/**
 * uk.ac.bbsrc.tgac.miso.core.event
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.3
 */
@Aspect
public class RunAlertAspect {
  protected static final Logger log = LoggerFactory.getLogger(RunAlertAspect.class);
  private RunAlertManager runAlertManager;

  public RunAlertAspect(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
  }

  public void removeWatcher(Run run, User user) {
    try {
      if (user != null) {
        runAlertManager.removeWatcher(run, user.getUserId());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addWatcher(Run run, User user) {
    try {
      if (user != null) {
        runAlertManager.addWatcher(run, user.getUserId());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void update(Long runId) {
    try {
      runAlertManager.update(runId);
    } catch (IOException e) {
      log.error("update run alert aspect", e);
    }
  }
}
