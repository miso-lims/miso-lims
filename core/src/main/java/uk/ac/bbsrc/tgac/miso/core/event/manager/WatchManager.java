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

package uk.ac.bbsrc.tgac.miso.core.event.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Watchable;

/**
 * uk.ac.bbsrc.tgac.miso.core.event.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 23/02/12
 * @since 0.1.6
 */
public class WatchManager {
  protected static final Logger log = LoggerFactory.getLogger(WatchManager.class);

  public void watch(Watchable w, User u) {
    log.debug("Adding watcher " + u.getLoginName() + " to " + w.getWatchableIdentifier() + " via WatchManager");
    w.addWatcher(u);
  }

  public void unwatch(Watchable w, User u) {
    log.debug("Removing watcher " + u.getLoginName() + " from " + w.getWatchableIdentifier() + " via WatchManager");
    w.removeWatcher(u);
  }
}
