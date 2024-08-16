package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusDao extends BulkSaveDao<DetailedQcStatus> {

  public DetailedQcStatus getByDescription(String description);

  public long getUsageBySamples(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraries(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraryAliquots(DetailedQcStatus detailedQcStatus);

}
