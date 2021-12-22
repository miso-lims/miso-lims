package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleTypeDao extends HibernateSaveDao<SampleType> implements SampleTypeDao {

  public HibernateSampleTypeDao() {
    super(SampleType.class);
  }

  @Override
  public SampleType getByName(String name) throws IOException {
    return getBy("name", name);
  }

  @Override
  public long getUsage(SampleType sampleType) throws IOException {
    return (long) currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("sampleType", sampleType.getName()))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public List<SampleType> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("typeId", ids);
  }

}
