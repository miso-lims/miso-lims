package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;

public enum PoolProperty {

  ALIAS("Alias") {
    @Override
    public String extract(Pool pool) {
      return pool.getAlias();
    }
  };

  private final String label;

  private PoolProperty(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract String extract(Pool pool);

}
