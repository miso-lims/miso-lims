package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.TreeMap;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;

public class PoolOrderCompletionGroup extends TreeMap<HealthType, PoolOrderCompletion> {

  private static final long serialVersionUID = 1L;

  public PoolOrderCompletionGroup() {
    super(HealthType.COMPARATOR);
  }

  public int getRemaining() {
    int remaining = 0;
    for (PoolOrderCompletion completion : values()) {
      remaining += completion.getNumPartitions() * completion.getHealth().getMultiplier();
    }
    return remaining > 0 ? remaining : 0;
  }

  public void add(PoolOrderCompletion item) {
    put(item.getHealth(), item);
  }

  @Override
  public PoolOrderCompletion get(Object key) {
    if (key instanceof String) {
      key = HealthType.get((String) key);
    }
    if (containsKey(key)) {
      return super.get(key);
    } else {
      PoolOrderCompletion empty = new PoolOrderCompletion();
      empty.setHealth((HealthType) key);
      return empty;
    }
  }

}
