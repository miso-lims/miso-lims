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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.NotificationConsumerStrategy;

/**
 * A concrete implementation of a {@link NotificationConsumerService} that loads and exposes {@link NotificationConsumerStrategy} objects at
 * runtime via the SPI {@link ServiceLoader} mechanism.
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class MisoNotificationConsumerService implements NotificationConsumerService {
  protected static final Logger log = LoggerFactory.getLogger(MisoNotificationConsumerService.class);
  private Map<String, NotificationConsumerStrategy> strategyMap;

  @Override
  public NotificationConsumerStrategy getConsumerStrategy(String strategyName) {
    for (NotificationConsumerStrategy strategy : getConsumerStrategies()) {
      if (strategy.getName().equals(strategyName)) {
        log.debug("Got strategy: " + strategy.getName());
        return strategy;
      }
    }
    log.warn("No strategy called '" + strategyName + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<NotificationConsumerStrategy> getConsumerStrategies() {
    // lazily load available strategies
    log.debug("Grabbing strategies...");
    if (strategyMap == null) {
      ServiceLoader<NotificationConsumerStrategy> consumerLoader = ServiceLoader.load(NotificationConsumerStrategy.class);
      Iterator<NotificationConsumerStrategy> consumerIterator = consumerLoader.iterator();

      strategyMap = new HashMap<String, NotificationConsumerStrategy>();
      while (consumerIterator.hasNext()) {
        NotificationConsumerStrategy p = consumerIterator.next();

        if (!strategyMap.containsKey(p.getName())) {
          strategyMap.put(p.getName(), p);
        } else {
          if (strategyMap.get(p.getName()) != p) {
            String msg = "Multiple different NotificationConsumerStrategies with the same strategy name " + "('" + p.getName()
                + "') are present on the classpath. Strategy names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }
      log.info("Loaded " + strategyMap.values().size() + " known strategies");
    }

    return strategyMap.values();
  }
}