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

package uk.ac.bbsrc.tgac.miso.core.event.listener;

import uk.ac.bbsrc.tgac.miso.core.event.model.RunEvent;

/**
 * uk.ac.bbsrc.tgac.miso.core.alert
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 23-Sep-2011
 * @since 0.1.2
 */
public interface RunListener extends MisoListener {
  /**
   * Called whenever a new Run is started
   * 
   * @param r
   *          Run that started
   */
  public void runStarted(RunEvent r);

  /**
   * Called whenever a Run is finished
   * 
   * @param r
   *          Run that completed
   */
  public void runCompleted(RunEvent r);

  /**
   * Called whenever a Run fails
   * 
   * @param r
   *          Run that failed
   */
  public void runFailed(RunEvent r);

  /**
   * Called whenever a Run QC is added
   * 
   * @param r
   *          Run that has a QC added
   */
  public void runQcAdded(RunEvent r);
}
