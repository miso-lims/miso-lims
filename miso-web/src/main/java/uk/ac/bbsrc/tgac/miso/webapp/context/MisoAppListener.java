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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.issuetracker.IssueTrackerFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoEntityNamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNameGeneratorResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.tagbarcode.TagBarcodeStrategyResolverService;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.runstats.client.manager.RunStatsManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

/**
 * The custom MISO context listener class. On webapp context init, we can do some startup checks, e.g. checking the existence of required
 * directories/files for sane app startup
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoAppListener implements ServletContextListener {
  protected static final Logger log = LoggerFactory.getLogger(MisoAppListener.class);

  /**
   * Called on webapp context init
   * 
   * @param event
   *          of type ServletContextEvent
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    ServletContext application = event.getServletContext();

    // load logging system manually so that we get the placeholders ${} swapped out for real values
    PropertyConfigurator.configure(application.getRealPath("/") + "/WEB-INF/log4j.miso.properties");

    XmlWebApplicationContext context = (XmlWebApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // resolve property file configuration placeholders
    MisoPropertyExporter exporter = (MisoPropertyExporter) context.getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();

    context.getServletContext().setAttribute("security.method", misoProperties.get("security.method"));

    log.info("Checking MISO storage paths...");
    String baseStoragePath = misoProperties.get("miso.baseDirectory");
    context.getServletContext().setAttribute("miso.baseDirectory", baseStoragePath);

    String taxonLookupEnabled = misoProperties.get("miso.taxonLookup.enabled");
    context.getServletContext().setAttribute("taxonLookupEnabled", Boolean.parseBoolean(taxonLookupEnabled));

    Map<String, String> dirchecks = MisoWebUtils.checkStorageDirectories(baseStoragePath);
    if (dirchecks.keySet().contains("error")) {
      log.error(dirchecks.get("error"));
    } else {
      log.info(dirchecks.get("ok"));
    }

    // set headless property so JFreeChart doesn't try to use the X rendering system to generate images
    System.setProperty("java.awt.headless", "true");

    // set up naming schemes
    MisoEntityNamingSchemeResolverService entityNamingSchemeResolverService = (MisoEntityNamingSchemeResolverService) context
        .getBean("entityNamingSchemeResolverService");
    Collection<MisoNamingScheme<?>> mnss = entityNamingSchemeResolverService.getNamingSchemes();

    MisoNameGeneratorResolverService nameGeneratorResolverService = (MisoNameGeneratorResolverService) context
        .getBean("nameGeneratorResolverService");
    Collection<NameGenerator<?>> ngs = nameGeneratorResolverService.getNameGenerators();

    for (MisoNamingScheme<?> mns : mnss) {
      log.info("Got naming scheme: " + mns.getSchemeName());
      String classname = mns.namingSchemeFor().getSimpleName().toLowerCase();

      if (misoProperties.containsKey("miso.naming.scheme." + classname)
          && misoProperties.get("miso.naming.scheme." + classname).equals(mns.getSchemeName())) {
        for (String key : misoProperties.keySet()) {
          if (key.startsWith("miso.naming.generator." + classname)) {
            String genprop = key.substring(key.lastIndexOf(".") + 1);
            NameGenerator ng = nameGeneratorResolverService
                .getNameGenerator(misoProperties.get("miso.naming.generator." + classname + "." + genprop));
            if (ng != null) {
              mns.registerCustomNameGenerator(genprop, ng);
            }
          }
        }

        if ("nameable".equals(classname)) {
          log.info("Replacing default global namingScheme with " + mns.getSchemeName());
          ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition("namingScheme");
          context.getBeanFactory().registerSingleton("namingScheme", mns);
        } else {
          log.info("Replacing default " + classname + "NamingScheme with " + mns.getSchemeName());
          ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition(classname + "NamingScheme");
          context.getBeanFactory().registerSingleton(classname + "NamingScheme", mns);
        }
      }

      for (String key : misoProperties.keySet()) {
        if (key.startsWith("miso.naming.validation." + classname)) {
          String prop = key.substring(key.lastIndexOf(".") + 1);

          try {
            mns.setValidationRegex(prop, misoProperties.get("miso.naming.validation." + classname + "." + prop));
          } catch (MisoNamingException e) {
            log.error("Cannot set new validation regex for field '" + prop + "'. Reverting to default.", e);
          }
        }

        if (key.startsWith("miso.naming.duplicates." + classname)) {
          String prop = key.substring(key.lastIndexOf(".") + 1);
          mns.setAllowDuplicateEntityName(prop,
              Boolean.parseBoolean(misoProperties.get("miso.naming.duplicates." + classname + "." + prop)));
        }
      }
    }

    // set up printers
    PrintManager printManager = (PrintManager) context.getBean("printManager");
    Collection<PrintContext> pcs = printManager.getPrintContexts();
    for (PrintContext pc : pcs) {
      log.info(pc.getName() + " : " + pc.getDescription());
    }

    try {
      Collection<MisoPrintService> mpss = printManager.listAllPrintServices();
      for (MisoPrintService mps : mpss) {
        log.info("Got print service: " + mps.toString());
      }
    } catch (Exception e) {
      log.error("Could not list print services. This does not bode well for printing.", e);
    }

    // set up Tag Barcode strategies
    TagBarcodeStrategyResolverService tagBarcodeService = (TagBarcodeStrategyResolverService) context
        .getBean("tagBarcodeStrategyResolverService");
    Collection<TagBarcodeStrategy> tbss = tagBarcodeService.getTagBarcodeStrategies();
    for (TagBarcodeStrategy tbs : tbss) {
      log.info("Got Tag Barcode Index service: " + tbs.getName());
    }

    // set up alerting
    if ("true".equals(misoProperties.get("miso.alerting.enabled"))) {
      // set up indexers and alerters
      MisoRequestManager rm = new MisoRequestManager();
      rm.setProjectStore((ProjectStore) context.getBean("projectStore"));
      rm.setRunStore((RunStore) context.getBean("runStore"));
      rm.setRunQcStore((RunQcStore) context.getBean("runQcStore"));
      rm.setPoolStore((PoolStore) context.getBean("poolStore"));

      SecurityManager sm = (com.eaglegenomics.simlims.core.manager.SecurityManager) context.getBean("securityManager");

      RunAlertManager ram = (RunAlertManager) context.getBean("runAlertManager");
      ram.setRequestManager(rm);
      ram.setSecurityManager(sm);

      ProjectAlertManager pam = (ProjectAlertManager) context.getBean("projectAlertManager");
      pam.setRequestManager(rm);
      pam.setSecurityManager(sm);

      PoolAlertManager poam = (PoolAlertManager) context.getBean("poolAlertManager");
      poam.setRequestManager(rm);
      poam.setSecurityManager(sm);
    }

    if (misoProperties.containsKey("miso.db.caching.mappers.enabled")) {
      boolean mapperCachingEnabled = Boolean.parseBoolean(misoProperties.get("miso.db.caching.mappers.enabled"));
      // TODO do something with this - probably set caching throughout DAOs
    }

    if ("true".equals(misoProperties.get("miso.db.caching.precache.enabled"))) {
      log.info("Precaching. This may take a while.");
      try {
        RequestManager rm = (RequestManager) context.getBean("requestManager");

        User userdetails = new User("precacher", "none", true, true, true, true,
            AuthorityUtils.createAuthorityList("ROLE_ADMIN,ROLE_INTERNAL"));
        PreAuthenticatedAuthenticationToken newAuthentication = new PreAuthenticatedAuthenticationToken(userdetails,
            userdetails.getPassword(), userdetails.getAuthorities());
        newAuthentication.setAuthenticated(true);
        newAuthentication.setDetails(userdetails);

        try {
          SecurityContext sc = SecurityContextHolder.getContextHolderStrategy().getContext();
          sc.setAuthentication(newAuthentication);
          SecurityContextHolder.getContextHolderStrategy().setContext(sc);
        } catch (AuthenticationException a) {
          log.error("security context init", a);
        }

        log.info("\\_ projects...");
        log.info("" + rm.listAllProjects().size());
        log.info("\\_ studies...");
        log.info("" + rm.listAllStudies().size());
        log.info("\\_ experiments...");
        log.info("" + rm.listAllExperiments().size());
        log.info("\\_ samples...");
        log.info("" + rm.listAllSamples().size());
        log.info("\\_ libraries...");
        log.info("" + rm.listAllLibraries().size());
        log.info("\\_ dilutions...");
        log.info("" + rm.listAllLibraryDilutions().size());
        log.info("" + rm.listAllEmPCRDilutions().size());
        log.info("\\_ pools...");
        log.info("" + rm.listAllPools().size());
        log.info("\\_ plates...");
        log.info("" + rm.listAllPlates().size());
        log.info("\\_ sequencer partition containers...");
        log.info("" + rm.listAllSequencerPartitionContainers().size());
        log.info("\\_ runs...");
        log.info("" + rm.listAllRuns().size());
      } catch (IOException e) {
        log.info("precache", e);
      }
    }

    if ("true".equals(misoProperties.get("miso.issuetracker.enabled"))) {
      String trackerType = misoProperties.get("miso.issuetracker.tracker");
      if (!isStringEmptyOrNull(trackerType)) {
        try {
          IssueTrackerManager manager = IssueTrackerFactory.newInstance().getTrackerManager(trackerType);
          if (manager != null) {
            for (String key : misoProperties.keySet()) {
              if (key.startsWith("miso.issuetracker." + trackerType)) {
                String prop = key.substring(key.lastIndexOf(".") + 1);
                String methodName = "set" + LimsUtils.capitalise(prop); // prop.substring(0,1).toUpperCase() + prop.substring(1);
                Method m = manager.getClass().getDeclaredMethod(methodName, String.class);
                m.invoke(manager, misoProperties.get(key));
              }
            }
            ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition("issueTrackerManager");
            context.getBeanFactory().registerSingleton("issueTrackerManager", manager);
          } else {
            log.error("No such issue tracker available with given type: " + trackerType);
          }
        } catch (NoSuchMethodException e) {
          log.error("Unable to start the defined issuetracker " + trackerType, e);
        } catch (InvocationTargetException e) {
          log.error("Unable to start the defined issuetracker " + trackerType, e);
        } catch (IllegalAccessException e) {
          log.error("Unable to start the defined issuetracker " + trackerType, e);
        }
      }
    }

    if ("true".equals(misoProperties.get("miso.statsdb.enabled"))) {
      try {
        JndiObjectFactoryBean jndiBean = new JndiObjectFactoryBean();
        jndiBean.setLookupOnStartup(true);
        jndiBean.setResourceRef(true);
        jndiBean.setJndiName("jdbc/STATSDB");
        jndiBean.setExpectedType(javax.sql.DataSource.class);
        jndiBean.afterPropertiesSet();

        DataSource datasource = (DataSource) jndiBean.getObject();

        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(datasource);
        template.setNativeJdbcExtractor(new CommonsDbcpNativeJdbcExtractor());
        context.getBeanFactory().registerSingleton("statsInterfaceTemplate", template);

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(datasource);
        context.getBeanFactory().registerSingleton("statsTransactionManager", transactionManager);

        RunStatsManager rsm = new RunStatsManager(template);
        context.getBeanFactory().registerSingleton("runStatsManager", rsm);
      } catch (NamingException e) {
        log.error("Cannot initiate statsdb connection", e);
      }
    }
  }

  /**
   * Called on webapp destruction
   * 
   * @param event
   *          of type ServletContextEvent
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    ServletContext application = event.getServletContext();
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
    log.info("MISO Application Context Destroyed: " + new Date());
  }
}
