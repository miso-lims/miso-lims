package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class DilutionPaginationFilter extends PaginationFilter {
  private PlatformType platformType;

  private Long poolId;

  public PlatformType getPlatformType() {
    return platformType;
  }

  public Long getPoolId() {
    return poolId;
  }

  public void setPlatformType(PlatformType platformType) {
    this.platformType = platformType;
  }

  public void setPool(Pool pool) {
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      throw new IllegalArgumentException("Cannot search for unsaved pool.");
    }
    poolId = pool.getId();
  }

  public void setPoolId(Long poolId) {
    this.poolId = poolId;
  }

}
