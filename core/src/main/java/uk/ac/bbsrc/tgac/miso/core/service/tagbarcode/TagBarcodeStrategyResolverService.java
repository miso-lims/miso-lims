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

package uk.ac.bbsrc.tgac.miso.core.service.tagbarcode;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.RequestManagerAware;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.tagbarcode
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 28/06/12
 * @since 0.1.6
 */
public class TagBarcodeStrategyResolverService {
  protected static final Logger log = LoggerFactory.getLogger(TagBarcodeStrategyResolverService.class);
  private Map<String, TagBarcodeStrategy> strategyMap;

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public TagBarcodeStrategy getTagBarcodeStrategy(String strategyName) {
    for (TagBarcodeStrategy strategy : getTagBarcodeStrategies()) {
      if (strategy.getName().equals(strategyName)) {
        return strategy;
      }
    }
    log.warn("No strategy called '" + strategyName + "' was available on the classpath");
    return null;
  }

  public Collection<TagBarcodeStrategy> getTagBarcodeStrategies() {
    // lazily load available strategies
    if (strategyMap == null) {
      ServiceLoader<TagBarcodeStrategy> consumerLoader = ServiceLoader.load(TagBarcodeStrategy.class);
      Iterator<TagBarcodeStrategy> consumerIterator = consumerLoader.iterator();

      strategyMap = new HashMap<String, TagBarcodeStrategy>();
      while (consumerIterator.hasNext()) {
        TagBarcodeStrategy p = consumerIterator.next();

        if (p instanceof RequestManagerAware) {
          if (requestManager != null && ((RequestManagerAware) p).getRequestManager() == null) {
            ((RequestManagerAware) p).setRequestManager(requestManager);
            p.reload();
          } else {
            log.error("Ooops. No request manager available to register with RequestManagerAware services!");
          }
        }

        if (!strategyMap.containsKey(p.getName())) {
          strategyMap.put(p.getName(), p);
        } else {
          if (strategyMap.get(p.getName()) != p) {
            String msg = "Multiple different TagBarcodeStrategies with the same strategy name " + "('" + p.getName()
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

  public Collection<TagBarcodeStrategy> getTagBarcodeStrategiesByPlatform(PlatformType platformType) {
    if (strategyMap == null) {
      getTagBarcodeStrategies();
    }

    Set<TagBarcodeStrategy> ts = new HashSet<TagBarcodeStrategy>();
    for (TagBarcodeStrategy s : strategyMap.values()) {
      if (s.getPlatformType().equals(platformType)) {
        ts.add(s);
      }
    }
    return ts;
  }
}
