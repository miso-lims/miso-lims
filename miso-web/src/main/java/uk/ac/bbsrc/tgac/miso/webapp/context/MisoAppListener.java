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

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

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

import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DelegatingNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.NamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira.JiraIssueManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import io.prometheus.client.hibernate.HibernateStatisticsCollector;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.jmx.JmxCollector;

/**
 * The custom MISO context listener class. On webapp context init, we can do some startup checks, e.g. checking the existence of required
 * directories/files for sane app startup
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoAppListener implements ServletContextListener {
  protected static final Logger log = LoggerFactory.getLogger(MisoAppListener.class);

  private static String getStringPropertyOrNull(String key, Map<String, String> misoProperties) {
    String value = misoProperties.get(key);
    return LimsUtils.isStringBlankOrNull(value) ? null : value;
  }

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
    String fileStoragePath = misoProperties.get("miso.fileStorageDirectory");

    Map<String, String> dirchecks = MisoWebUtils.checkStorageDirectories(baseStoragePath, fileStoragePath);
    if (dirchecks.keySet().contains("error")) {
      throw new IllegalStateException(dirchecks.get("error"));
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
    if (misoProperties.containsKey("miso.issuetracker.tracker")) {
      String trackerType = misoProperties.get("miso.issuetracker.tracker");
      if ("jira".equals(trackerType)) {
        IssueTrackerManager issueTracker = new JiraIssueManager();
        Properties properties = new Properties();
        properties.putAll(misoProperties);
        issueTracker.setConfiguration(properties);
        ((DefaultListableBeanFactory) context.getBeanFactory()).removeBeanDefinition("issueTrackerManager");
        context.getBeanFactory().registerSingleton("issueTrackerManager", issueTracker);
      } else {
        throw new IllegalArgumentException("Invalid tracker type specified at miso.issuetracker.tracker");
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
    log.info("Replacing default namingScheme with {}", scheme.getClass().getSimpleName());
    schemeHolder.setActualNamingScheme(scheme);

    applyGenerator("miso.naming.generator.nameable.name", misoProperties, resolver::getNameGenerator, scheme::setNameGenerator);
    applyGenerator("miso.naming.generator.sample.alias", misoProperties, resolver::getSampleAliasGenerator, scheme::setSampleAliasGenerator);
    applyGenerator("miso.naming.generator.library.alias", misoProperties, resolver::getLibraryAliasGenerator,
        scheme::setLibraryAliasGenerator);

    applyValidator("miso.naming.validator.nameable.name", misoProperties, resolver::getNameValidator, scheme::setNameValidator);
    applyValidator("miso.naming.validator.sample.alias", misoProperties, resolver::getSampleAliasValidator, scheme::setSampleAliasValidator);
    applyValidator("miso.naming.validator.library.alias", misoProperties, resolver::getLibraryAliasValidator,
        scheme::setLibraryAliasValidator);
    applyValidator("miso.naming.validator.project.shortName", misoProperties, resolver::getProjectShortNameValidator,
        scheme::setProjectShortNameValidator);
  }

  private <T> void applyGenerator(String property, Map<String, String> misoProperties, Function<String, NameGenerator<T>> resolver,
      Consumer<NameGenerator<T>> setter) {
    NameGenerator<T> generator = loadComponent(property, misoProperties, resolver);
    if (generator != null) {
      setter.accept(generator);
    }
  }

  private void applyValidator(String property, Map<String, String> misoProperties, Function<String, NameValidator> resolver,
      Consumer<NameValidator> setter) {
    NameValidator validator = loadComponent(property, misoProperties, resolver);
    if (validator != null) {
      String regex = misoProperties.get(property + ".regex");
      if (!LimsUtils.isStringBlankOrNull(regex)) {
        validator.setValidationRegex(regex);
      }
      String duplicatesProp = property + ".duplicates";
      if (misoProperties.containsKey(duplicatesProp)) {
        boolean allowDuplicates = Boolean.valueOf(misoProperties.get(duplicatesProp));
        validator.setDuplicateAllowed(allowDuplicates);
      }
      setter.accept(validator);
    }
  }
  
  private <T> T loadComponent(String property, Map<String, String> misoProperties, Function<String, T> resolver) {
    String value = getStringPropertyOrNull(property, misoProperties);
    if (value == null) {
      return null;
    }
    T component = resolver.apply(value);
    if (component == null) {
      throw new IllegalArgumentException("Failed to load name naming scheme component specified for '" + property + "'");
    }
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(component);
    return component;
  }

  /**
   * Called on webapp destruction
   * 
   * @param event
   *          of type ServletContextEvent
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    log.info("MISO Application Context Destroyed: {}", new Date());
  }
}
