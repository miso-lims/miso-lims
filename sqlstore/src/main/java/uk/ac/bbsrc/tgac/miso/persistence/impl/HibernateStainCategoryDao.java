package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory_;
import uk.ac.bbsrc.tgac.miso.core.data.Stain_;
import uk.ac.bbsrc.tgac.miso.persistence.StainCategoryDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateStainCategoryDao extends HibernateSaveDao<StainCategory> implements StainCategoryDao {

  public HibernateStainCategoryDao() {
    super(StainCategory.class);
  }

  @Override
  public StainCategory getByName(String name) throws IOException {
    return getBy(StainCategory_.name, name);
  }

  @Override
  public long getUsage(StainCategory stainCategory) throws IOException {
    return getUsageBy(Stain.class, Stain_.category, stainCategory);
  }

  @Override
  public List<StainCategory> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList(StainCategory_.STAIN_CATEGORY_ID, ids);
  }

}
