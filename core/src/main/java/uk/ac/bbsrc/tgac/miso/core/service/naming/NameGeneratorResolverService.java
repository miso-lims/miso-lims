package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.util.Collection;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.8
 */
public interface NameGeneratorResolverService {
  NameGenerator<?> getNameGenerator(String generatorName);

  Collection<NameGenerator<?>> getNameGenerators();
}