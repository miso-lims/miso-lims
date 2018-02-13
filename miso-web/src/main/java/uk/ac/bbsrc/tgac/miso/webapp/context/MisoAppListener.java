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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.context;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.jmx.JmxCollector;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.MalformedObjectNameException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DelegatingNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.NamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
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
    // Export all JVM HotSpot stats to Prometheus
    DefaultExports.initialize();

    ServletContext application = event.getServletContext();

    // load logging system manually so that we get the placeholders ${} swapped out for real values
    PropertyConfigurator.configure(application.getRealPath("/") + "/WEB-INF/log4j.miso.properties");

    XmlWebApplicationContext context = (XmlWebApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext(application);

    // resolve property file configuration placeholders
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
    } else {
      log.info(dirchecks.get("ok"));
    }

    // set headless property so JFreeChart doesn't try to use the X rendering system to generate images
    System.setProperty("java.awt.headless", "true");

    initializeNamingSchemes(context, misoProperties);

    new HibernateStatisticsCollector(context.getBeanFactory().getBean(SessionFactory.class), "spring").register();
    try {
      new JmxCollector(context.getResource("classpath:tomcat-prometheus.yml").getFile()).register();
    } catch (MalformedObjectNameException | IOException e) {
      log.error("Failed to load Prometheus configuration.", e);
    }

    loadIssueTrackerManager(misoProperties, context);
  }

  private void loadIssueTrackerManager(Map<String, String> misoProperties, XmlWebApplicationContext context) {
    if ("true".equals(misoProperties.get("miso.issuetracker.enabled"))) {
      String trackerType = misoProperties.get("miso.issuetracker.tracker");
      if (!isStringEmptyOrNull(trackerType)) {
        List<IssueTrackerManager> managers = context.getBeanFactory().getBeansOfType(IssueTrackerManager.class).values().stream()
            .filter(mgr -> mgr.getType().equals(trackerType))
            .collect(Collectors.toList());
        switch (managers.size()) {
        case 0:
          throw new IllegalStateException("No issue tracker available with given type " + trackerType);
        case 1:
          ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition("issueTrackerManager");
          context.getBeanFactory().registerSingleton("issueTrackerManager", managers.get(0));
          break;
        default:
          throw new IllegalStateException("Found multiple IssueTrackerManagers of type " + trackerType);
        }
      }
    }
  }

  private void initializeNamingSchemes(XmlWebApplicationContext context, Map<String, String> misoProperties) {
    // set up naming schemes
    NamingSchemeResolverService resolver = (NamingSchemeResolverService) context
        .getBean("namingSchemeResolverService");

    String currentPropertyValue = null;
    DelegatingNamingScheme schemeHolder = (DelegatingNamingScheme) context.getBeanFactory().getBean("namingScheme");
    if ((currentPropertyValue = getStringPropertyOrNull("miso.naming.scheme", misoProperties)) == null) {
      throw new IllegalArgumentException("No naming scheme configured");
    }
    NamingScheme scheme = resolver.getNamingScheme(currentPropertyValue);
    if (scheme == null) throw new IllegalArgumentException("Failed to load naming scheme '" + currentPropertyValue + "'");
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(scheme);
    log.info("Replacing default namingScheme with " + scheme.getClass().getSimpleName());
    schemeHolder.setActualNamingScheme(scheme);

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
