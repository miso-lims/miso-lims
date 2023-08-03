package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleClassDao extends HibernateSaveDao<SampleClass> implements SampleClassDao {

  public HibernateSampleClassDao() {
    super(SampleClass.class, SampleClassImpl.class);
  }

  @Override
  public List<SampleClass> listByCategory(String sampleCategory) {
    @SuppressWarnings("unchecked")
    List<SampleClass> records = currentSession().createCriteria(getEntityClass())
        .add(Restrictions.eq("sampleCategory", sampleCategory))
        .list();
    return records;
  }

  @Override
  public SampleClass getByAlias(String alias) throws IOException {
    return getBy("alias", alias);
  }

  @Override
  public long getUsage(SampleClass sampleClass) throws IOException {
    return getUsageBy(DetailedSampleImpl.class, "sampleClass", sampleClass);
  }

}
