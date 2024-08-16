package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

public interface SamplePurposeDao extends BulkSaveDao<SamplePurpose> {

  long getUsage(SamplePurpose samplePurpose) throws IOException;

  SamplePurpose getByAlias(String alias) throws IOException;

}
