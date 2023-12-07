package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.SetJoin;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.DeliverableDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDeliverableDao extends HibernateSaveDao<Deliverable> implements DeliverableDao {

  public HibernateDeliverableDao() {
    super(Deliverable.class);
  }

  @Override
  public long getUsage(Deliverable deliverable) throws IOException {
    LongQueryBuilder<ProjectImpl> queryBuilder = new LongQueryBuilder<>(currentSession(), ProjectImpl.class);
    SetJoin<ProjectImpl, Deliverable> deliverablesJoin =
        queryBuilder.getJoin(queryBuilder.getRoot(), ProjectImpl_.deliverables);
    queryBuilder.addPredicate(queryBuilder.getCriteriaBuilder().equal(deliverablesJoin, deliverable));
    return queryBuilder.getCount();
  }

  @Override
  public List<Deliverable> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("deliverableId", ids);
  }

  @Override
  public Deliverable getByName(String name) throws IOException {
    return getBy("name", name);
  }
}
