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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;

/**
 * A Store interface that describes how to retrieve and persist {@link Alert} objects
 * 
 * @author Rob Davey
 * @date 22-Sep-2011
 * @since 0.1.2
 */
public interface AlertStore extends Store<Alert>, Remover<Alert> {
  /**
   * List all Alerts that have been raised to a specific {@link com.eaglegenomics.simlims.core.User}, by a given user ID
   * 
   * @param userId
   *          of type long
   * @return Collection<Alert>
   * @throws IOException
   *           when
   */
  Collection<Alert> listByUserId(long userId) throws IOException;

  /**
   * List the most recent n Alerts that have been raised to a specific {@link com.eaglegenomics.simlims.core.User}, by a given user ID,
   * limited by the limit parameter
   * 
   * @param userId
   *          of type long
   * @param limit
   *          of type long
   * @return Collection<Alert>
   * @throws IOException
   *           when
   */
  Collection<Alert> listByUserId(long userId, long limit) throws IOException;

  /**
   * List all Alerts that have been raised at a specific {@link AlertLevel}
   * 
   * @param alertLevel
   *          of type AlertLevel
   * @return Collection<Alert>
   * @throws IOException
   *           when
   */
  Collection<Alert> listByAlertLevel(AlertLevel alertLevel) throws IOException;

  /**
   * List all unread Alerts that have been raised to a specific {@link com.eaglegenomics.simlims.core.User}, by a given user ID
   * 
   * @param userId
   *          of type long
   * @return Collection<Alert>
   * @throws IOException
   *           when
   */
  Collection<Alert> listUnreadByUserId(long userId) throws IOException;

  /**
   * List all unread Alerts that have been raised at a specific {@link AlertLevel}
   * 
   * @param alertLevel
   *          of type AlertLevel
   * @return Collection<Alert>
   * @throws IOException
   *           when
   */
  Collection<Alert> listUnreadByAlertLevel(AlertLevel alertLevel) throws IOException;
}
