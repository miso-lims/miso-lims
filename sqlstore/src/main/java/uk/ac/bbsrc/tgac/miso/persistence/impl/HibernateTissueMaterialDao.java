package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueMaterialDao extends HibernateSaveDao<TissueMaterial> implements TissueMaterialDao {

  public HibernateTissueMaterialDao() {
    super(TissueMaterialImpl.class);
  }

  @Override
  public long getUsage(TissueMaterial tissueMaterial) {
    return (long) currentSession().createCriteria(SampleTissueImpl.class)
        .add(Restrictions.eqOrIsNull("tissueMaterial", tissueMaterial))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
