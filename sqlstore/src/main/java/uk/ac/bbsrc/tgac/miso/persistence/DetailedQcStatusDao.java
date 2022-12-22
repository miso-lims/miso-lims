package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusDao extends BulkSaveDao<DetailedQcStatus> {

  public DetailedQcStatus getByDescription(String description);

  public long getUsageBySamples(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraries(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraryAliquots(DetailedQcStatus detailedQcStatus);

}