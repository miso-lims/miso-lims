package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSamplePurposeDao extends HibernateSaveDao<SamplePurpose> implements SamplePurposeDao {

  public HibernateSamplePurposeDao() {
    super(SamplePurpose.class, SamplePurposeImpl.class);
  }

  @Override
  public long getUsage(SamplePurpose samplePurpose) {
    return getUsageBy(SampleAliquotImpl.class, SampleAliquotImpl_.SAMPLE_PURPOSE, samplePurpose);
  }

  @Override
  public SamplePurpose getByAlias(String alias) throws IOException {
    return getBy(SamplePurposeImpl_.ALIAS, alias);
  }

  @Override
  public List<SamplePurpose> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(SamplePurposeImpl_.SAMPLE_PURPOSE_ID, ids);
  }

}
