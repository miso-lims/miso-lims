package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import com.eaglegenomics.simlims.core.Group;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface PaginationFilterSink<T> {

  public void restrictPaginationByArchived(T item, boolean isArchived, Consumer<String> errorHandler);

  public void restrictPaginationByArrayed(T item, boolean isArrayed, Consumer<String> errorHandler);

  public void restrictPaginationByBatchId(T item, String batchId, Consumer<String> errorHandler);

  public void restrictPaginationByBox(T item, String name, Consumer<String> errorHandler);

  public void restrictPaginationByBoxType(T item, BoxType boxType, Consumer<String> errorHandler);

  public void restrictPaginationByBoxUse(T item, long id, Consumer<String> errorHandler);

  public void restrictPaginationByCategory(T item, SopCategory category, Consumer<String> errorHandler);

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

  public void restrictPaginationByIds(T item, List<Long> ids, Consumer<String> errorHandler);

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

  public void restrictPaginationByRequisitionId(T item, String requisitionId, Consumer<String> errorHandler);

  public void restrictPaginationByRecipientGroups(T item, Collection<Group> groups, Consumer<String> errorHandler);

  public void restrictPaginationByTransferType(T item, TransferType transferType, Consumer<String> errorHandler);

  public void restrictPaginationByTissueOrigin(T item, String origin, Consumer<String> errorHandler);

  public void restrictPaginationByTissueType(T item, String type, Consumer<String> errorHandler);

  public void restrictPaginationByWorksetId(T item, long worksetId, Consumer<String> errorHandler);

}
