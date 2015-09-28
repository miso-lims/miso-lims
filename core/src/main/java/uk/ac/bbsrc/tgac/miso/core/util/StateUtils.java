package uk.ac.bbsrc.tgac.miso.core.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 04/07/14
 * @since version
 */
public class StateUtils {
  public static Set<String> getKeysFromState(JSONObject state) {
    JSONArray fields = state.getJSONArray("state");
    Set<String> keys = new HashSet<>();
    if (fields != null) {
      for (JSONObject j : (Iterable<JSONObject>)fields) {
        String key = j.getString("key");
        keys.add(key);
      }
    }
    return keys;
  }
}
