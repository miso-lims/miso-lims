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

package uk.ac.bbsrc.tgac.miso.runstats.client.handler;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.runstats.client.RunstatsStore;
import uk.ac.bbsrc.tgac.miso.runstats.client.manager.RunStatsManager;

/**
 * uk.ac.bbsrc.tgac.miso.runstats.client.handler
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/10/11
 * @since 0.1.3
 */
public class RunStatsHandler {
  @Autowired
  private RunstatsStore runStatsStore;

  @Autowired
  private RunStatsManager runStatsManager;

  public void setRunStatsStore(RunstatsStore runStatsStore) {
    this.runStatsStore = runStatsStore;
  }

  public void setRunStatsManager(RunStatsManager runStatsManager) {
    this.runStatsManager = runStatsManager;
  }

  public void persist() {

  }
}
