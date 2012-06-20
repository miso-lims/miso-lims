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

package uk.ac.bbsrc.tgac.miso.webapp.context;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.PrintableBarcode;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.factory.issuetracker.IssueTrackerFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The custom MISO context listener class. On webapp context init, we can do some startup checks, e.g. checking the existence of required directories/files
 * for sane app startup
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoAppListener implements ServletContextListener {
  protected static final Logger log = LoggerFactory.getLogger(MisoAppListener.class);

  /**
   * Called on webapp context init
   *
   * @param event of type ServletContextEvent
   */
  public void contextInitialized(ServletContextEvent event) {
    ServletContext application = event.getServletContext();
    XmlWebApplicationContext context = (XmlWebApplicationContext)WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    //resolve property file configuration placeholders
    MisoPropertyExporter exporter = (MisoPropertyExporter) context.getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
            
    log.info("Checking MISO storage paths...");
    String baseStoragePath = misoProperties.get("miso.baseDirectory");
    context.getServletContext().setAttribute("miso.baseDirectory", baseStoragePath);

    String taxonLookupEnabled = misoProperties.get("miso.taxonLookup.enabled");
    context.getServletContext().setAttribute("taxonLookupEnabled", Boolean.parseBoolean(taxonLookupEnabled));
    
    Map<String, String> dirchecks = MisoWebUtils.checkStorageDirectories(baseStoragePath);
    if (dirchecks.keySet().contains("error")) {
      log.error(dirchecks.get("error"));
    }
    else {
      log.info(dirchecks.get("ok"));
    }

    //set headless property so JFreeChart doesn't try to use the X rendering system to generate images
    System.setProperty("java.awt.headless", "true");

    //set up printers
    PrintManager printManager = (PrintManager)context.getBean("printManager");
    Collection<PrintContext> pcs = printManager.getPrintContexts();
    for (PrintContext pc : pcs) {
      log.info(pc.getName() + " : " + pc.getDescription());
    }

    try {
      Collection<MisoPrintService> mpss = printManager.listAllPrintServices();
      for (MisoPrintService mps : mpss) {
        log.info("Got print service: " + mps.toString());
      }
    }
    catch (Exception e) {
      log.error("Could not list print services. This does not bode well for printing.", e);
      e.printStackTrace();
    }

    //set up alerting
    if ("true".equals(misoProperties.get("miso.alerting.enabled"))) {
      //set up indexers and alerters
      MisoRequestManager rm = new MisoRequestManager();
      rm.setProjectStore((ProjectStore)context.getBean("projectStore"));
      rm.setRunStore((RunStore)context.getBean("runStore"));
      rm.setPoolStore((PoolStore)context.getBean("poolStore"));

      SecurityManager sm = (com.eaglegenomics.simlims.core.manager.SecurityManager)context.getBean("securityManager");

      RunAlertManager ram = (RunAlertManager)context.getBean("runAlertManager");
      ram.setRequestManager(rm);
      ram.setSecurityManager(sm);

      ProjectAlertManager pam = (ProjectAlertManager)context.getBean("projectAlertManager");
      pam.setRequestManager(rm);
      pam.setSecurityManager(sm);

      PoolAlertManager poam = (PoolAlertManager)context.getBean("poolAlertManager");
      poam.setRequestManager(rm);
      poam.setSecurityManager(sm);

      try {
        ram.indexify();
        pam.indexify();
        poam.indexify();
      }
      catch (IOException e) {
        e.printStackTrace();
        log.error("Unable to set up alerting system: " + e.getMessage());
      }
    }

    if ("true".equals(misoProperties.get("miso.issuetracker.enabled"))) {
      String trackerType = misoProperties.get("miso.issuetracker.tracker");
      if (trackerType != null && !"".equals(trackerType)) {
        try {
          IssueTrackerManager manager = IssueTrackerFactory.newInstance().getTrackerManager(trackerType);
          if (manager != null) {
            for (String key : misoProperties.keySet()) {
              if (key.startsWith("miso.issuetracker."+trackerType)) {
                String prop = key.substring(key.lastIndexOf(".")+1);
                String methodName = "set"+prop.substring(0,1).toUpperCase() + prop.substring(1);
                Method m = manager.getClass().getDeclaredMethod(methodName, String.class);
                m.invoke(manager, misoProperties.get(key));
              }
            }
            context.getBeanFactory().registerSingleton("issueTrackerManager", manager);
          }
          else {
            log.error("No such issue tracker available with given type: " + trackerType);
          }
        }
        catch (NoSuchMethodException e) {
          log.error("Unable to start the defined issuetracker " + trackerType + ": " + e.getMessage());
          e.printStackTrace();
        }
        catch (InvocationTargetException e) {
          log.error("Unable to start the defined issuetracker " + trackerType + ": " + e.getMessage());
          e.printStackTrace();
        }
        catch (IllegalAccessException e) {
          log.error("Unable to start the defined issuetracker " + trackerType + ": " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Called on webapp destruction
   *
   * @param event of type ServletContextEvent
   */
  public void contextDestroyed(ServletContextEvent event) {
    ServletContext application = event.getServletContext();
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
    log.info("MISO Application Context Destroyed: " + new Date());
  }
}
