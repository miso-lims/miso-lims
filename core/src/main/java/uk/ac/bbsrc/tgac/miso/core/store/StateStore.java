package uk.ac.bbsrc.tgac.miso.core.store;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: davey
 * Date: 13/05/2014
 */
public interface StateStore extends Store<JSONObject> {
  public Set<String> listAllKeys() throws IOException;
  public Map<Long, String> listStateKeysBySearch(String str) throws IOException;

  public boolean validateKeys(Set<String> keys) throws IOException;
  public long getIdForKey(String key) throws IOException;

  public String getKey(long keyId) throws IOException;
  public String getValue(long valueId) throws IOException;

  public long saveKey(String key) throws IOException;
  public long saveValue(String value) throws IOException;
  public long updateValue(long valueId, String value) throws IOException;
  public List<Map<Long, Long>> saveAll(JSONObject jsonObject) throws IOException;
}
