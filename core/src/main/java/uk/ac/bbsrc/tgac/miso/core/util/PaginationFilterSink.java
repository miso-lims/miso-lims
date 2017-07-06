package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Date;
import java.util.EnumSet;
import java.util.function.Consumer;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void restrictPaginationByArchived(T item, boolean isArchived, Consumer<String> errorHandler);

  public void restrictPaginationByBox(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByBoxUse(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationByClass(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByDate(T item, Date start, Date end, DateType type, Consumer<String> errorHandler);

  public void restrictPaginationByExternalName(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByFulfilled(T item, boolean isFulfilled, Consumer<String> errorHandler);

  public void restrictPaginationByHealth(T item, EnumSet<HealthType> healths, Consumer<String> errorHandler);

  public void restrictPaginationByIndex(T item, String index, Consumer<String> errorHandler);

  public void restrictPaginationByInstitute(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByKitType(T item, KitType type, Consumer<String> errorHandler);

  public void restrictPaginationByPlatformType(T item, PlatformType platformType, Consumer<String> errorHandler);

  public void restrictPaginationByPoolId(T item, long poolId, Consumer<String> errorHandler);

  public void restrictPaginationByProjectId(T item, long projectId, Consumer<String> errorHandler);

  public void restrictPaginationByQuery(T item, String query, Consumer<String> errorHandler);

  public void restrictPaginationBySequencerId(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationByUser(T item, String userName, boolean creator, Consumer<String> errorHandler);

}
