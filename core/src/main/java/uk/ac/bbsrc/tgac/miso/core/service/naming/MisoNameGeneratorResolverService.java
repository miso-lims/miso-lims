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
public class MisoNameGeneratorResolverService implements NameGeneratorResolverService {
  protected static final Logger log = LoggerFactory.getLogger(MisoNameGeneratorResolverService.class);
  private Map<String, NameGenerator<?>> contextMap;

  @Override
  public NameGenerator<?> getNameGenerator(String generatorName) {
    for (NameGenerator generator : getNameGenerators()) {
      if (generator.getGeneratorName().equals(generatorName)) {
        try {
          return generator.getClass().newInstance();
        } catch (InstantiationException e) {
          log.error("Cannot create a new instance of '" + generatorName + "'", e);
        } catch (IllegalAccessException e) {
          log.error("Cannot create a new instance of '" + generatorName + "'", e);
        }
      }
    }
    log.warn("No name generator called '" + generatorName + "' was available on the classpath");
    return null;
  }

  @Override
  public Collection<NameGenerator<?>> getNameGenerators() {
    // lazily load available generators
    if (contextMap == null) {
      ServiceLoader<? extends NameGenerator> consumerLoader = ServiceLoader.load(NameGenerator.class);
      Iterator<? extends NameGenerator> consumerIterator = consumerLoader.iterator();

      contextMap = new HashMap<String, NameGenerator<?>>();
      while (consumerIterator.hasNext()) {
        NameGenerator p = consumerIterator.next();

        if (!contextMap.containsKey(p.getGeneratorName())) {
          contextMap.put(p.getGeneratorName(), p);
        } else {
          if (contextMap.get(p.getGeneratorName()) != p) {
            String msg = "Multiple different Name Generators with the same scheme name " + "('" + p.getGeneratorName()
                + "') are present on the classpath. Generator names must be unique.";
            log.error(msg);
            throw new ServiceConfigurationError(msg);
          }
        }
      }

      log.info("Loaded " + contextMap.values().size() + " known name generators");
    }

    return contextMap.values();
  }
}
