package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.util.Collection;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since version
 */
public interface EntityNamingSchemeResolverService {
  MisoNamingScheme<?> getNamingScheme(String schemeName);

  Collection<MisoNamingScheme<?>> getNamingSchemes();
}