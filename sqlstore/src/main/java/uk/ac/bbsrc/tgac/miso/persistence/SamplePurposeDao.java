package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface SamplePurposeDao extends SaveDao<SamplePurpose> {

  long getUsage(SamplePurpose samplePurpose) throws IOException;

  SamplePurpose getByAlias(String alias) throws IOException;

  List<SamplePurpose> listByIdList(Collection<Long> ids) throws IOException;

}