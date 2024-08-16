package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;

public interface RunPurposeDao extends BulkSaveDao<RunPurpose> {

  RunPurpose getByAlias(String alias);

  long getUsageByPoolOrders(RunPurpose purpose);

  long getUsageBySequencingOrders(RunPurpose purpose);

}
