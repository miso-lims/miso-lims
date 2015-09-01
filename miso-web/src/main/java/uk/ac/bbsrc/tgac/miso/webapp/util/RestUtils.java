package uk.ac.bbsrc.tgac.miso.webapp.util;

import net.sf.json.JSONObject;

/**
 * Class for producing simple non-URL-encoded error messages for REST APIs
 *
 * @author Rob Davey
 * @date 19/08/15
 * @since 0.2.1-SNAPSHOT
 */
public class RestUtils {
  public static JSONObject error(String error, String objectKey, String objectValue) {
    JSONObject o = new JSONObject();
    o.put("error", error);
    o.put(objectKey, objectValue);
    return o;
  }

  public static JSONObject objectify(Object obj) {
    if (obj != null) return JSONObject.fromObject(obj);
    return new JSONObject();
  }

  public static String stringify(Object obj) {
    if (obj != null) return objectify(obj).toString();
    return "{}";
  }
}
