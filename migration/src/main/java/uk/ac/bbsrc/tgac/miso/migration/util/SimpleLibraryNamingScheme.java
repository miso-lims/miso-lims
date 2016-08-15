package uk.ac.bbsrc.tgac.miso.migration.util;

import java.util.HashMap;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NameGenerator;

/**
 * Simple naming scheme for Libraries. By default uses SimpleLibraryNameGenerator, does not allow
 * duplicate name or alias, and performs no validation. This configuration may be modified
 */
public class SimpleLibraryNamingScheme implements MisoNamingScheme<Library> {

  Map<String, NameGenerator<Library>> nameGenerators = new HashMap<>();
  Map<String, String> validationMap = new HashMap<>();
  Map<String, Boolean> allowDuplicatesMap = new HashMap<>();
  
  public SimpleLibraryNamingScheme() {
    allowDuplicatesMap.put("name", false);
    allowDuplicatesMap.put("alias", false);
    nameGenerators.put("name", new SimpleLibraryNameGenerator());
  }
  
  @Override
  public Class<Library> namingSchemeFor() {
    return Library.class;
  }

  @Override
  public String getSchemeName() {
    return getClass().getSimpleName();
  }

  @Override
  public String generateNameFor(String field, Library library) throws MisoNamingException {
    NameGenerator<Library> generator = nameGenerators.get(field);
    if (generator == null) throw new MisoNamingException("No name generator for field " + field);
    return generator.generateName(library);
  }

  @Override
  public void setValidationRegex(String fieldName, String validationRegex) throws MisoNamingException {
    validationMap.put(fieldName, validationRegex);
  }

  @Override
  public String getValidationRegex(String fieldName) throws MisoNamingException {
    return validationMap.get(fieldName);
  }

  @Override
  public boolean validateField(String fieldName, String entityName) throws MisoNamingException {
    String regex = validationMap.get(fieldName);
    return regex == null ? true : entityName.matches(regex);
  }

  @Override
  public void registerCustomNameGenerator(String fieldName, NameGenerator<Library> nameGenerator) {
    nameGenerators.put(fieldName, nameGenerator);
  }

  @Override
  public void unregisterCustomNameGenerator(String fieldName) {
    nameGenerators.remove(fieldName);
  }

  @Override
  public boolean allowDuplicateEntityNameFor(String fieldName) {
    return allowDuplicatesMap.containsKey(fieldName) ? allowDuplicatesMap.get(fieldName) : false;
  }

  @Override
  public void setAllowDuplicateEntityName(String fieldName, boolean allow) {
    allowDuplicatesMap.put(fieldName, allow);
  }

  @Override
  public boolean hasGeneratorFor(String fieldName) {
    return nameGenerators.containsKey(fieldName);
  }

}
