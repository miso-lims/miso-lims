package uk.ac.bbsrc.tgac.miso.core.service.naming;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.fluxion.spi.ServiceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@ServiceProvider
public class OicrSampleNamingScheme implements RequestManagerAwareNamingScheme<Sample> {
  protected static final Logger log = LoggerFactory.getLogger(OicrSampleNamingScheme.class);

  private static final String IDENTITY_REGEX_PART = "([A-Z\\d]{3,5})_(\\d{3,5})";
  private static final String TISSUE_ORIGIN_REGEX = "(Ad|Ap|Ag|Bm|Bn|Br|Bu|Cb|Cn|Du|Es|Fs|Gb|Hr|Ki|Le|Li|Ln|Lu|Lv|Lx|Ly|Md|Me|Nk|Oc|Om|Ov|Pa|Pb|Pr|Sa|Si|Sk|Sm|Sp|St|Ta|Tr|Mu|Wm|nn)";
  private static final String TISSUE_TYPE_REGEX = "[BRPXMCFESATOn]";
  private static final String TISSUE_REGEX_PART = TISSUE_ORIGIN_REGEX+"_"+TISSUE_TYPE_REGEX+"_(nn|\\d{2})_(\\d{1,2})-(\\d{1,2})";
  private static final String ANALYTE_REGEX_PART = "(C|CV|HE|LCM|D_S|R_S|D_|R_(\\d+_(MR|SM|WT)_)?)\\d+";
  
  public static final String NAME_REGEX = "^([A-Z]{3})([0-9]+)";
  public static final String ALIAS_REGEX = "^" + IDENTITY_REGEX_PART + "(_" + TISSUE_REGEX_PART + "(_" + ANALYTE_REGEX_PART  + ")?)?$";

  private final Map<String, Boolean> allowDuplicateMap = new HashMap<String, Boolean>();
  private final Map<String, Pattern> validationMap = new HashMap<String, Pattern>();
  private final Map<String, NameGenerator<Sample>> customNameGeneratorMap = new HashMap<String, NameGenerator<Sample>>();
  private RequestManager requestManager;

  public OicrSampleNamingScheme() {
    allowDuplicateMap.put("name", false);
    allowDuplicateMap.put("alias", false);
    validationMap.put("name", Pattern.compile(NAME_REGEX));
    validationMap.put("alias", Pattern.compile(ALIAS_REGEX));
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
  public Class<Sample> namingSchemeFor() {
    return Sample.class;
  }

  @Override
  public String getSchemeName() {
    return "OicrSampleNamingScheme";
  }

  @Override
  public String generateNameFor(String fieldName, Sample s) throws MisoNamingException {
    if (customNameGeneratorMap.get(fieldName) != null) {
      NameGenerator<Sample> sng = customNameGeneratorMap.get(fieldName);
      String customName = sng.generateName(s);
      if (validateField(fieldName, customName)) {
        return customName;
      } else {
        throw new MisoNamingException("Custom naming generator '" + sng.getGeneratorName() + "' supplied for Sample field '" + fieldName
            + "' generated an invalid name according to the validation scheme '" + validationMap.get(fieldName) + "' (generated name: " 
            + customName + ")");
      }
    } else {
      if ("alias".equals(fieldName)) {
        throw new MisoNamingException("Alias generation not available. Validation via validateName is available.");
      } else {
        if (validationMap.keySet().contains(fieldName)) {
          Method m = fieldCheck(fieldName);
          if (m != null) {
            log.info("Generating name for '" + fieldName + "' :: " + DefaultMisoEntityPrefix.get(Sample.class.getSimpleName()).name()
                + s.getId());
            return DefaultMisoEntityPrefix.get(Sample.class.getSimpleName()).name() + s.getId();
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
  public void registerCustomNameGenerator(String fieldName, NameGenerator<Sample> sampleNameGenerator) {
    this.customNameGeneratorMap.put(fieldName, sampleNameGenerator);
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