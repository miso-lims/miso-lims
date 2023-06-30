package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.persistence.DeliverableDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDeliverableDao extends HibernateSaveDao<Deliverable> implements DeliverableDao {

  public HibernateDeliverableDao() {
    super(Deliverable.class);
  }

  @Override
  public long getUsage(Deliverable deliverable) throws IOException {
    return (long) currentSession().createCriteria(Project.class)
        .add(Restrictions.eq("deliverableId", deliverable.getId())).setProjection(Projections.rowCount())
        .uniqueResult();
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
