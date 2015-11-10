package uk.ac.bbsrc.tgac.miso.core.service.naming;

import org.springframework.util.NumberUtils;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

 /**
 * This enum represents the set of prefixes for MISO objects, used in naming schemes
 *
 * @author Rob Davey
 * @date 29/08/12
 * @since 0.1.7
 */
public enum DefaultMisoEntityPrefix {
   EMP("emPCR"),
   EDI("emPCRDilution"),
   EPO("Pool"),
   EXP("Experiment"),
   LIB("Library"),
   LDI("LibraryDilution"),
   PLA("Plate"),
   PRO("Project"),
   UPO("Pool"),
   RUN("Run"),
   SAM("Sample"),
   SPC("SequencerPartitionContainer"),
   STU("Study"),
   IPO("Pool"),
   SUB("Submission"),
   WKF("Workflow"),
   WKP("WorkflowProcess");

  /**
   * Field key
   */
  private String key;
  /**
   * Field lookup
   */
  private static final Map<String, DefaultMisoEntityPrefix> lookup = new HashMap<String, DefaultMisoEntityPrefix>();
  private static final Map<String, DefaultMisoEntityPrefix> names = new HashMap<String, DefaultMisoEntityPrefix>();

  static {
    for (DefaultMisoEntityPrefix s : EnumSet.allOf(DefaultMisoEntityPrefix.class)) {
      lookup.put(s.getKey(), s);
      names.put(s.toString(), s);
    }
  }

  /**
   * Constructs a DefaultMisoEntityPrefix based on a given key
   *
   * @param key of type String
   */
  DefaultMisoEntityPrefix(String key) {
    this.key = key;
  }

  /**
   * Returns a DefaultMisoEntityPrefix given an enum key
   *
   * @param key of type String
   * @return DefaultMisoEntityPrefix
   */
  public static DefaultMisoEntityPrefix get(String key) {
    return lookup.get(key);
  }

  public static DefaultMisoEntityPrefix getByName(String name) {
    return names.get(name);
  }

  /**
   * Returns the key of this DefaultMisoEntityPrefix enum.
   *
   * @return String key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Returns the keys of this DefaultMisoEntityPrefix enum.
   *
   * @return ArrayList<String> keys.
   */
  public static ArrayList<String> getKeys() {
    ArrayList<String> keys = new ArrayList<String>();
    for (DefaultMisoEntityPrefix h : DefaultMisoEntityPrefix.values()) {
      keys.add(h.getKey());
    }
    return keys;
  }

  public static <T> T getMisoObjectByName(RequestManager requestManager, String entityName, Long id) throws MisoNamingException {
    String prefix = (entityName).substring(0, 3);
    String ident = (entityName).substring(3);
    long parsedId = NumberUtils.parseNumber(ident, Long.class);
    DefaultMisoEntityPrefix p = DefaultMisoEntityPrefix.getByName(prefix);
    try {
      if (p != null && parsedId == id) {
        Method m = RequestManager.class.getDeclaredMethod("get"+ LimsUtils.capitalise(p.getKey())+"ById", Long.TYPE);
        return id != null ? (T)m.invoke(requestManager, id) : null;
      }
      else {
        throw new MisoNamingException("Failed to resolve entity group element with identifier: " + prefix+ident);
      }
    }
    catch (NoSuchMethodException e) {
      throw new MisoNamingException("Failed to find method get"+p.getKey()+"ById on entity group element " + prefix+ident, e);
    }
    catch (InvocationTargetException e) {
      throw new MisoNamingException("Failed to call method get"+p.getKey()+"ById on entity group element " + prefix+ident, e);
    }
    catch (IllegalAccessException e) {
      throw new MisoNamingException("Failed to resolve entity group element " + prefix+ident, e);
    }
  }
}
