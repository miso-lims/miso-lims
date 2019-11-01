package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

  private final Map<K, V> map = new HashMap<>();

  public MapBuilder<K, V> put(K key, V value) {
    map.put(key, value);
    return this;
  }

  public Map<K, V> build() {
    return map;
  }

}
