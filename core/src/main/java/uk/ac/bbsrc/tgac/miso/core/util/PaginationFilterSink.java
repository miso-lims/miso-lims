package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Date;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void restrictPaginationByFulfilled(T item, boolean isFulfilled);

  public void restrictPaginationByPlatformType(T item, PlatformType platformType);

  public void restrictPaginationByPoolId(T item, long poolId);

  public void restrictPaginationByProjectId(T item, long projectId);

  public void restrictPaginationByQuery(T item, String query);

  public void restrictPaginationByUser(T item, String userName, boolean creator);

  public void restrictPaginationByDate(T item, Date start, Date end, boolean creation);
}
