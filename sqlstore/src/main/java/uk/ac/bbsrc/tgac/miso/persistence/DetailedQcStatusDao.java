package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;

public interface DetailedQcStatusDao {

  public List<DetailedQcStatus> list();

  public DetailedQcStatus get(Long id);

  public DetailedQcStatus getByDescription(String description);

  public long create(DetailedQcStatus detailedQcStatus);

  public long update(DetailedQcStatus detailedQcStatus);

  public long getUsageBySamples(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraries(DetailedQcStatus detailedQcStatus);

  public long getUsageByLibraryAliquots(DetailedQcStatus detailedQcStatus);

}