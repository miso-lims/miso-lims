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

package uk.ac.bbsrc.tgac.miso.webapp.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

/**
 * A handy class that exposes property placeholders discovered at webapp init time by a PropertyPlaceholderConfigurer.
 * <p/>
 * This class accepts a list of available property files referenced in the Spring configs via a property list, or alternatively
 * you can supply a single MISO miso.properties file that contains the base MISO storage directory (where other property
 * files are housed). These additional properties files will be discovered at runtime, imported into the base properties,
 * and used by Spring to do its magic.
 * <p/>
 * As an aside, usually these properties are not available to beans, but this class exposes them via getResolvedProperties()
 *
 * @author Rob Davey
 * @date 01-Sep-2010
 * @since 0.0.2
 */
public class MisoPropertyExporter extends PropertyPlaceholderConfigurer {
  private Map<String, String> resolvedProperties;
  
  @Override
  protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                   Properties misoProps) throws BeansException {

    String baseStoragePath = misoProps.getProperty("miso.baseDirectory");
    if (baseStoragePath != null) {

      Map<String, String> propchecks = MisoWebUtils.checkCorePropertiesFiles(baseStoragePath);
      if (propchecks.keySet().contains("error")) {
        throw new BeanInitializationException(propchecks.get("error"));
      }

      List<String> propertiesList = Arrays.asList(new File(baseStoragePath).list(new PropertiesFilenameFilter()));
      for (String propPath : propertiesList) {
        System.out.println("Attempting to load " + baseStoragePath+propPath);
        Properties tempProps;

        try {
          InputStream in = new FileInputStream(new File(baseStoragePath, propPath));
          tempProps = new Properties();
          try {
            tempProps.load(in);
            System.out.println("Loaded " + tempProps.keySet() + " from " + propPath);
            CollectionUtils.mergePropertiesIntoMap(tempProps, misoProps);
          }
          catch (IOException e) {
            throw new InvalidPropertyException(MisoPropertyExporter.class, "All", "Cannot load " + baseStoragePath+propPath + " properties. Cannot read file!");
          }
        }
        catch (FileNotFoundException e) {
          throw new InvalidPropertyException(MisoPropertyExporter.class, "All", "Cannot load " + baseStoragePath+propPath + " properties. File does not exist!");
        }
      }

      super.processProperties(beanFactoryToProcess, misoProps);
      resolvedProperties = new HashMap<String, String>();
      for (Object key : misoProps.keySet()) {
        String keyStr = key.toString();

        //doesn't seem to resolve properties properly - just end up null
        //resolvedProperties.put(keyStr, resolvePlaceholder(props.getProperty(keyStr), props, SYSTEM_PROPERTIES_MODE_OVERRIDE));
        resolvedProperties.put(keyStr, misoProps.getProperty(keyStr));
      }
    }
    else {
      throw new InvalidPropertyException(MisoPropertyExporter.class,
                                         "miso.baseDirectory", 
                                         "Cannot resolve miso.baseDirectory. This should be specified in the " +
                                         "miso.properties file which should be made available on the classpath.");
    }
  }

  public Map<String, String> getResolvedProperties() {
    return Collections.unmodifiableMap(resolvedProperties);
  }

  protected class PropertiesFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      return name.endsWith(".properties"); 
    }
  }
}
