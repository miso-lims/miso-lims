package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Date;
import java.util.EnumSet;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void restrictPaginationByClass(T item, String name);

  public void restrictPaginationByDate(T item, Date start, Date end, DateType type);

  public void restrictPaginationByFulfilled(T item, boolean isFulfilled);

  public void restrictPaginationByHealth(T item, EnumSet<HealthType> healths);

  public void restrictPaginationByIndex(T item, String index);

  public void restrictPaginationByPlatformType(T item, PlatformType platformType);

  public void restrictPaginationByPoolId(T item, long poolId);

  public void restrictPaginationByProjectId(T item, long projectId);

  public void restrictPaginationByQuery(T item, String query);

  public void restrictPaginationByUser(T item, String userName, boolean creator);

}
