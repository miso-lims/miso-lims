package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleTypeDao extends HibernateSaveDao<SampleType> implements SampleTypeDao {

  public HibernateSampleTypeDao() {
    super(SampleType.class);
  }

  @Override
  public SampleType getByName(String name) throws IOException {
    return getBy(SampleType_.NAME, name);
  }

  @Override
  public long getUsage(SampleType sampleType) throws IOException {
    LongQueryBuilder<SampleImpl> builder = new LongQueryBuilder<>(currentSession(), SampleImpl.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.sampleType), sampleType.getName()));
    return builder.getCount();
  }

  @Override
  public List<SampleType> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(SampleType_.TYPE_ID, ids);
  }

}
