package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;

public class Pair<K, V> implements Entry<K, V> {
  public static <T> Function<T, Pair<Integer, T>> number(int start) {
    return new Function<T, Pair<Integer, T>>() {
      private int current = start;

      @Override
      public Pair<Integer, T> apply(T t) {
        return new Pair<>(current++, t);
      }
    };

  }

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

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof Pair))
      return false;
    @SuppressWarnings("unchecked")
    Pair<K, V> po = (Pair<K, V>) o;
    return Objects.equals(this.key, po.key)
        && Objects.equals(this.value, po.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.key, this.value);
  }
}
