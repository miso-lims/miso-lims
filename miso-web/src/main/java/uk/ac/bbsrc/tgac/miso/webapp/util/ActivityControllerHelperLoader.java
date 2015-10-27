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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.eaglegenomics.simlims.core.Activity;

//import com.eaglegenomics.simlims.spring.ActivityControllerHelper;

public class ActivityControllerHelperLoader implements InitializingBean, ApplicationContextAware {
  // protected static final Logger log = LoggerFactory.getLogger(ActivityControllerHelperLoader.class);

  // private transient Map<Activity, ActivityControllerHelper> activityHelpers = new HashMap<Activity, ActivityControllerHelper>();

  @Autowired
  private ApplicationContext context;

  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }

  /*
   * public ActivityControllerHelper getHelper(Activity key) { return activityHelpers.get(key); }
   */
  public void afterPropertiesSet() throws Exception {
    /*
     * for (ActivityControllerHelper entry : context.getBeansOfType( ActivityControllerHelper.class).values()) { if (log.isInfoEnabled()) {
     * log.info("Loaded activity helper for " + entry.getActivity().getUniqueIdentifier()); } activityHelpers.put(entry.getActivity(),
     * entry); }
     */
  }
}
