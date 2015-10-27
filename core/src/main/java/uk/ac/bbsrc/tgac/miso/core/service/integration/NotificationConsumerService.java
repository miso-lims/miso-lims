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

package uk.ac.bbsrc.tgac.miso.core.service.integration;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.NotificationConsumerStrategy;

/**
 * A service interface to describe how to load {@link NotificationConsumerStrategy} services into MISO
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public interface NotificationConsumerService {
  /**
   * Get a {@link NotificationConsumerStrategy} by its name
   * 
   * @param strategyName
   * @return the unique {@link NotificationConsumerStrategy} that has this name, or null if none exists
   */
  NotificationConsumerStrategy getConsumerStrategy(String strategyName);

  /**
   * List all {@link NotificationConsumerStrategy} objects
   * 
   * @return
   */
  Collection<NotificationConsumerStrategy> getConsumerStrategies();
}
