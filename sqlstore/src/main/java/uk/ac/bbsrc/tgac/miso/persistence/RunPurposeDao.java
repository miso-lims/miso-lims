package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;

public interface RunPurposeDao extends SaveDao<RunPurpose> {

  public RunPurpose getByAlias(String alias);

  public long getUsageByPoolOrders(RunPurpose purpose);

  public long getUsageBySequencingOrders(RunPurpose purpose);

}
