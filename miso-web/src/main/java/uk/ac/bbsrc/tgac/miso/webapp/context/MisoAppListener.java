package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.management.MalformedObjectNameException;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import io.prometheus.jmx.JmxCollector;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.generation.NameGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.resolvers.NamingSchemeResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.NameValidator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

/**
 * The custom MISO context listener class. On webapp context init, we can do
 * some startup checks,
 * e.g. checking the existence of required directories/files for sane app
 * startup
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
   * @param event of type ServletContextEvent
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    // Export all JVM HotSpot stats to Prometheus
    JvmMetrics.builder().register();

    ServletContext application = event.getServletContext();
    XmlWebApplicationContext context = (XmlWebApplicationContext) WebApplicationContextUtils
        .getRequiredWebApplicationContext(application);

    // resolve property file configuration placeholders
    MisoPropertyExporter exporter = (MisoPropertyExporter) context.getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();

    // load logging system manually so that we can inject the baseDirectory property
    // from the miso properties file into the log4j2 properties file
    System.setProperty("misoBaseDirectory", misoProperties.get("miso.baseDirectory"));
    String configFilePath = application.getRealPath("/") + "/WEB-INF/log4j2.miso.properties";
    try {
      URI uri = new URI(configFilePath);
      Configurator.reconfigure(uri);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Failed to configure logging", e);
    }

    // Set JVM time zone to configured UI time
    String uiZone = getStringPropertyOrNull("miso.timeCorrection.uiZone", misoProperties);
    if (uiZone == null) {
      throw new IllegalArgumentException("miso.timeCorrection.uiZone not set");
    }
    TimeZone.setDefault(TimeZone.getTimeZone(uiZone));

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

    // set headless property so JFreeChart doesn't try to use the X rendering system
    // to generate images
    System.setProperty("java.awt.headless", "true");

    initializeNamingSchemes(context, misoProperties);

    try {
      new JmxCollector(context.getResource("classpath:tomcat-prometheus.yml").getFile()).register();
    } catch (MalformedObjectNameException | IOException e) {
      throw new IllegalStateException("Failed to load Prometheus configuration.", e);
    }
  }

  private void initializeNamingSchemes(XmlWebApplicationContext context, Map<String, String> misoProperties) {
    // set up naming schemes
    NamingSchemeResolverService resolver = (NamingSchemeResolverService) context.getBean("namingSchemeResolverService");
    NamingSchemeHolder schemeHolder = context.getBeanFactory().getBean(NamingSchemeHolder.class);

    schemeHolder.setPrimary(getConfiguredNamingScheme("miso.naming", misoProperties, resolver));
    if (schemeHolder.getPrimary() == null) {
      throw new IllegalArgumentException("No naming scheme configured");
    }
    schemeHolder.setSecondary(getConfiguredNamingScheme("miso.naming2", misoProperties, resolver));
  }

  private NamingScheme getConfiguredNamingScheme(String namespace, Map<String, String> misoProperties,
      NamingSchemeResolverService resolver) {
    String schemeName = getStringPropertyOrNull(namespace + ".scheme", misoProperties);
    if (schemeName == null) {
      return null;
    }
    NamingScheme scheme = resolver.getNamingScheme(schemeName);
    if (scheme == null)
      throw new IllegalArgumentException("Failed to load naming scheme '" + schemeName + "'");
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(scheme);

    applyGenerator(namespace + ".generator.nameable.name", misoProperties, resolver::getNameGenerator,
        scheme::setNameGenerator);
    applyGenerator(namespace + ".generator.sample.alias", misoProperties, resolver::getSampleAliasGenerator,
        scheme::setSampleAliasGenerator);
    applyGenerator(namespace + ".generator.library.alias", misoProperties, resolver::getLibraryAliasGenerator,
        scheme::setLibraryAliasGenerator);
    applyGenerator(namespace + ".generator.libraryaliquot.alias", misoProperties,
        resolver::getLibraryAliquotAliasGenerator,
        scheme::setLibraryAliquotAliasGenerator);

    applyValidator(namespace + ".validator.nameable.name", misoProperties, resolver::getNameValidator,
        scheme::setNameValidator);
    applyValidator(namespace + ".validator.sample.alias", misoProperties, resolver::getSampleAliasValidator,
        scheme::setSampleAliasValidator);
    applyValidator(namespace + ".validator.library.alias", misoProperties, resolver::getLibraryAliasValidator,
        scheme::setLibraryAliasValidator);
    applyValidator(namespace + ".validator.libraryaliquot.alias", misoProperties,
        resolver::getLibraryAliquotAliasValidator,
        scheme::setLibraryAliquotAliasValidator);
    applyValidator(namespace + ".validator.project.code", misoProperties, resolver::getProjectCodeValidator,
        scheme::setProjectCodeValidator);

    return scheme;
  }

  private <T> void applyGenerator(String property, Map<String, String> misoProperties,
      Function<String, NameGenerator<T>> resolver,
      Consumer<NameGenerator<T>> setter) {
    NameGenerator<T> generator = loadComponent(property, misoProperties, resolver);
    if (generator != null) {
      setter.accept(generator);
    }
  }

  private void applyValidator(String property, Map<String, String> misoProperties,
      Function<String, NameValidator> resolver,
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
      throw new IllegalArgumentException(
          "Failed to load name naming scheme component specified for '" + property + "'");
    }
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(component);
    return component;
  }

  /**
   * Called on webapp destruction
   * 
   * @param event of type ServletContextEvent
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    log.info("MISO Application Context Destroyed: {}", new Date());
  }
}
