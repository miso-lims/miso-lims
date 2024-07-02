package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleClassDao extends HibernateSaveDao<SampleClass> implements SampleClassDao {

  public HibernateSampleClassDao() {
    super(SampleClass.class, SampleClassImpl.class);
  }

  @Override
  public List<SampleClass> listByCategory(String sampleCategory) {
    QueryBuilder<SampleClass, SampleClassImpl> builder =
        new QueryBuilder<>(currentSession(), SampleClassImpl.class, SampleClass.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleClassImpl_.sampleCategory), sampleCategory));
    return builder.getResultList();
  }

  @Override
  public SampleClass getByAlias(String alias) throws IOException {
    return getBy(SampleClassImpl_.ALIAS, alias);
  }

  @Override
  public long getUsage(SampleClass sampleClass) throws IOException {
    return getUsageBy(DetailedSampleImpl.class, DetailedSampleImpl_.SAMPLE_CLASS, sampleClass);
  }

}
