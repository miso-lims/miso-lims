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

import uk.ac.bbsrc.tgac.miso.core.service.printing.schema.BarcodableSchema;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.printing
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 * @date 29/05/13
 * @since 0.2.0
 */
public class MisoBarcodableSchemaResolverService implements BarcodableSchemaResolverService {
  protected static final Logger log = LoggerFactory.getLogger(MisoBarcodableSchemaResolverService.class);
  private Map<String, BarcodableSchema> contextMap;

  @Override
  public BarcodableSchema getBarcodableSchema(String name) {
    for (BarcodableSchema barcodableSchema : getBarcodableSchemas()) {
      if (barcodableSchema.getName().equals(name)) {
        try {
          return barcodableSchema.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + name + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + name + "'", e);
        }
      }
    }
    log.warn("No schema called '" + name + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<BarcodableSchema> getBarcodableSchemas() {
    // lazily load available schemas
    if (contextMap == null) {
      ServiceLoader<BarcodableSchema> consumerLoader = ServiceLoader.load(BarcodableSchema.class);
      Iterator<BarcodableSchema> consumerIterator = consumerLoader.iterator();

      contextMap = new HashMap<String, BarcodableSchema>();
      while (consumerIterator.hasNext()) {
        BarcodableSchema p = consumerIterator.next();

        if (!contextMap.containsKey(p.getName())) {
          contextMap.put(p.getName(), p);
        } else {
          if (contextMap.get(p.getName()) != p) {
            String msg = "Multiple different BarcodableSchemas with the same schema name " + "('" + p.getName()
                + "') are present on the classpath. Schema names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }
      log.info("Loaded " + contextMap.values().size() + " known schemas");
    }

    return contextMap.values();
  }

}
