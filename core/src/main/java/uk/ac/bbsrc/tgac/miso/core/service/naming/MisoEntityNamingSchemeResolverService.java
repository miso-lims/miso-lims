package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.7
 */
public class MisoEntityNamingSchemeResolverService implements EntityNamingSchemeResolverService {
  protected static final Logger log = LoggerFactory.getLogger(MisoEntityNamingSchemeResolverService.class);
  private Map<String, MisoNamingScheme<?>> contextMap;

  @Override
  public MisoNamingScheme<?> getNamingScheme(String schemeName) {
    for (MisoNamingScheme scheme : getNamingSchemes()) {
      if (scheme.getSchemeName().equals(schemeName)) {
        try {
          return scheme.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + schemeName + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + schemeName + "'", e);
        }
      }
    }
    log.warn("No scheme called '" + schemeName + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<MisoNamingScheme<?>> getNamingSchemes() {
    // lazily load available schemes
    if (contextMap == null) {
      ServiceLoader<? extends MisoNamingScheme> consumerLoader = ServiceLoader.load(MisoNamingScheme.class);
      Iterator<? extends MisoNamingScheme> consumerIterator = consumerLoader.iterator();

      contextMap = new HashMap<String, MisoNamingScheme<?>>();
      while (consumerIterator.hasNext()) {
        MisoNamingScheme p = consumerIterator.next();

        if (!contextMap.containsKey(p.getSchemeName())) {
          contextMap.put(p.getSchemeName(), p);
        } else {
          if (contextMap.get(p.getSchemeName()) != p) {
            String msg = "Multiple different NamingSchemes with the same scheme name " + "('" + p.getSchemeName()
                + "') are present on the classpath. Scheme names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }

      consumerLoader = ServiceLoader.load(RequestManagerAwareNamingScheme.class);
      consumerIterator = consumerLoader.iterator();

      while (consumerIterator.hasNext()) {
        MisoNamingScheme p = consumerIterator.next();

        if (!contextMap.containsKey(p.getSchemeName())) {
          contextMap.put(p.getSchemeName(), p);
        } else {
          if (contextMap.get(p.getSchemeName()) != p) {
            String msg = "Multiple different NamingSchemes with the same scheme name " + "('" + p.getSchemeName()
                + "') are present on the classpath. Scheme names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }

      log.info("Loaded " + contextMap.values().size() + " known naming schemes");
    }

    return contextMap.values();
  }
}
