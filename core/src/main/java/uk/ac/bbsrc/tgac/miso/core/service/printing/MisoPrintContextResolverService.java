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

package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class MisoPrintContextResolverService implements PrintContextResolverService {
  protected static final Logger log = LoggerFactory.getLogger(MisoPrintContextResolverService.class);
  private Map<String, PrintContext> contextMap;

  @Override
  public PrintContext getPrintContext(String contextName) {
    for (PrintContext context : getPrintContexts()) {
      if (context.getName().equals(contextName)) {
        try {
          return context.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + contextName + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + contextName + "'", e);
        }
      }
    }
    log.warn("No context called '" + contextName + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<PrintContext> getPrintContexts() {
    // lazily load available contexts
    if (contextMap == null) {
      ServiceLoader<PrintContext> consumerLoader = ServiceLoader.load(PrintContext.class);
      Iterator<PrintContext> consumerIterator = consumerLoader.iterator();

      contextMap = new HashMap<String, PrintContext>();
      while (consumerIterator.hasNext()) {
        PrintContext p = consumerIterator.next();

        if (!contextMap.containsKey(p.getName())) {
          contextMap.put(p.getName(), p);
        } else {
          if (contextMap.get(p.getName()) != p) {
            String msg = "Multiple different PrintContexts with the same context name " + "('" + p.getName()
                + "') are present on the classpath. Context names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }
      log.info("Loaded " + contextMap.values().size() + " known contexts");
    }

    return contextMap.values();
  }
}
