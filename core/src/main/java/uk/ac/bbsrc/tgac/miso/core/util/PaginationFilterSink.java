package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void setFulfilled(T item, boolean isFulfilled);

  public void setPlatformType(T item, PlatformType platformType);

  public void setPoolId(T item, long poolId);

  public void setProjectId(T item, long projectId);

  public void setQuery(T item, String query);
}
