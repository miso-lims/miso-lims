package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTissueOriginDao extends HibernateSaveDao<TissueOrigin> implements TissueOriginDao {

  public HibernateTissueOriginDao() {
    super(TissueOriginImpl.class);
  }

  @Override
  public TissueOrigin getByAlias(String alias) {
    Criteria criteria = currentSession().createCriteria(TissueOrigin.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (TissueOrigin) criteria.uniqueResult();
  }

  @Override
  public long getUsage(TissueOrigin tissueOrigin) {
    return (long) currentSession().createCriteria(SampleTissueImpl.class)
        .add(Restrictions.eqOrIsNull("tissueOrigin", tissueOrigin))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
