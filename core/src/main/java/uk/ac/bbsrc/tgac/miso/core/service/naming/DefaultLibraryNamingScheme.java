package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.7
 */
@ServiceProvider
public class DefaultLibraryNamingScheme implements RequestManagerAwareNamingScheme<Library> {
  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryNamingScheme.class);

  private Map<String, Boolean> allowDuplicateMap = new HashMap<String, Boolean>();
  private Map<String, Pattern> validationMap = new HashMap<String, Pattern>();
  private Map<String, NameGenerator<Library>> customNameGeneratorMap = new HashMap<String, NameGenerator<Library>>();
  private RequestManager requestManager;

  public static final String DEFAULT_NAME_REGEX = "([A-Z]{3})([0-9]+)";
  public static final String DEFAULT_ALIAS_REGEX = "([A-z0-9]+)_L([A-z0-9]+)-([A-Z0-9]+)_(.*)";

  public DefaultLibraryNamingScheme() {
    allowDuplicateMap.put("name", false);
    allowDuplicateMap.put("alias", false);
    validationMap.put("name", Pattern.compile(DEFAULT_NAME_REGEX));
    validationMap.put("alias", Pattern.compile(DEFAULT_ALIAS_REGEX));
  }

  @Override
  public void setValidationRegex(String fieldName, String regex) throws MisoNamingException {
    if (fieldCheck(fieldName) != null) {
      if (validationMap.get(fieldName) != null) {
        log.warn("Setting validation regex from '" + validationMap.get(fieldName).pattern() + "' to '" + regex
            + "'. This usually doesn't happen at " + "runtime unless a custom regex is specified at MISO startup!");
        validationMap.put(fieldName, Pattern.compile(regex));
      }
    } else {
      throw new MisoNamingException("Cannot set validation regex for a field (via 'get" + LimsUtils.capitalise(fieldName)
          + "') that doesn't exist in " + namingSchemeFor().getCanonicalName());
    }
  }

  @Override
  public Class<Library> namingSchemeFor() {
    return Library.class;
  }

  @Override
  public String getSchemeName() {
    return "DefaultLibraryNamingScheme";
  }

  @Override
  public String generateNameFor(String fieldName, Library l) throws MisoNamingException {
    if (customNameGeneratorMap.get(fieldName) != null) {
      NameGenerator<Library> lng = customNameGeneratorMap.get(fieldName);
      String customName = lng.generateName(l);
      if (validateField(fieldName, customName)) {
        return customName;
      } else {
        throw new MisoNamingException("Custom naming generator '" + lng.getGeneratorName() + "' supplied for Library field '" + fieldName
            + "' generated an invalid name according to the validation scheme '" + validationMap.get(fieldName) + "'");
      }
    } else {
      if ("alias".equals(fieldName)) {
        if (l.getSample() != null) {
          Pattern samplePattern = Pattern.compile("([A-z0-9]+)_S([A-z0-9]+)_(.*)");
          Matcher m = samplePattern.matcher(l.getSample().getAlias());

          if (m.matches()) {
            try {
              int numLibs = requestManager.listAllLibrariesBySampleId(l.getSample().getId()).size();
              String la = m.group(1) + "_" + "L" + m.group(2) + "-" + (numLibs + 1) + "_" + m.group(3);
              if (validateField("alias", la)) {
                return la;
              } else {
                throw new MisoNamingException("Generated invalid Library alias for: " + l.toString());
              }
            } catch (IOException e) {
              throw new MisoNamingException("Cannot generate Library alias for: " + l.toString(), e);
            }
          } else {
            throw new MisoNamingException(
                "Cannot generate Library alias for: " + l.toString() + " from supplied sample alias: " + l.getSample().getAlias());
          }
        } else {
          throw new MisoNamingException("This alias generation scheme requires the Library to have a parent Sample set.");
        }
      } else {
        if (validationMap.keySet().contains(fieldName)) {
          Method m = fieldCheck(fieldName);
          if (m != null) {
            log.info("Generating name for '" + fieldName + "' :: " + DefaultMisoEntityPrefix.get(Library.class.getSimpleName()).name()
                + l.getId());
            return DefaultMisoEntityPrefix.get(Library.class.getSimpleName()).name() + l.getId();
          } else {
            throw new MisoNamingException("No such nameable field.");
          }
        } else {
          throw new MisoNamingException("Generation of names on field '" + fieldName + "' not available.");
        }
      }
    }
  }

  @Override
  public String getValidationRegex(String fieldName) throws MisoNamingException {
    Pattern p = validationMap.get(fieldName);
    if (p != null) {
      return validationMap.get(fieldName).pattern();
    } else {
      throw new MisoNamingException("No such field registered for validation");
    }
  }

  @Override
  public boolean validateField(String fieldName, String entityName) throws MisoNamingException {
    if (fieldCheck(fieldName) != null) {
      Pattern p = validationMap.get(fieldName);
      if (p != null) {
        Matcher mat = p.matcher(entityName);
        return mat.matches();
      }
    }
    return false;
  }

  @Override
  public void registerCustomNameGenerator(String fieldName, NameGenerator<Library> libraryNameGenerator) {
    this.customNameGeneratorMap.put(fieldName, libraryNameGenerator);
  }

  @Override
  public void unregisterCustomNameGenerator(String fieldName) {
    this.customNameGeneratorMap.remove(fieldName);
  }

  @Override
  public boolean allowDuplicateEntityNameFor(String fieldName) {
    return fieldCheck(fieldName) != null && allowDuplicateMap.get(fieldName);
  }

  @Override
  public void setAllowDuplicateEntityName(String fieldName, boolean allow) {
    if (allowDuplicateMap.containsKey(fieldName) && fieldCheck(fieldName) != null) {
      allowDuplicateMap.put(fieldName, allow);
    }
  }

  private Method fieldCheck(String fieldName) {
    try {
      return namingSchemeFor().getMethod("get" + LimsUtils.capitalise(fieldName));
    } catch (NoSuchMethodException e) {
      log.error("No such field '" + fieldName + "' on class " + namingSchemeFor().getCanonicalName(), e);
    }
    return null;
  }

  @Override
  public RequestManager getRequestManager() {
    return requestManager;
  }

  @Override
  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
