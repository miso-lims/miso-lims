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

package uk.ac.bbsrc.tgac.miso.notification.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * A handy class that exposes property placeholders discovered at init time by a PropertyPlaceholderConfigurer.
 * <p/>
 * As an aside, usually these properties are not available to beans, but this class exposes them via getResolvedProperties()
 * 
 * @author Rob Davey
 * @date 09-Dec-2011
 * @since 0.1.4
 */
public class NotificationPropertyExporter extends PropertyPlaceholderConfigurer {
  private Map<String, String> resolvedProperties;

  @Override
  protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties properties) throws BeansException {
    super.processProperties(beanFactoryToProcess, properties);
    resolvedProperties = new HashMap<String, String>();
    for (Object key : properties.keySet()) {
      String keyStr = key.toString();
      resolvedProperties.put(keyStr, properties.getProperty(keyStr));
    }
  }

  public Map<String, String> getResolvedProperties() {
    return Collections.unmodifiableMap(resolvedProperties);
  }
}
