package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSamplePurposeDao extends HibernateSaveDao<SamplePurpose> implements SamplePurposeDao {

  public HibernateSamplePurposeDao() {
    super(SamplePurposeImpl.class);
  }

  @Override
  public long getUsage(SamplePurpose samplePurpose) {
    return (long) currentSession().createCriteria(SampleAliquotImpl.class)
        .add(Restrictions.eq("samplePurpose", samplePurpose))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
