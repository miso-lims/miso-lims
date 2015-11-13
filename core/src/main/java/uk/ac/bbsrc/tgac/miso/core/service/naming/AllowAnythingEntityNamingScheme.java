package uk.ac.bbsrc.tgac.miso.core.service.naming;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.core.service.naming
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/09/12
 * @since 0.1.8
 */
@ServiceProvider
public class AllowAnythingEntityNamingScheme<T extends Nameable> implements MisoNamingScheme<T> {
  protected static final Logger log = LoggerFactory.getLogger(AllowAnythingEntityNamingScheme.class);

  private Class<T> type;
  private Map<String, Pattern> validationMap = new HashMap<String, Pattern>();
  private Map<String, NameGenerator<T>> customNameGeneratorMap = new HashMap<String, NameGenerator<T>>();

  public AllowAnythingEntityNamingScheme() {
    try {
      type = (Class<T>) Class.forName("uk.ac.bbsrc.tgac.miso.core.data.Nameable");
      validationMap.put("name", Pattern.compile("(.*)"));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public AllowAnythingEntityNamingScheme(Class<T> type) {
    this.type = type;
    validationMap.put("name", Pattern.compile("(.*)"));
  }

  @Override
  public void setValidationRegex(String fieldName, String regex) throws MisoNamingException {
    if (fieldCheck(fieldName)) {
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
  public Class<T> namingSchemeFor() {
    return type;
  }

  public void setNamingSchemeFor(Class<T> type) {
    this.type = type;
  }

  @Override
  public String getSchemeName() {
    return "AllowAnythingEntityNamingScheme";
  }

  @Override
  public String generateNameFor(String fieldName, T o) throws MisoNamingException {
    if (customNameGeneratorMap.get(fieldName) != null) {
      NameGenerator<T> lng = customNameGeneratorMap.get(fieldName);
      String customName = lng.generateName(o);
      if (validateField(fieldName, customName)) {
        return customName;
      } else {
        throw new MisoNamingException("Custom naming generator '" + lng.getGeneratorName() + "' supplied for entity field '" + fieldName
            + "' generated an invalid name according to the validation scheme '" + validationMap.get(fieldName) + "'");
      }
    } else {
      log.info("Generating name for " + o.getClass().getSimpleName() + " :: " + o.getId());
      return "" + o.getId();
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
    return !"".equals(entityName) && fieldCheck(fieldName);
  }

  @Override
  public void registerCustomNameGenerator(String fieldName, NameGenerator<T> nameGenerator) {
    this.customNameGeneratorMap.put(fieldName, nameGenerator);
  }

  @Override
  public void unregisterCustomNameGenerator(String fieldName) {
    this.customNameGeneratorMap.remove(fieldName);
  }

  @Override
  public boolean allowDuplicateEntityNameFor(String fieldName) {
    // if the field exists, then duplicates are allowed
    return fieldCheck(fieldName);
  }

  @Override
  public void setAllowDuplicateEntityName(String fieldName, boolean allow) {
    log.error("All duplicate names are allowed for all fields in this scheme.");
  }

  private boolean fieldCheck(String fieldName) {
    try {
      Method m = namingSchemeFor().getMethod("get" + LimsUtils.capitalise(fieldName));
      if (m != null) {
        return true;
      }
    } catch (NoSuchMethodException e) {
      log.error("No such field '" + fieldName + "' on class " + namingSchemeFor().getCanonicalName());
      e.printStackTrace();
    }
    return false;
  }
}