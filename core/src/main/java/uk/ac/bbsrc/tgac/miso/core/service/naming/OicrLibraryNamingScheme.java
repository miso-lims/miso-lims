package uk.ac.bbsrc.tgac.miso.core.service.naming;

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

@ServiceProvider
public class OicrLibraryNamingScheme implements RequestManagerAwareNamingScheme<Library> {
  protected static final Logger log = LoggerFactory.getLogger(OicrLibraryNamingScheme.class);

  private final Map<String, Boolean> allowDuplicateMap = new HashMap<String, Boolean>();
  private final Map<String, Pattern> validationMap = new HashMap<String, Pattern>();
  private final Map<String, NameGenerator<Library>> customNameGeneratorMap = new HashMap<String, NameGenerator<Library>>();
  private RequestManager requestManager;

  public static final String DEFAULT_NAME_REGEX = "([A-Z]{3})([0-9]+)";
  public static final String DEFAULT_ALIAS_REGEX = "([A-Z\\d]{3,5})_([0-9]{3,4}|[0-9][CR][0-9]{1,2})_(nn|[A-Z]{1}[a-z]{1})_([nRPXMCFETO])_(SE|PE|MP|\\?\\?)_(nn|\\d{2,4}|\\dK)_(TS|EX|CH|BS|WG|TR|WT|SM|MR|\\?\\?)";

  public OicrLibraryNamingScheme() {
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
    return OicrLibraryNamingScheme.class.getSimpleName();
  }

  @Override
  public String generateNameFor(String fieldName, Library l) throws MisoNamingException {
    log.info("inside generateNameFor " + fieldName + " " + l.getAlias());
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
          Pattern samplePattern = Pattern.compile(OicrSampleNamingScheme.ALIAS_REGEX);
          Matcher m = samplePattern.matcher(l.getSample().getAlias());

          if (m.matches()) {
            // try {
            // int numLibs = requestManager.listAllLibrariesBySampleId(l.getSample().getId()).size();
            String libraryType = "??";
            if (l.getLibraryType().getDescription().equals("Paired End")) {
              libraryType = "PE";
            } else if (l.getLibraryType().getDescription().equals("Single End")) {
              libraryType = "SE";
            }
            String sourceTemplateType = "??";
            if (l.getLibraryStrategyType().getName().equals("WGS")) {
              sourceTemplateType = "WG";
            } else if (l.getLibraryStrategyType().getName().equals("AMPLICON")) {
              sourceTemplateType = "TS";
            }
            String estimateInsertSize = "???";
            estimateInsertSize = "300";
            StringBuilder sb = new StringBuilder();
            sb.append(m.group(1)); // PCSI (project name)
            sb.append("_").append(m.group(2)); // 0123 (patient number)
            sb.append("_").append(m.group(3)); // Pa (tissue origin)
            sb.append("_").append(m.group(4)); // R (tissue type)
            sb.append("_").append(libraryType); // PE (library type)
            sb.append("_").append(estimateInsertSize); // 300 (estimated insert size)
            sb.append("_").append(sourceTemplateType); // WG (source template type)
            String libraryAlias = sb.toString();
            // String la = m.group(1) + "_" + m.group(2) + "-" + (numLibs + 1) + "_" + m.group(3);
            if (validateField("alias", libraryAlias)) {
              return libraryAlias;
            } else {
              throw new MisoNamingException("Generated invalid Library alias " + libraryAlias + " for: " + l.toString());
            }
            // } catch (IOException e) {
            // throw new MisoNamingException("Cannot generate Library alias for: " + l.toString(), e);
            // }
          } else {
            throw new MisoNamingException("Cannot generate Library alias for: " + l.toString() + " from supplied sample alias: "
                + l.getSample().getAlias());
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
    log.info("validateField");
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
      log.error("No such field '" + fieldName + "' on class " + namingSchemeFor().getCanonicalName());
      e.printStackTrace();
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