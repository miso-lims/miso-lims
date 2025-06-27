package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable_;
import uk.ac.bbsrc.tgac.miso.persistence.DeliverableCategoryDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateDeliverableCategoryDao extends HibernateSaveDao<DeliverableCategory>
    implements DeliverableCategoryDao {

  public HibernateDeliverableCategoryDao() {
    super(DeliverableCategory.class);
  }

  @Override
  public DeliverableCategory getByName(String name) throws IOException {
    return getBy(DeliverableCategory_.name, name);
  }

  @Override
  public long getUsage(DeliverableCategory category) throws IOException {
    return getUsageBy(Deliverable.class, Deliverable_.category, category);
  }

  @Override
  public List<DeliverableCategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(DeliverableCategory_.categoryId, ids);
  }

}
