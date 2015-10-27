package uk.ac.bbsrc.tgac.miso.core.service.naming;

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.7
 */
@Spi
public interface MisoNamingScheme<T> {
  Class<T> namingSchemeFor();

  String getSchemeName();

  String generateNameFor(String field, T t) throws MisoNamingException;

  void setValidationRegex(String fieldName, String validationRegex) throws MisoNamingException;

  String getValidationRegex(String fieldName) throws MisoNamingException;

  boolean validateField(String fieldName, String entityName) throws MisoNamingException;

  void registerCustomNameGenerator(String fieldName, NameGenerator<T> nameGenerator);

  void unregisterCustomNameGenerator(String fieldName);

  boolean allowDuplicateEntityNameFor(String fieldName);

  void setAllowDuplicateEntityName(String fieldName, boolean allow);
}