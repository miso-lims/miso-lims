package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface SamplePurposeDao extends BulkSaveDao<SamplePurpose> {

  long getUsage(SamplePurpose samplePurpose) throws IOException;

  SamplePurpose getByAlias(String alias) throws IOException;

}