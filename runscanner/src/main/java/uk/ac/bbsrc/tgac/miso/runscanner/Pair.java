package uk.ac.bbsrc.tgac.miso.runscanner;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V> {
  private final K key;
  private final V value;

  public Pair(K key, V value) {
    super();
    this.key = key;
    this.value = value;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(V arg0) {
    throw new UnsupportedOperationException("Pairs are immutable.");
  }

}
