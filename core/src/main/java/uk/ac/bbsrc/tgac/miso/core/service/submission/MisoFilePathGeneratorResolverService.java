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

package uk.ac.bbsrc.tgac.miso.core.service.submission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.integration
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/02/12
 * @since 0.1.5
 */
public class MisoFilePathGeneratorResolverService implements FilePathGeneratorResolverService {
  protected static final Logger log = LoggerFactory.getLogger(MisoFilePathGeneratorResolverService.class);
  private Map<String, FilePathGenerator> contextMap;

  @Override
  public FilePathGenerator getFilePathGenerator(PlatformType platformType) {
    for (FilePathGenerator generator : getFilePathGenerators()) {
      if (generator.generatesFilePathsFor() != null && generator.generatesFilePathsFor().equals(platformType)) {
        try {
          return generator.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + generator.getName() + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + generator.getName() + "'", e);
        }
      }
    }
    log.warn("No generator available for '" + platformType.getKey() + "' was available on the classpath");
    return null;
  }

  @Override
  public FilePathGenerator getFilePathGenerator(String generatorName) {
    for (FilePathGenerator generator : getFilePathGenerators()) {
      if (generator.getName().equals(generatorName)) {
        try {
          return generator.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + generatorName + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + generatorName + "'", e);
        }
      }
    }
    log.warn("No generator called '" + generatorName + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<FilePathGenerator> getFilePathGenerators() {
    // lazily load available contexts
    if (contextMap == null) {
      ServiceLoader<FilePathGenerator> consumerLoader = ServiceLoader.load(FilePathGenerator.class);
      Iterator<FilePathGenerator> consumerIterator = consumerLoader.iterator();

      contextMap = new HashMap<String, FilePathGenerator>();
      while (consumerIterator.hasNext()) {
        FilePathGenerator p = consumerIterator.next();

        if (!contextMap.containsKey(p.getName())) {
          contextMap.put(p.getName(), p);
        } else {
          if (contextMap.get(p.getName()) != p) {
            String msg = "Multiple different FilePathGenerators with the same context name " + "('" + p.getName()
                + "') are present on the classpath. Generator names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }
      log.info("Loaded " + contextMap.values().size() + " known generators");
    }

    return contextMap.values();
  }
}
