/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.factory.issuetracker.IssueTrackerFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.NamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.runstats.client.manager.RunStatsManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import io.prometheus.client.hotspot.DefaultExports;

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
    // Export all JVM HotSpot stats to Prometheus
    DefaultExports.initialize();

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

    initializeNamingSchemes(context, misoProperties);

    // set up printers
    PrintManager<?, ?> printManager = (PrintManager<?, ?>) context.getBean("printManager");
    Collection<PrintContext> pcs = printManager.getPrintContexts();
    for (PrintContext<?> pc : pcs) {
      log.info(pc.getName() + " : " + pc.getDescription());
    }

    try {
      Collection<MisoPrintService> mpss = printManager.listAllPrintServices();
      for (MisoPrintService<?, ?, ?> mps : mpss) {
        log.info("Got print service: " + mps.toString());
      }
    } catch (Exception e) {
      log.error("Could not list print services. This does not bode well for printing.", e);
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
        log.info("\\_ samples...");
        log.info("" + rm.listAllSamples().size());
        log.info("\\_ libraries...");
        log.info("" + rm.listAllLibraries().size());
        log.info("\\_ dilutions...");
        log.info("" + rm.listAllLibraryDilutions().size());
        log.info("" + rm.listAllEmPCRDilutions().size());
        log.info("\\_ pools...");
        log.info("" + rm.listAllPools().size());
        log.info("\\_ sequencing containers...");
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

  private void initializeNamingSchemes(XmlWebApplicationContext context, Map<String, String> misoProperties) {
    // set up naming schemes
    NamingSchemeResolverService resolver = (NamingSchemeResolverService) context
        .getBean("namingSchemeResolverService");

    String currentPropertyValue = null;
    NamingScheme scheme = (NamingScheme) context.getBeanFactory().getBean("namingScheme");
    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.scheme", misoProperties)) != null) {
      scheme = resolver.getNamingScheme(currentPropertyValue);
      if (scheme == null) throw new IllegalArgumentException("Failed to load naming scheme '" + currentPropertyValue + "'");
      SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(scheme);
      log.info("Replacing default namingScheme with " + scheme.getClass().getSimpleName());
      ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition("namingScheme");
      context.getBeanFactory().registerSingleton("namingScheme", scheme);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.generator.nameable.name", misoProperties)) != null) {
      NameGenerator<Nameable> generator = resolver.getNameGenerator(currentPropertyValue);
      if (generator == null) throw new IllegalArgumentException("Failed to load name generator '" + currentPropertyValue + "'");
      SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(generator);
      scheme.setNameGenerator(generator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.generator.sample.alias", misoProperties)) != null) {
      NameGenerator<Sample> generator = resolver.getSampleAliasGenerator(currentPropertyValue);
      if (generator == null) throw new IllegalArgumentException("Failed to load sample alias generator '" + currentPropertyValue + "'");
      SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(generator);
      scheme.setSampleAliasGenerator(generator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.generator.library.alias", misoProperties)) != null) {
      NameGenerator<Library> generator = resolver.getLibraryAliasGenerator(currentPropertyValue);
      if (generator == null) throw new IllegalArgumentException("Failed to load library alias generator '" + currentPropertyValue + "'");
      SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(generator);
      scheme.setLibraryAliasGenerator(generator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.validator.nameable.name", misoProperties)) != null) {
      NameValidator validator = resolver.getNameValidator(currentPropertyValue);
      setUpValidator(validator, misoProperties, "miso.naming.validator.nameable.name");
      scheme.setNameValidator(validator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.validator.sample.alias", misoProperties)) != null) {
      NameValidator validator = resolver.getSampleAliasValidator(currentPropertyValue);
      setUpValidator(validator, misoProperties, "miso.naming.validator.sample.alias");
      scheme.setSampleAliasValidator(validator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.validator.library.alias", misoProperties)) != null) {
      NameValidator validator = resolver.getLibraryAliasValidator(currentPropertyValue);
      setUpValidator(validator, misoProperties, "miso.naming.validator.library.alias");
      scheme.setLibraryAliasValidator(validator);
    }

    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.validator.project.shortName", misoProperties)) != null) {
      NameValidator validator = resolver.getLibraryAliasValidator(currentPropertyValue);
      setUpValidator(validator, misoProperties, "miso.naming.validator.library.alias");
      scheme.setLibraryAliasValidator(validator);
    }
  }

  private String getStringPropertyOrNull(String key, Map<String, String> misoProperties) {
    String value = misoProperties.get(key);
    return LimsUtils.isStringBlankOrNull(value) ? null : value;
  }

  private void setUpValidator(NameValidator validator, Map<String, String> misoProperties, String baseProperty) {
    if (validator == null) throw new IllegalArgumentException("Failed to load name validator specified for '" + baseProperty + "'");
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(validator);

    String regexProp = misoProperties.get(baseProperty + ".regex");
    if (!LimsUtils.isStringBlankOrNull(regexProp)) {
      validator.setValidationRegex(regexProp);
    }
    if (misoProperties.containsKey(baseProperty + ".duplicates")) {
      boolean duplicatesProp = Boolean.valueOf(misoProperties.get("miso.naming.validator.nameable.name.regex"));
      validator.setDuplicateAllowed(duplicatesProp);
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
    log.info("MISO Application Context Destroyed: " + new Date());
  }
}
