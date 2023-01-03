package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;

public interface LabDao extends BulkSaveDao<Lab> {
  
  public Lab getByAlias(String alias) throws IOException;

  /**
   * Check how many Samples (DetailedSample Tissues only) reference this lab
   * 
   * @param lab the Lab to check usage of
   * @return the number of Samples referencing the Lab
   */
  public long getUsageByTissues(Lab lab);

  public long getUsageByTransfers(Lab lab);

}
