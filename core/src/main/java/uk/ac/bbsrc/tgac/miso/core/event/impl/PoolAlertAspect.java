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

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;

/**
 * uk.ac.bbsrc.tgac.miso.core.event
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 11/11/11
 * @since 0.1.6
 */
@Aspect
public class PoolAlertAspect {
  protected static final Logger log = LoggerFactory.getLogger(PoolAlertAspect.class);

  private PoolAlertManager poolAlertManager;

  public PoolAlertAspect(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
  }

  public void removeWatcher(Pool pool, User user) {
    try {
      if (user != null) {
        poolAlertManager.removeWatcher(pool, user.getUserId());
      }
    } catch (IOException e) {
      log.error("remove watcher", e);
    }
  }

  public void addWatcher(Pool pool, User user) {
    try {
      if (user != null) {
        poolAlertManager.addWatcher(pool, user.getUserId());
      }
    } catch (IOException e) {
      log.error("add watcher", e);
    }
  }

  public void update(Pool pool) {
    try {
      poolAlertManager.update(pool.getId());
    } catch (IOException e) {
      log.error("update pool alert aspect", e);
    }
  }
}
