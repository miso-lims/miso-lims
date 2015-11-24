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

package uk.ac.bbsrc.tgac.miso.core.factory.issuetracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory.issuetracker
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/06/12
 * @since 0.1.6
 */
public class IssueTrackerFactory {
  protected static final Logger log = LoggerFactory.getLogger(IssueTrackerFactory.class);
  private Map<String, IssueTrackerManager> managerMap;

  public static IssueTrackerFactory newInstance() {
    return new IssueTrackerFactory();
  }

  public IssueTrackerManager getTrackerManager(String trackerType) {
    for (IssueTrackerManager manager : getTrackerManagers()) {
      if (manager.getType().equals(trackerType)) {
        log.debug("Got manager: " + manager.getType());
        return manager;
      }
    }
    log.warn("No issue manager which supports the '" + trackerType + "' type was available on the classpath");
    return null;
  }

  public Collection<IssueTrackerManager> getTrackerManagers() {
    // lazily load available issue tracker managers
    log.info("Grabbing available issue tracker managers...");
    if (managerMap == null) {
      log.info("...lazily");
      ServiceLoader<IssueTrackerManager> consumerLoader = ServiceLoader.load(IssueTrackerManager.class);
      Iterator<IssueTrackerManager> consumerIterator = consumerLoader.iterator();

      managerMap = new HashMap<String, IssueTrackerManager>();
      while (consumerIterator.hasNext()) {
        IssueTrackerManager p = consumerIterator.next();

        if (!managerMap.containsKey(p.getType())) {
          managerMap.put(p.getType(), p);
        } else {
          if (managerMap.get(p.getType()) != p) {
            String msg = "Multiple different IssueTrackerManager with the same issue tracker type name " + "('" + p.getType()
                + "') are present on the classpath. Issue tracker types names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }
      log.info("Loaded " + managerMap.values().size() + " known issue tracker managers");
    }

    return managerMap.values();
  }
}