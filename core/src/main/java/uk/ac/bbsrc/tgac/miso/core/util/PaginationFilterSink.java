package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Date;
import java.util.EnumSet;
import java.util.function.Consumer;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void restrictPaginationByArchived(T item, boolean isArchived, Consumer<String> errorHandler);

  public void restrictPaginationByArrayed(T item, boolean isArrayed, Consumer<String> errorHandler);

  public void restrictPaginationByBox(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByBoxUse(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationByClass(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByDate(T item, Date start, Date end, DateType type, Consumer<String> errorHandler);

  public void restrictPaginationByDistributed(T item, Consumer<String> errorHandler);

  public void restrictPaginationByDistributionRecipient(T item, String recipient, Consumer<String> errorHandler);

  public void restrictPaginationByExternalName(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByFulfilled(T item, boolean isFulfilled, Consumer<String> errorHandler);

  public void restrictPaginationByDraft(T item, boolean isDraft, Consumer<String> errorHandler);

  public void restrictPaginationByGhost(T item, boolean isGhost, Consumer<String> errorHandler);

  public void restrictPaginationByGroupId(T item, String groupId, Consumer<String> errorHandler);

  public void restrictPaginationByHealth(T item, EnumSet<HealthType> healths, Consumer<String> errorHandler);

  public void restrictPaginationById(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationByIndex(T item, String index, Consumer<String> errorHandler);

  public void restrictPaginationByInstitute(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByInstrumentType(T item, InstrumentType type, Consumer<String> errorHandler);

  public void restrictPaginationByKitType(T item, KitType type, Consumer<String> errorHandler);

  public void restrictPaginationByKitName(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByPending(T item, Consumer<String> errorHandler);

  public void restrictPaginationByPlatformType(T item, PlatformType platformType, Consumer<String> errorHandler);

  public void restrictPaginationByPoolId(T item, long poolId, Consumer<String> errorHandler);

  public void restrictPaginationByProjectId(T item, long projectId, Consumer<String> errorHandler);

  public void restrictPaginationByQuery(T item, String query, boolean exact, Consumer<String> errorHandler);

  public void restrictPaginationBySequencerId(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationBySequencingParametersName(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationBySequencingParametersId(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationBySubproject(T item, String query, Consumer<String> errorHandler);

  public void restrictPaginationByUser(T item, String userName, boolean creator, Consumer<String> errorHandler);

  public void restrictPaginationByUserOrGroup(T item, String name, boolean creator, Consumer<String> errorHandler);

  public void restrictPaginationByFreezer(T item, String query, Consumer<String> errorHandler);

}
