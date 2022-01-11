package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.persistence.StainCategoryDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateStainCategoryDao extends HibernateSaveDao<StainCategory> implements StainCategoryDao {

  public HibernateStainCategoryDao() {
    super(StainCategory.class);
  }

  @Override
  public StainCategory getByName(String name) throws IOException {
    return getBy("name", name);
  }

  @Override
  public long getUsage(StainCategory stainCategory) throws IOException {
    return getUsageBy(Stain.class, "category", stainCategory);
  }

  @Override
  public List<StainCategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("stainCategoryId", ids);
  }

}
